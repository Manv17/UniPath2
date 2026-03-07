package org.example.unipath2.domain.career;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.example.unipath2.domain.course.Course;
import org.example.unipath2.domain.course.Graduation;
import org.example.unipath2.domain.enums.CourseType;
import org.example.unipath2.domain.enums.DegreeType;
import org.example.unipath2.application.exception.DuplicateCourseException;

import java.util.*;

public class Career implements Subject {
    private final List<Course> courses;
    private Graduation graduation;
    @JsonIgnore
    private final Set<Observer> observers = new HashSet<>();
    @JsonIgnore
    private int TOTAL_CFU;

    private DegreeType degreeType = DegreeType.TRIENNALE;

    public Career() {
        this.courses = new ArrayList<>();
        this.graduation = null;
        this.setTOTAL_CFU(180);
    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : new ArrayList<>(observers)) {
            o.update(this);
        }
    }

    public double getTOTAL_CFU() {
        return TOTAL_CFU;
    }

    public void setTOTAL_CFU(int TOTAL_CFU) {
        this.TOTAL_CFU = TOTAL_CFU;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void addCourse(Course newCourse) throws DuplicateCourseException {
        if (newCourse == null) return;

        for (Course course : courses) {
            if (course.equals(newCourse)) {
                if (newCourse.getType() == CourseType.GRADED) {
                    throw new DuplicateCourseException("Corso " + newCourse.getName() + " già esistente!");
                } else {
                    throw new DuplicateCourseException("Idoneità " + newCourse.getName() + " già esistente!");
                }
            }
        }

        courses.add(newCourse);
        notifyObservers();
    }

    public void removeCourse(Course c) {
        if (courses.remove(c)) {
            notifyObservers();
        }
    }

    public DegreeType getDegreeType() {
        return degreeType != null ? degreeType : DegreeType.TRIENNALE;
    }

    public void setDegreeType(DegreeType degreeType) {
        this.degreeType = (degreeType != null) ? degreeType : DegreeType.TRIENNALE;
        if (degreeType == DegreeType.TRIENNALE) {
            setTOTAL_CFU(180);
        } else {
            setTOTAL_CFU(120);
        }
        notifyObservers();
    }

    public Graduation getGraduation() {
        return graduation;
    }

    public void setGraduation(Graduation graduation) {
        this.graduation = graduation;
    }

    public void resetCareer() {
        courses.clear();
        notifyObservers();
    }
}
