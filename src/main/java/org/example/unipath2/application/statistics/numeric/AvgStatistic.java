package org.example.unipath2.application.statistics.numeric;

import org.example.unipath2.application.statistics.Statistic;
import org.example.unipath2.domain.course.Course;

import java.util.List;

public class AvgStatistic implements NumericStrategy {

    private final BasicAvgStatistic basicAvgStatistic = new BasicAvgStatistic();

    @Override
    public double compute(List<Course> validCourses, Statistic statistic) {
        if (validCourses.isEmpty()) {
            return 0.0;
        }
        double sumGrades = basicAvgStatistic.getSumGrades(validCourses);
        return Math.round((sumGrades / validCourses.size()) * 10.00) / 10.00;
    }
}
