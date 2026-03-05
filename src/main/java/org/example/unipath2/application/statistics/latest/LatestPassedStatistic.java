package org.example.unipath2.application.statistics.latest;

import org.example.unipath2.domain.course.Course;

import java.util.Comparator;
import java.util.List;

public class LatestPassedStatistic implements ListCourseStrategy {
    @Override
    public List<Course> compute(List<Course> validCourses) {
        return validCourses.stream()
                .sorted(Comparator.comparing(Course::getDate).reversed())
                .limit(4)
                .toList();
    }
}
