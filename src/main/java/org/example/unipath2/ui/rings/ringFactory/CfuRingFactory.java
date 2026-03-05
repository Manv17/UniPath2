package org.example.unipath2.ui.rings.ringFactory;

import org.example.unipath2.domain.enums.Colors;
import org.example.unipath2.application.statistics.Statistic;
import org.example.unipath2.ui.rings.RingCard;

public class CfuRingFactory implements RingFactory {
    @Override
    public RingCard createRingCard(Statistic statistic, int diameter) {
        return new RingCard(
                Colors.CFU.getColor(),
                diameter,
                String.valueOf(statistic.getEarnedCFU()),
                "/ " + (int) statistic.getTotalCfu(),
                statistic.getCfuProgress());
    }
}