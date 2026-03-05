package org.example.unipath2.application.statistics.latest;

import org.example.unipath2.domain.course.Course;

import java.util.List;

public interface ListCourseStrategy {
    List<Course> compute(List<Course> validCourses);

}
