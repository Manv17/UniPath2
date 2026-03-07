package org.example.unipath2.ui.controllers.pages;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.unipath2.domain.career.Observer;
import org.example.unipath2.ui.controllers.BaseController;
import org.example.unipath2.domain.course.Course;
import org.example.unipath2.domain.enums.CourseSemester;
import org.example.unipath2.domain.enums.CourseStatus;
import org.example.unipath2.domain.course.AptitudinalCourse;
import org.example.unipath2.application.statistics.Statistic;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import org.example.unipath2.domain.career.CareerActions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LibrettoController extends BaseController implements Observer {

    private final CareerActions actions = new CareerActions();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public TableView<Course> coursesTable;
    @FXML
    public TableColumn<Course, String> courseNameColumn;
    @FXML
    public TableColumn<Course, Number> cfuColumn;
    @FXML
    public TableColumn<Course, CourseStatus> statusColumn;
    @FXML
    public TableColumn<Course, CourseSemester> semesterColumn;
    @FXML
    public TableColumn<Course, String> dateColumn;
    @FXML
    public TableColumn<Course, String> gradeColumn;
    @FXML
    public TableColumn<Course, Number> yearColumn;

    @Override
    public void onContextSet() {
        statistic = new Statistic(career.getCourses(), career);
        refreshUI();
    }

    private void updateTable() {
        courseNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        cfuColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCfu()));

        statusColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getStatus()));

        semesterColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<CourseSemester>(cellData.getValue().getSemester()));

        yearColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getYear()));

        dateColumn.setCellValueFactory(cellData -> {
                    LocalDate date = cellData.getValue().getDate();
                    return new SimpleStringProperty(date != null ? dateTimeFormatter.format(date) : "");
                }
        );

        gradeColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            if (course instanceof AptitudinalCourse apt) {
                return new SimpleStringProperty(apt.isPassed() ? "Idoneo" : "");
            }
            Integer grade = course.getGrade();
            return new SimpleStringProperty(grade != null ? grade.toString() : "");
        });
    }

    @Override
    public void refreshUI() {

        if (career == null || coursesTable == null) {
            return;
        }
        coursesTable.setItems(FXCollections.observableArrayList(career.getCourses()));
        updateTable();
        coursesTable.refresh();
    }

    @FXML
    public void handleDeleteButton(ActionEvent event) {
        Course selectedCourse = coursesTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Conferma eliminazione corso");
            alert.setHeaderText("Sei sicuro di voler eliminare il corso " + selectedCourse.getName() + "?");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Corso eliminato");
                info.setHeaderText("");
                info.setContentText("Corso eliminato con successo");
                info.showAndWait();

                actions.deleteCourse(career, selectedCourse);
                refreshUI();
            }
        }
    }

    public void handleEditButton(ActionEvent event) {
        Course selected = coursesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        String fxml;
        if (selected instanceof AptitudinalCourse) {
            fxml = "/org/example/unipath2/editAptitudinal-view.fxml";
        } else {
            fxml = "/org/example/unipath2/editCourse-view.fxml";
        }
        openWindow(fxml, selected);
    }

    @FXML
    public void handleHomeButton(ActionEvent event) {
        switchTab("/org/example/unipath2/home-view.fxml", event);
    }

    @FXML
    public void handleStatsButton(ActionEvent event) {
        switchTab("/org/example/unipath2/stats-view.fxml", event);
    }

    @FXML
    public void handleAddGradedCourse(ActionEvent event) {
        openWindow("/org/example/unipath2/addCourse-view.fxml");
    }

    @FXML
    public void handleAddAptitudinalCourse(ActionEvent event) {
        openWindow("/org/example/unipath2/addAptitudinal-view.fxml");
    }

    @FXML
    public void handleSettingsButton(ActionEvent event) {
        openWindow("/org/example/unipath2/settings-view.fxml");
    }

}
