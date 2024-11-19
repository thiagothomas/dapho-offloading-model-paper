package unisinos.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import unisinos.model.message.EdgeMessage;
import unisinos.services.MessageProcessingService;
import java.time.Clock;
import java.util.List;

@Path("/vital-sign")
public class VitalSignResource
{

    @Inject
    MessageProcessingService messageProcessingService;
    @Inject
    Clock clock;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void message (List<EdgeMessage> edgeMessage)
    {
        //        edgeMessage.setFirstArrivalAtNode(clock.instant());
        //        messageProcessingService.processMessage(edgeMessage);
        // Set the first arrival time of each message
        edgeMessage.forEach(message -> message.setFirstArrivalAtNode(clock.instant()));
        edgeMessage.forEach(message -> messageProcessingService.processMessage(message));
    }

}
