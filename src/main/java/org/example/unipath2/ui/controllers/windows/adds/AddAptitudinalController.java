package org.example.unipath2.ui.controllers.windows.adds;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.unipath2.domain.career.CareerActions;
import org.example.unipath2.application.functions.courses.AptitudinalCourseFactory;
import org.example.unipath2.application.functions.forms.FormValidator;
import org.example.unipath2.ui.controllers.windows.WindowController;
import org.example.unipath2.domain.course.AptitudinalCourse;
import org.example.unipath2.domain.course.Course;
import org.example.unipath2.domain.enums.CourseSemester;
import org.example.unipath2.domain.enums.CourseStatus;
import org.example.unipath2.application.exception.DuplicateCourseException;

import java.time.LocalDate;

public class AddAptitudinalController extends WindowController {

    @FXML
    public TextField nameField;
    @FXML
    public ChoiceBox<Integer> cfuChoice;
    @FXML
    public ChoiceBox<Integer> yearChoice;
    @FXML
    public ChoiceBox<CourseStatus> statusChoice;
    @FXML
    public DatePicker datePicker;

    AptitudinalCourseFactory courseFactory = new AptitudinalCourseFactory();

    CareerActions actions = new CareerActions();

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
        initializeYearChoice();
        initializeStatusPassedChoice();
    }

    private void initializeStatusPassedChoice() {
        statusChoice.getItems().addAll(CourseStatus.values());
        statusChoice.getSelectionModel().select(CourseStatus.DA_FARE);
    }

    private void initializeYearChoice() {
        for (int i = 1; i <= 3; i++) {
            yearChoice.getItems().add(i);
        }
        yearChoice.getSelectionModel().select(0);
    }

    private void initializeCfuChoice() {
        for (int i = 3; i <= 12; i++) {
            cfuChoice.getItems().add(i);
        }
        cfuChoice.getSelectionModel().select(3);
    }

    private void resetFieldStyles() {
        nameField.setStyle("");
        cfuChoice.setStyle("");
        yearChoice.setStyle("");
        statusChoice.setStyle("");
        datePicker.setStyle("");
    }

    private boolean validateForm(String name,
                                 Integer cfu,
                                 Integer year,
                                 CourseStatus courseStatus,
                                 LocalDate date) {

        FormValidator validator = new FormValidator();

        validator.validateNotBlank(name, nameField)
                .validateNotNull(cfu, cfuChoice)
                .validateNotNull(year, yearChoice)
                .validateNotNull(courseStatus, statusChoice);

        if (courseStatus == CourseStatus.SUPERATO || courseStatus == CourseStatus.PIANIFICATO) {
            validator.validateNotNull(date, datePicker);
        }

        return validator.isValid();
    }

    public void handleUndoButton(ActionEvent event) {
        closePage(event);
    }

    public void handleAddButton(ActionEvent event) {
        String name = nameField.getText();
        Integer cfu = cfuChoice.getValue();
        Integer year = yearChoice.getValue();
        CourseSemester semester = CourseSemester.I;
        CourseStatus status = statusChoice.getValue();

        LocalDate date = datePicker.getValue();

        resetFieldStyles();
        if (!validateForm(name, cfu, year, status, date)) {
            return;
        }

        Course newCourse = courseFactory.build(name, cfu, year, semester, status, null, date);

        try {
            actions.addCourse(career, newCourse);
        } catch (DuplicateCourseException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Idoneità già presente");
            alert.setHeaderText("");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        closePage(event);
    }
}
