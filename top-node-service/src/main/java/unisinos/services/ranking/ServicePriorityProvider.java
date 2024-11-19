package unisinos.services.ranking;

import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import unisinos.config.ServicePriorityConfig;
import unisinos.model.message.ServiceType;

@ApplicationScoped
public class ServicePriorityProvider {

    @Inject
    ServicePriorityConfig servicePriorityConfig;

    public int getPriority(ServiceType serviceType) {
        return switch (serviceType) {
            case NEWS2 -> servicePriorityConfig.news2();
            case ML_PREDICTION -> servicePriorityConfig.ml_prediction();
        };
    }
}
