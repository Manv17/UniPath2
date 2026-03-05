package org.example.unipath2.ui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.example.unipath2.domain.career.Observer;
import org.example.unipath2.ui.view.ViewManager;
import org.example.unipath2.domain.career.Career;
import org.example.unipath2.domain.course.Course;
import org.example.unipath2.application.statistics.Statistic;


public abstract class BaseController implements Observer {
    protected Career career;
    protected Statistic statistic;

    public void setController(Career career) {
        if (career == null) {
            throw new IllegalArgumentException("career cannot be null");
        }

        if (this.career == career) {
            refreshUI();
            return;
        }

        this.career = career;
        this.career.addObserver(this);

        onContextSet();
    }

    public abstract void onContextSet();

    @Override
    public void update(Career updatedCareer) {
        this.career = updatedCareer;
        statistic = new Statistic(updatedCareer.getCourses(), career);
        refreshUI();
    }

    public abstract void refreshUI();

    protected void switchTab(String fxmlPath, ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        ViewManager.switchToTab(stage, fxmlPath, career);
    }

    public static void closePage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    protected void openWindow(String fxml) {
        ViewManager.openWindow(fxml, career);
    }

    protected void openWindow(String fxml, Course course) {
        ViewManager.openWindow(fxml, career, course);
    }
}
