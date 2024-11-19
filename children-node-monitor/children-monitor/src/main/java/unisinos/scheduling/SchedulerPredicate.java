package unisinos.scheduling;

import io.quarkus.scheduler.Scheduled.SkipPredicate;
import io.quarkus.scheduler.ScheduledExecution;
import jakarta.inject.Singleton;

@Singleton
public class SchedulerPredicate implements SkipPredicate
{

    public static boolean connectionsEstablished = false;

    @Override
    public boolean test (ScheduledExecution execution)
    {
        return !connectionsEstablished;
    }


}
