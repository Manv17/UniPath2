package org.example.unipath2.ui.controllers.windows.edits;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import org.example.unipath2.application.functions.forms.FormValidator;
import org.example.unipath2.domain.career.CareerActions;
import org.example.unipath2.domain.course.AptitudinalCourse;
import org.example.unipath2.domain.course.Course;
import org.example.unipath2.domain.enums.CourseStatus;
import org.example.unipath2.ui.controllers.windows.WindowController;

import java.time.LocalDate;

public class EditGraduationController extends WindowController {

    @FXML
    public DatePicker datePicker;
    @FXML
    public ChoiceBox<CourseStatus> statusChoice;
    @FXML
    public ChoiceBox<Integer> pointChoice;

    @FXML
    public Label pointsLabel;
    @FXML
    public Label dateLabel;

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
        initailize();
    }

    private void initailize() {
        statusChoice.getItems().setAll(CourseStatus.values());
        pointChoice.getItems().clear();
        for (int i = 0; i <= 10; i++) {
            pointChoice.getItems().add(i);
        }
        pointChoice.setDisable(true);
        pointsLabel.setDisable(true);

        datePicker.setDisable(true);
        dateLabel.setDisable(true);

        pointChoiceAble();
        datePickerAble();

        CourseStatus status = career.getGraduation().getStatus();
        statusChoice.getSelectionModel().select(status);

        boolean done = (status == CourseStatus.SUPERATO);
        if (done) {
            pointChoice.getSelectionModel().select(career.getGraduation().getThesisPoints());
        } else {
            pointChoice.getSelectionModel().clearSelection();
        }

        datePicker.setValue(career.getGraduation().getGraduationDate());

    }

    private void pointChoiceAble() {
        statusChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldS, newS) -> {
            boolean done = (newS == CourseStatus.SUPERATO);
            pointChoice.setDisable(!done);
            pointsLabel.setDisable(!done);
            if (!done) {
                pointChoice.getSelectionModel().clearSelection();
            }
        });
    }

    private void datePickerAble() {
        statusChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldS, newS) -> {
            boolean done = (newS != CourseStatus.DA_FARE);
            datePicker.setDisable(!done);
            dateLabel.setDisable(!done);
        });
    }


    @FXML
    public void clearDate() {
        datePicker.setValue(null);
    }

    @FXML
    public void handleUndoButton(ActionEvent event) {
        closePage(event);
    }

    @FXML
    public void handleEditButton(ActionEvent event) {
        CourseStatus newStatus = statusChoice.getValue();
        LocalDate newDate = datePicker.getValue();
        Integer newPoints = pointChoice.getValue();

        resetFieldStyles();

        if (!validateForm(newStatus, newPoints, newDate)) {
            return;
        }

        career.getGraduation().setStatus(newStatus);
        career.getGraduation().setGraduationDate(newDate);
        career.getGraduation().setThesisPoints(newPoints);

        CareerActions actions = new CareerActions();
        actions.saveCareer(career);
        career.notifyObservers();

        closePage(event);
    }


    private void resetFieldStyles() {
        statusChoice.setStyle("");
        datePicker.setStyle("");
        pointChoice.setStyle("");
    }


    private boolean validateForm(
            CourseStatus courseStatus,
            Integer points,
            LocalDate date) {

        FormValidator validator = new FormValidator();

        validator
                .validateNotNull(courseStatus, statusChoice);

        if (courseStatus != CourseStatus.DA_FARE) {
            validator.validateNotNull(date, datePicker);
        }

        if (courseStatus == CourseStatus.SUPERATO) {
            validator.validateNotNull(points, pointChoice)
                    .validateNotNull(date, datePicker);
        }

        return validator.isValid();
    }
}
