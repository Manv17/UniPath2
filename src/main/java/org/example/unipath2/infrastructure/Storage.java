package org.example.unipath2.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.unipath2.domain.career.Career;
import org.example.unipath2.domain.course.AptitudinalCourse;
import org.example.unipath2.domain.enums.CourseSemester;

import java.io.File;
import java.io.IOException;

public final class Storage {
    private static final File fileCareer = new File("career.json");
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());


    public static Career loadCareer() {
        try {
            if (!fileCareer.exists()) saveCareer(new Career());
            if (fileCareer.length() == 0) {
                System.out.println("File empty or format not valid: generating new career");
                return new Career();
            }

            Career career = mapper.readValue(fileCareer, Career.class);

            career.getCourses().replaceAll(course -> {
                if (course.getSemester() == CourseSemester.I && !(course instanceof AptitudinalCourse)) {
                    AptitudinalCourse apt = new AptitudinalCourse(
                            course.getName(),
                            course.getCfu(),
                            course.getYear(),
                            course.getSemester()
                    );
                    apt.setStatus(course.getStatus());
                    apt.setDate(course.getDate());
                    return apt;
                }
                return course;
            });

            System.out.println("Career loaded");
            return career;
        } catch (MismatchedInputException e) {
            e.printStackTrace();
            System.out.println("File empty or format not valid: generating new career");
            return new Career();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading career: " + e.getMessage());
            return null;
        }
    }

    public static void saveCareer(Career career) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(fileCareer, career);
            System.out.println("Career saved: " + career);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving career: " + e.getMessage());
        }
    }
}
