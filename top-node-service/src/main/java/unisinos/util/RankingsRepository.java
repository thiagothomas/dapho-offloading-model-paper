package unisinos.util;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class RankingsRepository
{

    private final Map<UUID, Double> currentRankings = new ConcurrentHashMap<>();

    public void add (UUID id, Double ranking)
    {
        currentRankings.put(id, ranking);
    }

    public void remove (UUID id)
    {
        currentRankings.remove(id);
    }

    public List<Double> getCurrentRankingsArray ()
    {
        return List.copyOf(currentRankings.values());
    }

}
