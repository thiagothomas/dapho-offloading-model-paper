package unisinos.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "erwa")
public interface ERWAConfig
{
    double alpha();
}
