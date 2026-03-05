package org.example.unipath2.application.functions.courses;

import org.example.unipath2.domain.course.Course;
import org.example.unipath2.domain.enums.CourseSemester;
import org.example.unipath2.domain.enums.CourseStatus;

import java.time.LocalDate;

public interface CourseFactory {
    Course build(String name,
                 int cfu,
                 int year,
                 CourseSemester semester,
                 CourseStatus status,
                 Integer grade,
                 LocalDate date);
}
