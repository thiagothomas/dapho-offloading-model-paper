package unisinos.services.providers;

import io.quarkus.arc.Lock;
import jakarta.inject.Singleton;
import unisinos.model.enums.Threshold;
import unisinos.model.node.Metrics;
import unisinos.model.node.ResourcesInfo;

@Singleton
public class CurrentNodeMetricsProvider
{

    private ResourcesInfo resourcesInfo = new ResourcesInfo(Threshold.NONE, new Metrics(1.0,1.0,1.0));

    @Lock(Lock.Type.READ)
    public ResourcesInfo getResourcesInfo ()
    {
        return resourcesInfo;
    }

    @Lock(Lock.Type.WRITE)
    public void setResourcesInfo (ResourcesInfo resourcesInfo)
    {
        this.resourcesInfo = resourcesInfo;
    }

}
