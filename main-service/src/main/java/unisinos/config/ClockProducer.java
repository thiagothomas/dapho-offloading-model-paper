package unisinos.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;
import java.time.Clock;

@ApplicationScoped
public class ClockProducer {

    @Produces
    @ApplicationScoped
    public Clock systemClock() {
        return Clock.systemUTC();
    }
}