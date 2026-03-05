package org.example.unipath2.application.functions.courses;

import org.example.unipath2.domain.course.AptitudinalCourse;
import org.example.unipath2.domain.course.Course;
import org.example.unipath2.domain.enums.CourseSemester;
import org.example.unipath2.domain.enums.CourseStatus;

import java.time.LocalDate;

public class AptitudinalCourseFactory implements CourseFactory {
    @Override
    public Course build(String name, int cfu, int year, CourseSemester semester, CourseStatus status, Integer grade, LocalDate date) {
        AptitudinalCourse course;
        course = new AptitudinalCourse(name, cfu, year, semester);

        if (status == CourseStatus.DONE && date == null) {
            throw new IllegalArgumentException("DONE requires a date");
        }

        if (status == CourseStatus.DONE) {
            course.setPassed(true, date);
        } else if (date != null) {
            course.setDate(date);
        }
        course.setStatus(status);
        return course;
    }
}
