package org.example.unipath2.domain.career;


import org.example.unipath2.domain.course.Course;
import org.example.unipath2.domain.enums.CourseStatus;
import org.example.unipath2.application.exception.DuplicateCourseException;
import org.example.unipath2.infrastructure.Storage;

import java.time.LocalDate;
import java.util.Objects;

public final class CareerActions {

    public Career load() {
        return Objects.requireNonNullElseGet(Storage.loadCareer(), Career::new);
    }

    public void deleteCourse(Career career, Course course) {
        career.removeCourse(course);
        Storage.saveCareer(career);
    }

    public void editCourse(Career career, Course course,
                           CourseStatus newStatus, LocalDate newDate, Integer newGrade) {

        if (newStatus == CourseStatus.DONE) {
            course.setGrade(newGrade, newDate);
        } else {
            course.setGrade(null, newDate);
        }

        course.setStatus(newStatus);
        course.setDate(newDate);

        Storage.saveCareer(career);
        career.notifyObservers();
    }

    public void addCourse(Career career, Course course) throws DuplicateCourseException {
        career.addCourse(course);
        Storage.saveCareer(career);
    }

    public void saveCareer(Career career) {
        Storage.saveCareer(career);
        career.notifyObservers();
    }
}
