package unisinos.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import unisinos.model.node.ResourcesInfo;
import unisinos.services.offloading.OffloadingService;
import unisinos.services.providers.CurrentNodeMetricsProvider;
import java.util.logging.Logger;

@Path("/resource-info")
public class MetricsResource
{
    // create logger
    private static final Logger LOGGER = Logger.getLogger(MetricsResource.class.getName());

    @Inject
    CurrentNodeMetricsProvider currentNodeMetricsProvider;

    @POST
    public void postCurrentMetrics (ResourcesInfo resourcesInfo)
    {
        LOGGER.info("Received metrics: " + resourcesInfo);
        currentNodeMetricsProvider.setResourcesInfo(resourcesInfo);
    }

    @GET
    public ResourcesInfo getCurrentMetrics ()
    {
        LOGGER.info("GET - Current metrics: " + currentNodeMetricsProvider.getResourcesInfo());
        return currentNodeMetricsProvider.getResourcesInfo();
    }

}
