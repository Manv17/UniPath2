package org.example.unipath2.domain.course;

import org.example.unipath2.domain.enums.CourseStatus;

import java.time.LocalDate;

public class Graduation {
    private int cfu;
    private Integer thesisPoints;
    private LocalDate graduationDate;
    private CourseStatus status;

    public Graduation() {
    }

    public Graduation(int cfu, Integer thesisPoints, LocalDate graduationDate, CourseStatus status) {
        this.cfu = cfu;
        this.thesisPoints = thesisPoints;
        this.graduationDate = graduationDate;
        this.status = status;
    }

    public int getCfu() {
        return cfu;
    }

    public void setCfu(int cfu) {
        this.cfu = cfu;
    }

    public Integer getThesisPoints() {
        return thesisPoints;
    }

    public void setThesisPoints(Integer thesisPoints) {
        this.thesisPoints = thesisPoints;
    }

    public LocalDate getGraduationDate() {
        return graduationDate;
    }

    public void setGraduationDate(LocalDate graduationDate) {
        this.graduationDate = graduationDate;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }
}
