package org.example.unipath2.domain.course;

import org.example.unipath2.domain.enums.CourseSemester;
import org.example.unipath2.domain.enums.CourseStatus;
import org.example.unipath2.domain.enums.CourseType;

import java.time.LocalDate;

public class AptitudinalCourse extends Course {

    public AptitudinalCourse() {
        super();
        setType(CourseType.APTITUDINAL);
    }

    public AptitudinalCourse(String name, int cfu, int year, CourseSemester semester) {
        super(name, cfu, year, semester);
        setType(CourseType.APTITUDINAL);
    }


    public void setPassed(Boolean passed, LocalDate date) {

        super.setGrade(null, date);
        setType(CourseType.APTITUDINAL);

        if (Boolean.TRUE.equals(passed)) {
            setStatus(CourseStatus.DONE);
        } else {
            setStatus(CourseStatus.TO_DO);
        }
    }

}