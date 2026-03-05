package org.example.unipath2.application.statistics.minmax;

import org.example.unipath2.domain.course.Course;

import java.util.List;
import java.util.Optional;

public interface CourseStrategy {
    Optional<Course> compute(List<Course> validCourses);
}
