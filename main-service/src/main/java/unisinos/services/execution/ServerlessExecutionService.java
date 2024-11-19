package unisinos.services.execution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.arc.Lock;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import unisinos.model.message.EdgeMessage;
import unisinos.model.results.OffloadingInfo;
import unisinos.model.results.Result;
import unisinos.rest.ResultsClient;
import unisinos.rest.ServerlessFunctionClient;
import unisinos.services.providers.RunningServicesProvider;
import unisinos.services.ranking.PriorityEvaluationService;
import unisinos.services.ranking.ServicePriorityProvider;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class ServerlessExecutionService
{
    // LOGGER
    private static final Logger LOGGER = Logger.getLogger(ServerlessExecutionService.class.getName());

    @Inject
    ThreadContext threadContext;
    @Inject
    ManagedExecutor managedExecutor;
    @Inject
    RunningServicesProvider runningServicesProvider;
    @Inject
    PriorityEvaluationService priorityEvaluationService;
    @Inject
    ServicePriorityProvider servicePriorityProvider;
    @RestClient
    ServerlessFunctionClient serverlessFunctionClient;
    @RestClient
    ResultsClient resultsClient;
    @Inject
    Clock clock;

    public void executeTaskLocally (EdgeMessage message)
    {
        String json = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            json = objectMapper.writeValueAsString(message.getVitalSign());
        }
        catch (JsonProcessingException e) {
            LOGGER.severe("Failed to serialize message");
            throw new RuntimeException(e);
        }
        LOGGER.info("Executing task with ID: " + message.getId() + " locally");
        Instant executedAt = runningServicesProvider.executionStarted(message.getId(),
                                                                      message.getService(),
                                                                      priorityEvaluationService.ranking(message));

        threadContext.withContextCapture(serverlessFunctionClient.runFunction(message.getService().getServiceName(), json))
             .thenApplyAsync(result -> {
                 LOGGER.info("Serverless function returned: "
                                             + result);
                 Duration serviceDuration = runningServicesProvider.executionFinished(message.getId());

                 sendResultsToResultsService(message,
                                             executedAt,
                                             serviceDuration);

                 LOGGER.info("Task with ID: "
                                             + message.getId()
                                             + " finished execution");
                 return null;
             }, managedExecutor);
    }

    private void sendResultsToResultsService (EdgeMessage message,
                                              Instant executedAt,
                                              Duration serviceDuration)
    {
        Integer userPriority = message.getUserPriority();
        int servicePriority = servicePriorityProvider.getPriority(message.getService());
        Instant firstArrivalAtNode = message.getFirstArrivalAtNode();
        List<OffloadingInfo> offloadingInfo = message.getOffloadedBy();

        Result result = new Result(userPriority.doubleValue(),
                                   (double)servicePriority,
                                   firstArrivalAtNode,
                                   executedAt,
                                   offloadingInfo,
                                   serviceDuration);

        LOGGER.info("Sending results to results service: " + result);
        // try until successfull
        boolean keepTrying = true;
        int retries = 0;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        while (keepTrying) {
            try (Response response = resultsClient.sendResults(objectMapper.writeValueAsString(result))) {
                keepTrying = false;
                LOGGER.info("Results sent to results service. Response: " + response.getStatus());
            }
            catch (Exception e) {
                LOGGER.severe("Failed to send results to results service");
                if (retries >= 3) {
                    throw new RuntimeException(e);
                }
                retries++;
            }
        }
    }

}
