package unisinos.services.providers;

import io.quarkus.arc.Lock;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import unisinos.model.node.FogNode;
import unisinos.model.node.Metrics;
import unisinos.model.node.ResourcesInfo;
import unisinos.model.results.OffloadingInfo;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class AvailableNodesProvider
{
    @Inject
    CurrentNodeMetricsProvider currentNodeMetricsProvider;
    private final PriorityQueue<FogNode> availableNodes = new PriorityQueue<>(Comparator.comparingDouble(this::score)
                                                                                              .reversed());

    @Lock(Lock.Type.WRITE)
    public FogNode findOffloadingNode (List<OffloadingInfo> offloadingInfo)
    {
        Set<String> offloadedBy = offloadingInfo.stream()
                                              .map(OffloadingInfo::node)
                                              .collect(Collectors.toSet());
        FogNode bestNode = null;
        PriorityQueue<FogNode> tempQueue = new PriorityQueue<>(availableNodes.comparator());

        while (!availableNodes.isEmpty()) {
            FogNode node = availableNodes.poll();
            if (!offloadedBy.contains(node.nodeId())) {
                bestNode = node;
                tempQueue.add(node);
                break;
            }
            tempQueue.add(node);
        }

        availableNodes.addAll(tempQueue);

        return bestNode;
    }

    @Lock(Lock.Type.WRITE)
    public void setAvailableNodes (List<FogNode> nodes)
    {
        this.availableNodes.clear();
        this.availableNodes.addAll(nodes);
    }

    private double score (FogNode n)
    {
        Metrics metrics = n.metrics();

        double cpuScore, memoryScore, latencyScore;
        ResourcesInfo currentMetrics = currentNodeMetricsProvider.getResourcesInfo();

        cpuScore = resourceGrowth(100 - currentMetrics.metrics().cpuUsage(),
                                  100 - metrics.cpuUsage());
        memoryScore = resourceGrowth(100 - currentMetrics.metrics().memoryUsage(),
                                     100 - metrics.memoryUsage());
        latencyScore = resourceGrowth(currentMetrics.metrics().latency(),
                                      metrics.latency());

        return cpuScore + memoryScore + latencyScore;
    }

    private double resourceGrowth (double current,
                                   double target)
    {
        return current == 0 ? target : (target - current) / current;
    }

}
