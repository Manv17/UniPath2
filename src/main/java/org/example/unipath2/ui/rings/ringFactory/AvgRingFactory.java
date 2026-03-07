package org.example.unipath2.ui.rings.ringFactory;

import org.example.unipath2.domain.enums.Colors;
import org.example.unipath2.application.statistics.Statistic;
import org.example.unipath2.application.statistics.numeric.AvgStatistic;
import org.example.unipath2.application.statistics.numeric.NumericStrategy;
import org.example.unipath2.ui.rings.RingCard;

public class AvgRingFactory implements RingFactory {
    private final NumericStrategy numericStrategy = new AvgStatistic();

    @Override
    public RingCard createRingCard(Statistic statistic, int diameter) {
        double avg = numericStrategy.compute(statistic.getValidPassedCourse());
        String label = "/ " + (int) statistic.getMaxAvg();
        double progress = avg / statistic.getMaxAvg();

        return new RingCard(
                Colors.PRIMARY.getColor(),
                diameter,
                String.format("%.2f", avg),
                label,
                progress
        );
    }
}
