package org.example.unipath2.application.functions.courses;

import org.example.unipath2.domain.course.Course;
import org.example.unipath2.domain.enums.CourseSemester;
import org.example.unipath2.domain.enums.CourseStatus;

import java.time.LocalDate;

public class GradedCourseFactory implements CourseFactory {
    @Override
    public Course build(String name,
                        int cfu,
                        int year,
                        CourseSemester semester,
                        CourseStatus status,
                        Integer grade,
                        LocalDate date) {
        Course course;
        course = new Course(name, cfu, year, semester);

        if (status == CourseStatus.SUPERATO) {
            if (grade == null || date == null) {
                throw new IllegalArgumentException("DONE requires grade and date");
            }
            course.setGrade(grade, date);
        } else if (date != null) {
            course.setDate(date);
        }
        course.setStatus(status);
        return course;
    }
}
