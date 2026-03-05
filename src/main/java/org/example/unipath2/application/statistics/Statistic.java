package org.example.unipath2.application.statistics;

import org.example.unipath2.domain.career.Career;
import org.example.unipath2.domain.course.Course;
import org.example.unipath2.domain.enums.CourseType;

import java.util.List;
import java.util.stream.Collectors;

public class Statistic {

    private static final double MAX_AVG = 30;
    private static final double MAX_BASE = 110;
    private double TOTAL_CFU;

    private final List<Course> courses;
    private Career career;

    public Statistic(List<Course> courses, Career career) {
        this.courses = courses;
        this.career = career;
        this.TOTAL_CFU = career.getTOTAL_CFU();
    }

    public List<Course> getPassedCourse() {
        return courses.stream()
                .filter(Course::isPassed)
                .toList();
    }

    public List<Course> getValidPassedCourse() {
        return courses.stream()
                .filter(course -> course.isPassed() && course.getType().equals(CourseType.GRADED))
                .collect(Collectors.toList());
    }

    public int getCntPassedCourse() {
        return (int) courses.stream()
                .filter(Course::isPassed)
                .count();
    }

    public int getCntPlannedCourse() {
        return (int) courses.stream()
                .filter(Course::isPlanned)
                .count();
    }

    public int getCntToDOCourse() {
        return (int) courses.stream()
                .filter(Course::isToDo)
                .count();
    }

    public int getEarnedCFU() {
        int cfu = 0;
        for (Course c : getPassedCourse()) {
            cfu += c.getCfu();
        }
        return cfu;
    }


    private double getValidCfu() {
        double validCfu = 0;

        for (Course c : getValidPassedCourse()) {
            validCfu += c.getCfu();
        }

        return validCfu;
    }

    public double getCfuProgress() {
        return getEarnedCFU() / TOTAL_CFU;
    }

    public double getCfuProgressText() {
        double progress = getEarnedCFU() / TOTAL_CFU;
        return Math.round(progress * 100.0);
    }

    public double getAvgProgress() {
        return getEarnedCFU() / TOTAL_CFU;
    }

    public double getMaxAvg() {
        return MAX_AVG;
    }

    public double getMaxBase() {
        return MAX_BASE;
    }

    public double getTotalCfu() {
        return TOTAL_CFU;
    }

}
