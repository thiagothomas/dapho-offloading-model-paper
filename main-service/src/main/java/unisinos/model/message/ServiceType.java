package unisinos.model.message;

public enum ServiceType {
    NEWS2("news2"),
    ML_PREDICTION("ml_prediction");

    private final String serviceName;

    ServiceType(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}