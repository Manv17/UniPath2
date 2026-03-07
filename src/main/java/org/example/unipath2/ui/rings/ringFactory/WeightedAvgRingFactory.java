package org.example.unipath2.ui.rings.ringFactory;

import org.example.unipath2.domain.enums.Colors;
import org.example.unipath2.application.statistics.numeric.NumericStrategy;
import org.example.unipath2.application.statistics.Statistic;
import org.example.unipath2.application.statistics.numeric.WeightedAvgStatistic;
import org.example.unipath2.ui.rings.RingCard;

public class WeightedAvgRingFactory implements RingFactory {
    private final NumericStrategy numericStrategy = new WeightedAvgStatistic();

    @Override
    public RingCard createRingCard(Statistic statistic, int diameter) {
        double wAvg = numericStrategy.compute(statistic.getValidPassedCourse());
        String label = "/ " + (int) statistic.getMaxAvg();
        double progress = wAvg / statistic.getMaxAvg();

        return new RingCard(
                Colors.PRIMARY.getColor(),
                diameter,
                String.format("%.2f", wAvg),
                label,
                progress
        );
    }
}
