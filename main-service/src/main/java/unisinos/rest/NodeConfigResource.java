package unisinos.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import unisinos.model.node.ResourcesInfo;
import unisinos.services.providers.NodeIdProvider;

@Path("/node-config")
public class NodeConfigResource
{

    @Inject
    NodeIdProvider nodeIdProvider;


    @GET
    @Path("/set-node-id/{nodeId}")
    public String postCurrentMetrics (@PathParam("nodeId") String nodeId)
    {
        nodeIdProvider.setNodeId(nodeId);
        return "Node ID set to " + nodeId;
    }


}
