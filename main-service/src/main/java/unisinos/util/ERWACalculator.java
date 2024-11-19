package unisinos.util;

import io.quarkus.arc.Lock;
import jakarta.inject.Singleton;
import unisinos.config.ERWAConfig;
import unisinos.model.message.ServiceType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ERWACalculator
{

    private final double alpha;  // Smoothing factor
    private Map<ServiceType, Double> movingAverage;  // Use a Double object to handle null (no initial value)

    public ERWACalculator (ERWAConfig config)
    {
        double alpha = config.alpha();
        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException("Alpha must be between 0 and 1.");
        }
        this.alpha = alpha;
        reset();
    }

    @Lock(value = Lock.Type.WRITE)
    public void update (ServiceType service,
                        double newValue)
    {
        Double currentAverage = movingAverage.get(service);
        if (currentAverage == Double.POSITIVE_INFINITY) {
            movingAverage.put(service,
                              newValue);
        }
        else {
            currentAverage = (1 - alpha) * currentAverage + alpha * newValue;
            movingAverage.put(service,
                              currentAverage);
        }

    }

    @Lock(value = Lock.Type.READ)
    public double getMovingAverage (ServiceType serviceType)
    {
        if (movingAverage.get(serviceType) == null) {
            return 0;
        }
        return movingAverage.get(serviceType);
    }

    @Lock(value = Lock.Type.READ)
    public List<Double> getAllMovingAverages ()
    {
        List<Double> averages = new ArrayList<>();

        for (ServiceType service : ServiceType.values()) {
            averages.add(getMovingAverage(service));
        }

        return averages;
    }

    public void reset ()
    {
        movingAverage = new ConcurrentHashMap<>();
        for (ServiceType service : ServiceType.values()) {
            movingAverage.put(service,
                              Double.POSITIVE_INFINITY);
        }
    }
}
