package org.example.unipath2.ui.rings.ringFactory;

import org.example.unipath2.application.statistics.Statistic;
import org.example.unipath2.ui.rings.RingCard;

public interface RingFactory {
    RingCard createRingCard(Statistic statistic, int diameter);
}
