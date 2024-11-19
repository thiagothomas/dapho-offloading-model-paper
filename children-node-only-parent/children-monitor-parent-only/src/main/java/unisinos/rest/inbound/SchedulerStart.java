package unisinos.rest.inbound;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import unisinos.scheduling.SchedulerPredicate;

@Path("/scheduler")
public class SchedulerStart
{

    @GET
    @Path("/init")
    public String connect ()
    {
        SchedulerPredicate.connectionsEstablished = true;
        return "Network Information Scheduler started";
    }

}
