package org.example.unipath2.application.statistics.minmax;

import org.example.unipath2.domain.course.Course;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MinStatistic implements CourseStrategy {
    @Override
    public Optional<Course> compute(List<Course> validCourses) {
        return validCourses.stream()
                .filter(c -> c.getGrade() != null)
                .min(Comparator.comparingInt(Course::getGrade));
    }
}
