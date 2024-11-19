package unisinos.services.ranking;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import unisinos.model.message.EdgeMessage;
import unisinos.model.enums.Decision;
import unisinos.services.providers.RunningServicesProvider;
import unisinos.util.RankingsRepository;
import java.util.List;

@ApplicationScoped
public class PriorityEvaluationService
{

    @Inject
    ServicePriorityProvider servicePriorityProvider;
    @Inject
    RunningServicesProvider runningServicesProvider;

    private static final Double USER_PRIORITY_WEIGHT = 2.0;

    public Decision shouldOffload (EdgeMessage message)
    {
        double userPriority = message.getUserPriority();
        double servicePriority = servicePriorityProvider.getPriority(message.getService());
        double addedWeight = message.getOffloadedBy().size() / 2.0;

        double ranking = this.ranking(userPriority,
                                      servicePriority,
                                      addedWeight);

        // Calculate percentiles (Q1, Q3)
        List<Double> runningServicesRankings = runningServicesProvider.getRunningServicesRankings();
        if (runningServicesRankings.isEmpty()) {
            return Decision.EXECUTE;
        }
        double[] percentiles = calculatePercentiles(runningServicesRankings);
        double lowerBound = percentiles[0]; // Q1 (25th percentile)
        double upperBound = percentiles[1]; // Q3 (75th percentile)

        if(ranking > upperBound) {
            return Decision.EXECUTE;
        } else if (ranking < lowerBound) {
            return Decision.OFFLOAD;
        }

        return Decision.INCONCLUSIVE;
    }

    public double ranking(EdgeMessage message) {
        double userPriority = message.getUserPriority();
        double servicePriority = servicePriorityProvider.getPriority(message.getService());
        double addedWeight = message.getOffloadedBy().size() / 2.0;

        return ranking(userPriority, servicePriority, addedWeight);
    }

    public double ranking (double userPriority,
                            double servicePriority,
                            double addedWeight)
    {
        return (USER_PRIORITY_WEIGHT + addedWeight) * userPriority + servicePriority;
    }

    private double[] calculatePercentiles (List<Double> rankings)
    {
        double[] rankingArray = rankings.stream().mapToDouble(Double::doubleValue).sorted()
                        .toArray();
        Percentile percentile = new Percentile();
        percentile.setData(rankingArray);

        double Q1 = percentile.evaluate(25); // 25th percentile
        double Q3 = percentile.evaluate(75); // 75th percentile

        return new double[] { Q1, Q3 };
    }

}
