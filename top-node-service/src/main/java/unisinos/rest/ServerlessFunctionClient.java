package unisinos.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import java.util.concurrent.CompletionStage;

@RegisterRestClient(configKey = "serverless-function")
@Path("/function/{fn_name}")
@ApplicationScoped
public interface ServerlessFunctionClient {

    @POST
    CompletionStage<String> runFunction(@PathParam("fn_name") String fnName, String payload);

}