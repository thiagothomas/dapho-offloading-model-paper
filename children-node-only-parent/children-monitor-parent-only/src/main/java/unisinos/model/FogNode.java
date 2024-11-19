package unisinos.model;

public class FogNode
{

    private String nodeId;
    private String wsProvideNetworkInfoAddress;
    private String restResourceMonitorAddress;
    private Metrics metrics;
    private Boolean currentNode;

    public FogNode() {

    }

    public FogNode (String nodeId,
                    String wsProvideNetworkInfoAddress,
                    String restResourceMonitorAddress,
                    Metrics metrics,
                    Boolean currentNode)
    {
        this.nodeId = nodeId;
        this.wsProvideNetworkInfoAddress = wsProvideNetworkInfoAddress;
        this.restResourceMonitorAddress = restResourceMonitorAddress;
        this.metrics = metrics;
        this.currentNode = currentNode;
    }

    public String getNodeId ()
    {
        return nodeId;
    }

    public void setNodeId (String nodeId)
    {
        this.nodeId = nodeId;
    }

    public Metrics getMetrics ()
    {
        return metrics;
    }

    public void setMetrics (Metrics metrics)
    {
        this.metrics = metrics;
    }

    public String getRestResourceMonitorAddress ()
    {
        return restResourceMonitorAddress;
    }

    public void setRestResourceMonitorAddress (String restResourceMonitorAddress)
    {
        this.restResourceMonitorAddress = restResourceMonitorAddress;
    }

    public String getWsProvideNetworkInfoAddress ()
    {
        return wsProvideNetworkInfoAddress;
    }


    public void setWsProvideNetworkInfoAddress (String wsProvideNetworkInfoAddress)
    {
        this.wsProvideNetworkInfoAddress = wsProvideNetworkInfoAddress;
    }

    public Boolean getCurrentNode ()
    {
        return currentNode;
    }

    public void setCurrentNode (Boolean currentNode)
    {
        this.currentNode = currentNode;
    }
}
