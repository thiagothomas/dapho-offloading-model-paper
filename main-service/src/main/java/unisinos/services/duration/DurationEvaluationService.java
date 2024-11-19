package unisinos.services.duration;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import unisinos.model.message.ServiceType;
import unisinos.model.enums.Decision;
import unisinos.util.ERWACalculator;
import java.util.List;

@ApplicationScoped
public class DurationEvaluationService
{

    @Inject
    ERWACalculator erwaCalculator;

    public Decision shouldOffload (ServiceType neededService)
    {
        double neededServiceDuration = erwaCalculator.getMovingAverage(neededService);
        double median = calculateMedian(erwaCalculator.getAllMovingAverages());

        if (neededServiceDuration == Double.POSITIVE_INFINITY) {
            return Decision.EXECUTE;
        } else if (neededServiceDuration > median) {
            return Decision.OFFLOAD;
        }
        else if (neededServiceDuration < median) {
            return Decision.EXECUTE;
        }
        else {
            return Decision.INCONCLUSIVE;
        }
    }

    private double calculateMedian (List<Double> rankings)
    {
        double[] durationsArray = rankings.stream().mapToDouble(Double::doubleValue).sorted()
                        .distinct().toArray();
        Median median = new Median();
        median.setData(durationsArray);
        return median.evaluate();
    }

}
