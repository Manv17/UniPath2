package org.example.unipath2.application.statistics.avg;

import org.example.unipath2.domain.course.Course;

import java.util.List;

public interface NumericStrategy {

    double compute(List<Course> validCourses);
}
