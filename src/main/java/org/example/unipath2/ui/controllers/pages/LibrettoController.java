package org.example.unipath2.ui.controllers.pages;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.unipath2.domain.career.Observer;
import org.example.unipath2.domain.course.Graduation;
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
    @FXML
    public TableView<Object> coursesTable;
    @FXML
    public TableColumn<Object, String> courseNameColumn;
    @FXML
    public TableColumn<Object, Number> cfuColumn;
    @FXML
    public TableColumn<Object, CourseStatus> statusColumn;
    @FXML
    public TableColumn<Object, CourseSemester> semesterColumn;
    @FXML
    public TableColumn<Object, String> dateColumn;
    @FXML
    public TableColumn<Object, String> gradeColumn;
    @FXML
    public TableColumn<Object, Number> yearColumn;
    @FXML
    public MenuItem addGraduationItem;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void onContextSet() {
        statistic = new Statistic(career.getCourses(), career);
        updateTable();
        refreshUI();
    }

    private void updateTable() {
        courseNameColumn.setCellValueFactory(cellData -> {
            Object value = cellData.getValue();
            if (value instanceof Course course) {
                return new SimpleStringProperty(course.getName());
            }
            if (value instanceof Graduation) {
                return new SimpleStringProperty("Prova finale");
            }
            return new SimpleStringProperty("");
        });

        cfuColumn.setCellValueFactory(cellData -> {
            Object value = cellData.getValue();
            if (value instanceof Course course) {
                return new SimpleIntegerProperty(course.getCfu());
            }
            if (value instanceof Graduation graduation) {
                return new SimpleIntegerProperty(graduation.getCfu());
            }
            return new SimpleIntegerProperty(0);
        });

        statusColumn.setCellValueFactory(cellData -> {
            Object value = cellData.getValue();
            if (value instanceof Course course) {
                return new SimpleObjectProperty<>(course.getStatus());
            }
            if (value instanceof Graduation graduation) {
                return new SimpleObjectProperty<>(graduation.getStatus());
            }
            return new SimpleObjectProperty<>(null);
        });

        semesterColumn.setCellValueFactory(cellData -> {
            Object value = cellData.getValue();
            if (value instanceof Course course) {
                return new SimpleObjectProperty<>(course.getSemester());
            }
            return new SimpleObjectProperty<>(null);
        });

        yearColumn.setCellValueFactory(cellData -> {
            Object value = cellData.getValue();
            if (value instanceof Course course) {
                return new SimpleIntegerProperty(course.getYear());
            }
            return new SimpleIntegerProperty(0);
        });

        dateColumn.setCellValueFactory(cellData -> {
            Object value = cellData.getValue();
            LocalDate date = null;

            if (value instanceof Course course) {
                date = course.getDate();
            } else if (value instanceof Graduation graduation) {
                date = graduation.getGraduationDate();
            }

            return new SimpleStringProperty(date != null ? dateTimeFormatter.format(date) : "");
        });

        gradeColumn.setCellValueFactory(cellData -> {
            Object value = cellData.getValue();

            if (value instanceof AptitudinalCourse apt) {
                return new SimpleStringProperty(apt.isPassed() ? "Idoneo" : "");
            }

            if (value instanceof Course course) {
                Integer grade = course.getGrade();
                return new SimpleStringProperty(grade != null ? grade.toString() : "");
            }

            if (value instanceof Graduation graduation) {
                Integer points = graduation.getThesisPoints();
                return new SimpleStringProperty(points != null ? "+" + points : "");
            }

            return new SimpleStringProperty("");
        });
    }

    @Override
    public void refreshUI() {

        if (career == null || coursesTable == null) {
            return;
        }
        ObservableList<Object> tableItems = FXCollections.observableArrayList();
        tableItems.addAll(career.getCourses());
        if (career.getGraduation() != null) {
            tableItems.add(career.getGraduation());
        }
        coursesTable.setItems(tableItems);

        if (career.getGraduation() != null) {
            addGraduationItem.setDisable(true);
            addGraduationItem.setText("Prova finale già presente");
        } else {
            addGraduationItem.setDisable(false);
            addGraduationItem.setText("Prova finale");
        }
    }

    @FXML
    public void handleDeleteButton() {
        Object selectedItem = coursesTable.getSelectionModel().getSelectedItem();

        if (selectedItem instanceof Graduation) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Conferma eliminazione prova finale");
            alert.setHeaderText("Sei sicuro di voler eliminare la prova finale?");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Prova finale eliminata");
                info.setHeaderText("");
                info.setContentText("Prova finale eliminata con successo");
                info.showAndWait();

                career.setGraduation(null);
                actions.saveCareer(career);
                career.notifyObservers();
                refreshUI();
            }
            return;
        }

        if ((selectedItem instanceof Course selectedCourse)) {
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

    public void handleEditButton() {
        Object selectedItem = coursesTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        if (selectedItem instanceof Graduation) {
            openWindow("/org/example/unipath2/views/edits/editGraduation-view.fxml");
            return;
        }

        if (!(selectedItem instanceof Course selected)) {
            return;
        }

        String fxml;
        if (selected instanceof AptitudinalCourse) {
            fxml = "/org/example/unipath2/views/edits/editAptitudinal-view.fxml";
        } else {
            fxml = "/org/example/unipath2/views/edits/editCourse-view.fxml";
        }
        openWindow(fxml, selected);
    }

    @FXML
    public void handleHomeButton(ActionEvent event) {
        switchTab("/org/example/unipath2/views/pages/home-view.fxml", event);
    }

    @FXML
    public void handleStatsButton(ActionEvent event) {
        switchTab("/org/example/unipath2/views/pages/stats-view.fxml", event);
    }

    @FXML
    public void handleAddGradedCourse() {
        openWindow("/org/example/unipath2/views/adds/addCourse-view.fxml");
    }

    @FXML
    public void handleAddAptitudinalCourse() {
        openWindow("/org/example/unipath2/views/adds/addAptitudinal-view.fxml");
    }

    @FXML
    public void handleSettingsButton() {
        openWindow("/org/example/unipath2/views/windows/settings-view.fxml");
    }

    @FXML
    public void handleAddGraduation() {
        openWindow("/org/example/unipath2/views/adds/addGraduation-view.fxml");
    }
}
