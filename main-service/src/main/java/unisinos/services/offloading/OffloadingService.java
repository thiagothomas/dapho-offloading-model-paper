package unisinos.services.offloading;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import unisinos.model.message.EdgeMessage;
import unisinos.model.node.FogNode;
import unisinos.model.node.Metrics;
import unisinos.model.results.OffloadingInfo;
import unisinos.model.results.OffloadingReason;
import unisinos.services.providers.AvailableNodesProvider;
import unisinos.services.providers.NodeIdProvider;
import unisinos.websocket.ConnectionsManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class OffloadingService
{

    private final Logger logger = Logger.getLogger(OffloadingService.class.getName());

    @Inject
    ConnectionsManager connectionsManager;
    @Inject
    NodeIdProvider nodeConfig;
    @Inject
    AvailableNodesProvider availableNodesProvider;

    public void offloadTask (EdgeMessage message, OffloadingReason reason)
    {
        FogNode node = availableNodesProvider.findOffloadingNode(message.getOffloadedBy());
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            OffloadingInfo offloadingInfo = new OffloadingInfo(nodeConfig.getNodeId(),
                                                               reason);
            message.getOffloadedBy().add(offloadingInfo);
            connectionsManager.sendMessageToNode(node.nodeId(),
                                                 objectMapper.writeValueAsString(message));
        }
        catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Failed to serialize message", e);
        }
    }


}
