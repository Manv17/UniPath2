package org.example.unipath2.application.statistics.numeric;

import org.example.unipath2.application.statistics.Statistic;
import org.example.unipath2.domain.course.Course;

import java.util.List;

public class BaseCalculatorStatistic implements NumericStrategy {
    private final NumericStrategy weightedAvgStatistic;

    public BaseCalculatorStatistic() {
        this.weightedAvgStatistic = new WeightedAvgStatistic();
    }

    @Override
    public double compute(List<Course> validCourses, Statistic statistic) {
        double avg = weightedAvgStatistic.compute(validCourses, statistic);
        int points = 0;
        if (statistic.getThesisPoins() != null) {
            points = statistic.getThesisPoins();
        }
        return Math.round((avg / 30.0) * 110.0) + points;
    }
}
