package unisinos;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import unisinos.dto.FogNodeDTO;
import unisinos.dto.MetricsDTO;
import unisinos.model.FogNode;
import unisinos.model.ResourcesInfo;
import unisinos.model.Threshold;
import unisinos.repository.FogNodesRepository;
import unisinos.rest.outbound.NodeMetricsClient;
import unisinos.websocket.ConnectionsManager;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class NodeMetricsService
{

    private static final Logger LOGGER = Logger.getLogger(NodeMetricsService.class.getName());

    @Inject
    FogNodesRepository fogNodesRepository;

    @Inject
    ConnectionsManager connectionsManager;

    ObjectMapper objectMapper = new ObjectMapper();

    public void evaluateNetworkInformation ()
    {
        Map<FogNode, NodeMetricsClient> nodesClients = getNodesClientsFromNodes(fogNodesRepository.getFogNodes());
        Set<FogNode> availableNodes = evaluateMetricsFromNodes(nodesClients);
        sendMessageToAllNodes(availableNodes, fogNodesRepository.getFogNodes());
    }

    private Set<FogNode> evaluateMetricsFromNodes (Map<FogNode, NodeMetricsClient> nodesClients)
    {
        Set<FogNode> availableNodes = new HashSet<>();
        nodesClients.forEach((fogNode, nodeMetricsClient) -> {
            try {
                ResourcesInfo resourcesInfo = nodeMetricsClient.getMetrics();
                if (resourcesInfo.threshold() == Threshold.NONE || fogNode.getCurrentNode()) {
                    availableNodes.add(fogNode);
                    fogNode.setMetrics(resourcesInfo.metrics());
                }
                LOGGER.info("Metrics from " + fogNode.getNodeId() + ": " + resourcesInfo);

            }
            catch (Exception e) {
                LOGGER.log(Level.SEVERE,
                           "Failed to fetch metrics from " + fogNode.getNodeId(),
                           e);
            }
        });
        return availableNodes;
    }

    private void sendMessageToAllNodes (Set<FogNode> availableNodes,
                                        List<FogNode> fogNodes)
    {
        if (availableNodes.isEmpty()) {
            LOGGER.warning("No available nodes to send metrics.");
            return;
        }

        try (Jsonb jsonb = JsonbBuilder.create()) {
            fogNodes.parallelStream().forEach(node -> sendMetricsToNode(node,
                                                                        availableNodes,
                                                                        jsonb));
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                       "Failed to send metrics",
                       e);
        }
    }

    private void sendMetricsToNode (FogNode node,
                                    Set<FogNode> allNodes,
                                    Jsonb jsonb)
    {
        if (node.getCurrentNode()) {
            return;
        }

        Set<FogNode> otherNodes = new HashSet<>(allNodes);
        otherNodes.removeIf(fogNode -> !fogNode.getCurrentNode());

        List<FogNodeDTO> nodeIds = otherNodes.stream().map(this::createFogNodeDTO)
                        .collect(Collectors.toList());

        LOGGER.log(Level.INFO,
                   "Sending metrics to node {0}: {1}",
                   new Object[] { node.getNodeId(), nodeIds });
        try {
            connectionsManager.sendMessage(node.getNodeId(),
                                           jsonb.toJson(nodeIds));
            LOGGER.info("Metrics sent to " + node.getNodeId());
        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE,
                       "Failed to send metrics to " + node.getNodeId(),
                       e);
        }
    }

    private Map<FogNode, NodeMetricsClient> getNodesClientsFromNodes (List<FogNode> fogNodes)
    {
        return fogNodes.stream().collect(Collectors.toConcurrentMap(fogNode -> fogNode,
                                                                    this::getNodeClient));
    }

    private NodeMetricsClient getNodeClient (FogNode fogNode)
    {
        return RestClientBuilder.newBuilder()
                        .baseUri(URI.create(fogNode.getRestResourceMonitorAddress()))
                        .build(NodeMetricsClient.class);
    }

    private FogNodeDTO createFogNodeDTO (FogNode fogNode)
    {
        return new FogNodeDTO(fogNode.getNodeId(),
                              new MetricsDTO(fogNode.getMetrics().cpuUsage(),
                                             fogNode.getMetrics().memoryUsage(),
                                             fogNode.getMetrics().latency()));
    }
}
