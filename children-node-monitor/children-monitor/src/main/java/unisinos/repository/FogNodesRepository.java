package unisinos.repository;

import io.quarkus.arc.Lock;
import jakarta.inject.Singleton;
import unisinos.model.FogNode;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class FogNodesRepository
{

    private final List<FogNode> fogNodes = new ArrayList<>();

    @Lock(Lock.Type.WRITE)
    public void addFogNode (FogNode fogNode)
    {
        fogNodes.add(fogNode);
    }

    @Lock(Lock.Type.WRITE)
    public void removeFogNode (FogNode fogNode)
    {
        fogNodes.remove(fogNode);
    }

    @Lock(Lock.Type.READ)
    public List<FogNode> getFogNodes ()
    {
        return fogNodes;
    }

}
