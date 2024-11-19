package unisinos.services.providers;

import io.quarkus.arc.Lock;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import unisinos.model.execution.ExecutionWithDuration;
import unisinos.model.execution.ServiceExecution;
import unisinos.model.message.ServiceType;
import unisinos.util.ERWACalculator;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@ApplicationScoped
@Lock
public class RunningServicesProvider
{

    @Inject ERWACalculator erwaCalculator;
    @Inject Clock clock;

    private final Map<UUID, ExecutionWithDuration> services = new ConcurrentHashMap<>();

    @Lock(value = Lock.Type.WRITE)
    public Instant executionStarted (UUID id,
                                     ServiceType service,
                                     double ranking)
    {
        var executionStart = clock.instant();
        services.put(id,
                     new ExecutionWithDuration(new ServiceExecution(service,
                                                                    ranking),
                                               executionStart));
        return executionStart;
    }

    @Lock(value = Lock.Type.WRITE)
    public Duration executionFinished (UUID id)
    {
        Duration result = Optional.ofNullable(services.get(id)).map(execution -> {
            var now = clock.instant();
            Duration between = Duration.between(execution.executionStart(),
                                                now);
            updateDuration(between,
                           execution.execution().serviceName());
            return between;
        }).orElse(null);

        services.remove(id);
        return result;
    }

    @Lock(value = Lock.Type.READ)
    public List<Double> getRunningServicesRankings()
    {
        return List.copyOf(services.values().stream().map(ExecutionWithDuration::execution).map(ServiceExecution::ranking).toList());
    }

    private void updateDuration (Duration duration,
                                 ServiceType serviceType)
    {
        this.erwaCalculator.update(serviceType, duration.toMillis());
    }

}