package unisinos.services.providers;

import io.quarkus.arc.Lock;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Singleton;

@Singleton
public class NodeIdProvider
{

    private String nodeId;


    @Lock(Lock.Type.READ)
    public String getNodeId ()
    {
        return nodeId;
    }

    @Lock(Lock.Type.WRITE)
    public void setNodeId (String nodeId)
    {
        this.nodeId = nodeId;
    }

}
