package org.example.unipath2.infrastructure;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.unipath2.domain.career.Career;
import org.example.unipath2.domain.course.AptitudinalCourse;
import org.example.unipath2.domain.enums.CourseSemester;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Storage {
    private static final Path appDirectory = initAppDirectory();
    private static final File fileCareer = appDirectory.resolve("career.json").toFile();
    private static final int CURRENT_SCHEMA_VERSION = 2;
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static Path initAppDirectory() {
        Path appDirectory = resolveAppDirectory();
        try {
            Files.createDirectories(appDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create app data directory: " + appDirectory, e);
        }
        return appDirectory;
    }

    private static Path resolveAppDirectory() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData != null && !appData.isBlank()) {
                return Paths.get(appData, "UniPath2");
            }
        }

        if (os.contains("mac")) {
            return Paths.get(System.getProperty("user.home"), "Library", "Application Support", "UniPath2");
        }

        return Paths.get(System.getProperty("user.home"), ".unipath2");
    }

    private static JsonNode migrateIfNeeded(JsonNode root) {
        int version = root.has("schemaVersion") ? root.get("schemaVersion").asInt() : 1;

        while (version < CURRENT_SCHEMA_VERSION) {
            switch (version) {
                case 1 -> {
                    root = migrateV1ToV2((ObjectNode) root);
                    version = 2;
                }
                default -> throw new IllegalStateException("Unsupported schema version: " + version);
            }
        }

        return root;
    }

    private static JsonNode migrateV1ToV2(ObjectNode root) {
        root.put("schemaVersion", 2);

        if (!root.has("annoImmatricolazione")) {
            root.putNull("annoImmatricolazione");
        }

        return root;
    }

    public static Career loadCareer() {
        try {
            if (!fileCareer.exists()) saveCareer(new Career());
            if (fileCareer.length() == 0) {
                System.out.println("File empty or format not valid: generating new career");
                return new Career();
            }

            JsonNode root = mapper.readTree(fileCareer);
            JsonNode migratedRoot = migrateIfNeeded(root);

            if (!migratedRoot.equals(root)) {
                mapper.writerWithDefaultPrettyPrinter().writeValue(fileCareer, migratedRoot);
            }

            Career career = mapper.treeToValue(migratedRoot, Career.class);

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
            ObjectNode root = mapper.valueToTree(career);
            root.put("schemaVersion", CURRENT_SCHEMA_VERSION);
            mapper.writerWithDefaultPrettyPrinter().writeValue(fileCareer, root);
            System.out.println("Career saved: " + career);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving career: " + e.getMessage());
        }
    }
}
