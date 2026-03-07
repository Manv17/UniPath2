package org.example.unipath2.application.statistics.numeric;

import org.example.unipath2.application.statistics.Statistic;
import org.example.unipath2.domain.course.Course;

import java.util.List;

public class BasicAvgStatistic {

    public BasicAvgStatistic() {
    }

    public double getSumGrades(List<Course> validCourses) {

        if (validCourses.isEmpty())
            return 0;

        int sumGrades = 0;

        for (Course c : validCourses) {
            Integer g = c.getGrade();
            if (g != null) sumGrades += g;
        }
        return sumGrades;
    }

    public double getSumWeights(List<Course> courses) {
        double sum = 0;
        for (Course c : courses) sum += c.getGrade() * c.getCfu();
        return sum;
    }

    public double getSumCfu(List<Course> courses) {
        double sum = 0;
        for (Course c : courses) sum += c.getCfu();
        return sum;
    }
}
