package org.example.unipath2.ui.controllers.windows.edits;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import org.example.unipath2.application.functions.forms.FormValidator;
import org.example.unipath2.ui.controllers.pages.LibrettoController;
import org.example.unipath2.ui.controllers.windows.WindowController;
import org.example.unipath2.domain.course.AptitudinalCourse;
import org.example.unipath2.domain.course.Course;
import org.example.unipath2.domain.enums.CourseStatus;
import org.example.unipath2.infrastructure.Storage;

import java.time.LocalDate;

public class EditAptitudinalController extends WindowController {

    @FXML
    public Label nameLabel;
    @FXML public ChoiceBox<CourseStatus> statusChoice;
    @FXML public DatePicker datePicker;

    private AptitudinalCourse course;

    private LibrettoController librettoController;

    private boolean uiInitialized = false;

    public void setLibrettoController(LibrettoController librettoController) {
        this.librettoController = librettoController;
    }

    @Override
    public void setCourseToEdit(Course course) {

    }

    @Override
    public void setAptitudinalToEdit(AptitudinalCourse course) {
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
    }

    private void populateFieldsFromCourse() {
        if (course == null) {
            return;
        }
        if (nameLabel != null) {
            nameLabel.setText(course.getName());
        }

        CourseStatus status = course.getStatus();
        statusChoice.getSelectionModel().select(status);

        if (course.getDate() != null){
            datePicker.setValue(course.getDate());
        }
    }

    public boolean validateForm(CourseStatus status, LocalDate date){
        FormValidator validator = new FormValidator();
        validator.validateNotNull(status, statusChoice);

        if (status.equals(CourseStatus.DONE)){
            validator.validateNotNull(date, datePicker);
        }

        return validator.isValid();
    }

    public void handleUndoButton(ActionEvent event) {
        closePage(event);
    }

    public void handleEditButton(ActionEvent event) {
        CourseStatus newStatus = statusChoice.getValue();
        LocalDate newDate = datePicker.getValue();

        if (!validateForm(newStatus, newDate)) {
            return;
        }

        course.setPassed(newStatus == CourseStatus.DONE, newDate);

        course.setStatus(newStatus);
        course.setDate(newDate);

        career.notifyObservers();
        Storage.saveCareer(career);

        if (librettoController != null)
            librettoController.refreshUI();

        closePage(event);
    }
}
