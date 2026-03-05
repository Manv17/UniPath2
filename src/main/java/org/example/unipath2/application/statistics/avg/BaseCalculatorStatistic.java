package org.example.unipath2.application.statistics.avg;

import org.example.unipath2.domain.course.Course;

import java.util.List;

public class BaseCalculatorStatistic implements NumericStrategy {
    private final NumericStrategy weightedAvgStatistic;

    public BaseCalculatorStatistic() {
        this.weightedAvgStatistic = new WeightedAvgStatistic();
    }
    
    @Override
    public double compute(List<Course> validCourses) {
        double avg = weightedAvgStatistic.compute(validCourses);
        return Math.round((avg / 30.0) * 110.0);
    }
}
