package unisinos.model.results;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public record Result(Double userPriority, Double servicePriority, Instant firstArrivalAtNode, Instant executedAt, List<OffloadingInfo> offloadedBy, Duration serviceDuration)
{
}
