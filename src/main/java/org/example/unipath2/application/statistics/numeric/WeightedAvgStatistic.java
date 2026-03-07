package org.example.unipath2.application.statistics.numeric;

import org.example.unipath2.domain.course.Course;

import java.util.List;

public class WeightedAvgStatistic implements NumericStrategy {

    private final BasicAvgStatistic basicAvgStatistic = new BasicAvgStatistic();

    @Override
    public double compute(List<Course> validCourses) {

        if (validCourses.isEmpty()) {
            return 0.0;
        }

        double sumWeights = basicAvgStatistic.getSumWeights(validCourses);
        double sumCfu = basicAvgStatistic.getSumCfu(validCourses);

        if (sumWeights == 0 || sumCfu == 0) {
            return 0.0;
        }

        return Math.round((sumWeights / sumCfu) * 100.0) / 100.0;
    }
}
