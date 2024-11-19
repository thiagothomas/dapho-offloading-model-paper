package unisinos.scheduling;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import unisinos.NodeMetricsService;
import java.util.ArrayList;
import java.util.logging.Logger;

@ApplicationScoped
public class NetworkInformatioScheduler
{
    private static final Logger LOGGER = Logger.getLogger(NetworkInformatioScheduler.class.getName());

    @Inject
    NodeMetricsService nodeMetricsService;

    @Scheduled(every = "10s", skipExecutionIf = SchedulerPredicate.class)
    void retrieveNetworkInformation() {
        LOGGER.info("Retrieving network information");
        nodeMetricsService.evaluateNetworkInformation();
    }

}
