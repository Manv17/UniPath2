package org.example.unipath2.ui.controllers.windows.edits;

import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import org.example.unipath2.domain.career.CareerActions;
import org.example.unipath2.application.functions.forms.FormValidator;
import org.example.unipath2.ui.controllers.pages.LibrettoController;
import org.example.unipath2.ui.controllers.windows.WindowController;
import org.example.unipath2.domain.course.AptitudinalCourse;
import org.example.unipath2.domain.course.Course;
import org.example.unipath2.domain.enums.CourseStatus;

import java.time.LocalDate;

public class EditCourseController extends WindowController {

    @FXML public Label nameLabel;
    @FXML public ChoiceBox<CourseStatus> statusChoice;
    @FXML public ChoiceBox<Integer> gradeChoice;
    @FXML public DatePicker datePicker;
    @FXML public Label gradeLabel;

    private Course course;
    private final CareerActions actions = new CareerActions();

    private boolean uiInitialized = false;

    private LibrettoController librettoController;

    public void setLibrettoController(LibrettoController librettoController) {
        this.librettoController = librettoController;
    }

    @Override
    public void setAptitudinalToEdit(AptitudinalCourse course) {
        throw new UnsupportedOperationException(
                "EditCourseController does not support AptitudinalCourse"
        );
    }

    @Override
    public void setCourseToEdit(Course course) {
        this.course = course;
        if (uiInitialized && statusChoice != null) {
            populateFieldsFromCourse();
        }
    }

    @Override
    public void onContextSet() {
        refreshUI();
    }

    @Override
    public void refreshUI() {
        initializeStatusGradeChoice();
        populateFieldsFromCourse();
    }

    private void initializeStatusGradeChoice() {
        if (uiInitialized) {
            return;
        }
        uiInitialized = true;

        statusChoice.getItems().setAll(CourseStatus.values());
        gradeChoice.getItems().clear();
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

    private void populateFieldsFromCourse() {
        if (course == null) {
            return;
        }
        if (nameLabel != null) {
            nameLabel.setText(course.getName());
        }

        CourseStatus st = course.getStatus();
        statusChoice.getSelectionModel().select(st);

        boolean done = (st == CourseStatus.SUPERATO);
        gradeChoice.setDisable(!done);

        if (done) {
            gradeChoice.getSelectionModel().select(course.getGrade());
        } else {
            gradeChoice.getSelectionModel().clearSelection();
        }

        datePicker.setValue(course.getDate());
    }

    public void handleUndoButton(ActionEvent event) {
        closePage(event);
    }

    public void handleEditButton(ActionEvent event) {
        CourseStatus newStatus = statusChoice.getValue();
        LocalDate newDate = datePicker.getValue();
        Integer newGrade = gradeChoice.getValue();

        if (!validateForm(newStatus, newDate, newGrade)) {
            return;
        }

        actions.editCourse(career, course, newStatus, newDate, newGrade);

        closePage(event);
    }

    public boolean validateForm(CourseStatus status, LocalDate date, Integer grade){
        FormValidator validator = new FormValidator();

        validator.validateNotNull(status, statusChoice);

        if (status == CourseStatus.SUPERATO ){
            validator.validateNotNull(grade, gradeChoice)
                    .validateNotNull(date, datePicker);
        }

        return validator.isValid();
    }

    public void clearDate(ActionEvent event) {
        datePicker.setValue(null);
    }
}
