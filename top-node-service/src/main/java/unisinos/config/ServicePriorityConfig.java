package unisinos.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "service.priority")
public interface ServicePriorityConfig
{
    Integer news2 ();
    Integer ml_prediction ();
}
