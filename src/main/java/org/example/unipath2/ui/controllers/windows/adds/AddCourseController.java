package org.example.unipath2.ui.controllers.windows.adds;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.unipath2.domain.career.CareerActions;
import org.example.unipath2.application.functions.courses.GradedCourseFactory;
import org.example.unipath2.application.functions.forms.FormValidator;
import org.example.unipath2.ui.controllers.windows.WindowController;
import org.example.unipath2.domain.course.AptitudinalCourse;
import org.example.unipath2.domain.course.Course;
import org.example.unipath2.domain.enums.CourseSemester;
import org.example.unipath2.domain.enums.CourseStatus;
import org.example.unipath2.application.exception.DuplicateCourseException;

import java.time.LocalDate;

public class AddCourseController extends WindowController {

    @FXML
    public TextField nameField;
    @FXML
    public ChoiceBox<Integer> cfuChoice;
    @FXML
    public ChoiceBox<Integer> yearChoice;
    @FXML
    public ChoiceBox<CourseSemester> semesterChoice;
    @FXML
    public ChoiceBox<CourseStatus> statusChoice;
    @FXML
    public ChoiceBox<Integer> gradeChoice;
    @FXML
    public DatePicker datePicker;
    @FXML
    public Label gradeLabel;

    CareerActions actions = new CareerActions();

    GradedCourseFactory courseFactory = new GradedCourseFactory();

    @Override
    public void setCourseToEdit(Course course) {

    }

    @Override
    public void setAptitudinalToEdit(AptitudinalCourse course) {

    }

    @Override
    public void onContextSet() {
        refreshUI();
    }

    @Override
    public void refreshUI() {
        initializeCfuChoice();
        initializeSemesterChoice();
        initializeYearChoice();
        initializeStatusGradeChoice();
    }

    private void initializeCfuChoice() {
        for (int i = 3; i <= 12; i++) {
            cfuChoice.getItems().add(i);
        }
        cfuChoice.getSelectionModel().select(3);
    }

    private void initializeYearChoice() {
        for (int i = 1; i <= 3; i++) {
            yearChoice.getItems().add(i);
        }
        yearChoice.getSelectionModel().select(0);
    }

    private void initializeSemesterChoice() {
        semesterChoice.getItems().addAll(CourseSemester.values());
        semesterChoice.getSelectionModel().select(CourseSemester.PRIMO);
    }

    private void initializeStatusGradeChoice() {
        statusChoice.getItems().addAll(CourseStatus.values());
        statusChoice.getSelectionModel().select(CourseStatus.DA_FARE);

        for (int i = 18; i <= 30; i++) {
            gradeChoice.getItems().add(i);
        }
        gradeChoice.setDisable(true);
        gradeLabel.setDisable(true);

        statusChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldS, newS) -> {
            boolean done = (newS == CourseStatus.SUPERATO);
            gradeChoice.setDisable(!done);
            gradeLabel.setDisable(!done);
            if (!done) {
                gradeChoice.getSelectionModel().clearSelection();
            }
        });
    }

    public void handleUndoButton(ActionEvent event) {
        closePage(event);
    }

    public void handleAddButton(ActionEvent event) {
        String name = nameField.getText();
        Integer cfu = cfuChoice.getValue();
        Integer year = yearChoice.getValue();
        CourseSemester semester = semesterChoice.getValue();
        CourseStatus status = statusChoice.getValue();
        Integer grade = null;
        if (!gradeChoice.isDisabled()) {
            grade = gradeChoice.getValue();
        }
        LocalDate date = datePicker.getValue();

        resetFieldStyles();

        if (!validateForm(name, cfu, year, semester, status, grade, date)) {
            return;
        }

        Course newCourse = courseFactory.build(name, cfu, year, semester, status, grade, date);

        try {
            actions.addCourse(career, newCourse);
        } catch (DuplicateCourseException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Corso già presente");
            alert.setHeaderText("");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        closePage(event);
    }

    private void resetFieldStyles() {
        nameField.setStyle("");
        cfuChoice.setStyle("");
        yearChoice.setStyle("");
        semesterChoice.setStyle("");
        statusChoice.setStyle("");
        gradeChoice.setStyle("");
        datePicker.setStyle("");
    }

    private boolean validateForm(String name,
                                 Integer cfu,
                                 Integer year,
                                 CourseSemester semester,
                                 CourseStatus courseStatus,
                                 Integer grade,
                                 LocalDate date) {

        FormValidator validator = new FormValidator();

        validator.validateNotBlank(name, nameField)
                .validateNotNull(cfu, cfuChoice)
                .validateNotNull(year, yearChoice)
                .validateNotNull(semester, semesterChoice)
                .validateNotNull(courseStatus, statusChoice);

        if (courseStatus == CourseStatus.SUPERATO) {
            validator.validateNotNull(grade, gradeChoice)
                    .validateNotNull(date, datePicker);
        }

        if (courseStatus == CourseStatus.PIANIFICATO) {
            validator.validateNotNull(date, datePicker);
        }

        return validator.isValid();
    }
}
