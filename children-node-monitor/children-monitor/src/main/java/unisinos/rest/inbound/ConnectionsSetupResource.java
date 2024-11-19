package unisinos.rest.inbound;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import unisinos.model.FogNode;
import unisinos.repository.FogNodesRepository;
import unisinos.websocket.ConnectionsManager;
import java.util.List;
import java.util.Set;

@Path("/connections-setup")
public class ConnectionsSetupResource
{
    @Inject
    ConnectionsManager connectionsManager;
    @Inject
    FogNodesRepository fogNodeRepository;

    @POST
    @Path("/add-connection")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response addConnection (List<FogNode> fogNodes)
    {
        for (FogNode fogNode : fogNodes) {
            if (fogNode.getWsProvideNetworkInfoAddress() != null) {
                connectionsManager.addNodeConnection(fogNode.getNodeId(),
                                                     fogNode.getWsProvideNetworkInfoAddress());
            }
            fogNodeRepository.addFogNode(fogNode);
        }

        return Response.ok("Connection established with nodes " + fogNodes).build();
    }

    @POST
    @Path("/{nodeId}/send-message")
    public Response sendMessage (@PathParam("nodeId") String nodeId,
                                 String message)
    {
        try {
            connectionsManager.sendMessage(nodeId, message);
            return Response.ok("Mensagem enviada ao nó " + nodeId).build();
        }
        catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Erro ao enviar mensagem: " + e.getMessage()).build();
        }
    }

}
