package org.example.unipath2.domain.course;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.example.unipath2.domain.enums.CourseSemester;
import org.example.unipath2.domain.enums.CourseStatus;
import org.example.unipath2.domain.enums.CourseType;

import java.time.LocalDate;
import java.util.Objects;

public class Course {

    private String name;
    private int cfu;
    private int year;
    private CourseSemester semester;
    private CourseStatus status;
    private Integer grade;
    private LocalDate date;
    private CourseType type;

    public Course() {
    }

    public Course(String name, int cfu, int year, CourseSemester semester) {
        this.name = name;
        this.cfu = cfu;
        this.year = year;
        this.semester = semester;
        this.type = CourseType.GRADED;
    }

    public String getName() {
        return name;
    }

    public int getCfu() {
        return cfu;
    }

    public int getYear() {
        return year;
    }

    public CourseSemester getSemester() {
        return semester;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade, LocalDate date) {
        this.grade = grade;
        this.date = date;
        setStatusFromGrade(grade);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStatusFromGrade(Integer grade) {
        if (grade != null && grade >= 18) {
            this.status = CourseStatus.DONE;
        } else {
            this.status = CourseStatus.TO_DO;
        }
    }

    @JsonIgnore
    public boolean isPassed() {
        return status.equals(CourseStatus.DONE);
    }

    @JsonIgnore
    public boolean isPlanned() {
        return status.equals(CourseStatus.PLANNED);
    }

    @JsonIgnore
    public boolean isToDo() {
        return status.equals(CourseStatus.TO_DO);
    }

    public CourseType getType() {
        return type;
    }

    public void setType(CourseType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return cfu == course.cfu && Objects.equals(name, course.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cfu);
    }

    @Override
    public String toString() {
        return "Course{" +
                "name='" + name + '\'' +
                ", cfu=" + cfu +
                ", year=" + year +
                ", semester=" + semester +
                ", status=" + status +
                ", grade=" + grade +
                ", date=" + date +
                ", type=" + type +
                '}';
    }

}
