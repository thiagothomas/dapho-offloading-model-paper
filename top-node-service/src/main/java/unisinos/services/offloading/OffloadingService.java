package unisinos.services.offloading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import unisinos.model.message.EdgeMessage;
import unisinos.model.results.OffloadingInfo;
import unisinos.model.results.OffloadingReason;
import unisinos.rest.ApiGatewayClient;
import unisinos.services.providers.NodeIdProvider;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class OffloadingService
{

    private final Logger logger = Logger.getLogger(OffloadingService.class.getName());

    @Inject
    NodeIdProvider nodeConfig;
    @Inject
    @RestClient
    ApiGatewayClient apiGatewayClient;

    public void offloadTask (EdgeMessage message,
                             OffloadingReason reason)
    {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            OffloadingInfo offloadingInfo = new OffloadingInfo(nodeConfig.getNodeId(),
                                                               reason);
            message.getOffloadedBy().add(offloadingInfo);
            apiGatewayClient.sendMessage(objectMapper.writeValueAsString(message));
        }
        catch (JsonProcessingException e) {
            logger.log(Level.SEVERE,
                       "Failed to serialize message",
                       e);
        }
    }

}
