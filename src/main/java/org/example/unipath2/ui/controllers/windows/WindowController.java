package org.example.unipath2.ui.controllers.windows;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.example.unipath2.ui.controllers.BaseController;
import org.example.unipath2.domain.course.AptitudinalCourse;
import org.example.unipath2.domain.course.Course;

public abstract class WindowController extends BaseController {

    private Course course;

    public abstract void setCourseToEdit(Course course);

    public abstract void setAptitudinalToEdit(AptitudinalCourse course);

    public static void closePage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

}
