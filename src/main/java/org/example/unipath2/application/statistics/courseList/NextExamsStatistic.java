package org.example.unipath2.application.statistics.courseList;

import org.example.unipath2.domain.course.Course;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class NextExamsStatistic implements ListCourseStrategy {
    @Override
    public List<Course> compute(List<Course> validCourses) {
        return validCourses.stream()
                .filter(c -> c.getDate() != null)
                .filter(Course::isPlanned)
                .filter(c -> !c.getDate().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(Course::getDate))
                .limit(3)
                .toList();
    }
}
