package unisinos.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import unisinos.model.message.EdgeMessage;
import unisinos.model.enums.Decision;
import unisinos.model.results.OffloadingReason;
import unisinos.services.duration.DurationEvaluationService;
import unisinos.model.enums.Threshold;
import unisinos.services.execution.ServerlessExecutionService;
import unisinos.services.offloading.OffloadingService;
import unisinos.services.providers.CurrentNodeMetricsProvider;
import unisinos.services.ranking.PriorityEvaluationService;
import java.util.logging.Logger;

@ApplicationScoped
public class MessageProcessingService
{
    // LOGGER
    private static final Logger LOGGER = Logger.getLogger(MessageProcessingService.class.getName());

    @Inject
    PriorityEvaluationService priorityEvaluationService;
    @Inject
    DurationEvaluationService durationEvaluationService;
    @Inject
    OffloadingService offloadingService;
    @Inject
    ServerlessExecutionService serverlessExecutionService;
    @Inject
    CurrentNodeMetricsProvider currentNodeMetricsProvider;

    public void processMessage (EdgeMessage message)
    {
        OffloadingReason offloadingReason = shouldOffload(message);
        if (offloadingReason != null) {
            LOGGER.info("Offloading task: " + message);
            offloadingService.offloadTask(message, offloadingReason);
        }
        else {
            LOGGER.info("Executing task locally: " + message);
            serverlessExecutionService.executeTaskLocally(message);
        }
    }

    OffloadingReason shouldOffload (EdgeMessage message)
    {
        Threshold threshold = getThresholds();

        if (threshold == Threshold.UPPER) {
            return OffloadingReason.UPPER_THRESHOLD;
        }
        else if (threshold == Threshold.LOWER) {
            Decision decision = priorityEvaluationService.shouldOffload(message);
            if (decision == Decision.OFFLOAD) {
                return OffloadingReason.RANKING;
            }
            else if (decision == Decision.EXECUTE) {
                return null;
            }

            decision = durationEvaluationService.shouldOffload(message.getService());

            if (decision == Decision.OFFLOAD) {
                return OffloadingReason.SERVICE_DURATION;
            }
            else if (decision == Decision.INCONCLUSIVE) {
                return OffloadingReason.INCONCLUSIVE;
            }
        }

        return null;
    }

    private Threshold getThresholds ()
    {
        return currentNodeMetricsProvider.getResourcesInfo().threshold();
    }

}
