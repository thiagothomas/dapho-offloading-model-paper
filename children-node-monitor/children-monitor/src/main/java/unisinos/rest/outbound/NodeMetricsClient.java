package unisinos.rest.outbound;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import unisinos.model.Metrics;
import unisinos.model.ResourcesInfo;

@Path("/node-metrics")
@RegisterRestClient
public interface NodeMetricsClient
{

    @GET
    ResourcesInfo getMetrics ();
}