package org.example.unipath2.ui.rings.ringFactory;

import org.example.unipath2.domain.enums.Colors;
import org.example.unipath2.application.statistics.numeric.NumericStrategy;
import org.example.unipath2.application.statistics.Statistic;
import org.example.unipath2.application.statistics.numeric.BaseCalculatorStatistic;
import org.example.unipath2.ui.rings.RingCard;

public class BaseRingFactory implements RingFactory {
    private final NumericStrategy numericStrategy = new BaseCalculatorStatistic();

    @Override
    public RingCard createRingCard(Statistic statistic, int diameter) {
        double base = numericStrategy.compute(statistic.getValidPassedCourse());
        String label = "/ " + (int) statistic.getMaxBase();
        double progress = base / statistic.getMaxBase();

        return new RingCard(
                Colors.BASE.getColor(),
                diameter,
                String.format("%.0f", base),
                label,
                progress
        );
    }
}
