package unisinos.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "results-server")
@Path("/results")
@ApplicationScoped
public interface ResultsClient
{

    @POST
    Response sendResults (String payload);

}