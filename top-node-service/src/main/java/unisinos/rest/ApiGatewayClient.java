package unisinos.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import java.util.concurrent.CompletionStage;

@RegisterRestClient(configKey = "api-gateway")
@Path("/vital-sign")
@ApplicationScoped
public interface ApiGatewayClient
{

    @POST
    CompletionStage<Response> sendMessage (String payload);

}