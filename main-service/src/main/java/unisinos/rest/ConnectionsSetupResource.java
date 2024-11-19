package unisinos.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import unisinos.model.OffloadingConnection;
import unisinos.websocket.ConnectionsManager;
import java.util.List;

@Path("/connections-setup")
public class ConnectionsSetupResource
{
    @Inject
    ConnectionsManager connectionsManager;

    @POST
    @Path("/add-connection")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addConnection (List<OffloadingConnection> connection)
    {
        // iterate list and add connections
        for (OffloadingConnection conn : connection) {
            connectionsManager.addNodeConnection(conn.nodeId(),
                                                 conn.ipAddress());
        }
        return Response.ok("Connection established with nodes: " + connection).build();
    }

    @POST
    @Path("/{nodeId}/send-message")
    public Response sendMessage (@PathParam("nodeId") String nodeId,
                                 String message)
    {
        try {
            connectionsManager.sendMessageToNode(nodeId,
                                                 message);
            return Response.ok("Mensagem enviada ao nó " + nodeId).build();
        }
        catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Erro ao enviar mensagem: " + e.getMessage()).build();
        }
    }

}
