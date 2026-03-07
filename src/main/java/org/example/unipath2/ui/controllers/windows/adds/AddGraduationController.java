package org.example.unipath2.ui.controllers.windows.adds;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import org.example.unipath2.application.functions.forms.FormValidator;
import org.example.unipath2.domain.career.CareerActions;
import org.example.unipath2.domain.course.AptitudinalCourse;
import org.example.unipath2.domain.course.Course;
import org.example.unipath2.domain.course.Graduation;
import org.example.unipath2.domain.enums.CourseStatus;
import org.example.unipath2.ui.controllers.windows.WindowController;

import java.time.LocalDate;

public class AddGraduationController extends WindowController {
    @FXML
    public ChoiceBox<Integer> cfuChoice;
    @FXML
    public ChoiceBox<CourseStatus> statusChoice;
    @FXML
    public DatePicker datePicker;
    @FXML
    public ChoiceBox<Integer> pointsChoice;

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
        initializeCfuChoice();
        initializeStatusPointsChoice();
    }

    private void initializeCfuChoice() {
        for (int i = 3; i <= 12; i++) {
            cfuChoice.getItems().add(i);
        }
        cfuChoice.getSelectionModel().select(0);
    }

    private void initializeStatusPointsChoice() {
        statusChoice.getItems().addAll(CourseStatus.values());
        statusChoice.getSelectionModel().select(CourseStatus.DA_FARE);

        for (int i = 0; i <= 10; i++) {
            pointsChoice.getItems().add(i);
        }
        pointsChoice.setDisable(true);
        pointsLabel.setDisable(true);

        datePicker.setDisable(true);
        dateLabel.setDisable(true);

        pointChoiceAble();
        datePickerAble();
    }

    private void pointChoiceAble() {
        statusChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldS, newS) -> {
            boolean done = (newS == CourseStatus.SUPERATO);
            pointsChoice.setDisable(!done);
            pointsLabel.setDisable(!done);
            if (!done) {
                pointsChoice.getSelectionModel().clearSelection();
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
    public void handleUndoButton(ActionEvent event) {
        closePage(event);
    }

    @FXML
    public void handleAddButton(ActionEvent event) {
        Integer cfu = cfuChoice.getValue();
        CourseStatus status = statusChoice.getValue();
        LocalDate graduationDate = datePicker.getValue();
        Integer points = null;
        if (!pointsChoice.isDisabled()) {
            points = pointsChoice.getValue();
        }

        resetFieldStyles();

        if (!validateForm(cfu, status, points, graduationDate)) {
            return;
        }

        Graduation graduation = new Graduation(cfu, points, graduationDate, status);

        if (career.getGraduation() != null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Prova finale è già presente");
            alert.setHeaderText("");
            alert.setContentText("Ogni laurea ha solo una prova finale.");
            alert.showAndWait();
            return;
        } else {
            career.setGraduation(graduation);
            CareerActions actions = new CareerActions();
            actions.saveCareer(career);
        }

        closePage(event);
    }

    private void resetFieldStyles() {
        cfuChoice.setStyle("");
        statusChoice.setStyle("");
        datePicker.setStyle("");
        pointsChoice.setStyle("");
    }


    private boolean validateForm(
            Integer cfu,
            CourseStatus courseStatus,
            Integer points,
            LocalDate date) {

        FormValidator validator = new FormValidator();

        validator
                .validateNotNull(cfu, cfuChoice)
                .validateNotNull(courseStatus, statusChoice);

        if (courseStatus != CourseStatus.DA_FARE) {
            validator.validateNotNull(date, datePicker);
        }

        if (courseStatus == CourseStatus.SUPERATO) {
            validator.validateNotNull(points, pointsChoice)
                    .validateNotNull(date, datePicker);
        }

        return validator.isValid();
    }

}
