package org.example.unipath2.ui.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.unipath2.domain.career.Career;
import org.example.unipath2.ui.controllers.BaseController;
import org.example.unipath2.ui.controllers.windows.edits.EditAptitudinalController;
import org.example.unipath2.ui.controllers.windows.edits.EditCourseController;
import org.example.unipath2.domain.course.AptitudinalCourse;
import org.example.unipath2.domain.course.Course;

import java.io.IOException;

public class ViewManager {
    public static void switchToTab(Stage stage, String fxml, Career career) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource(fxml));
            Parent root = loader.load();

            BaseController controller = loader.getController();
            controller.setController(career);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            System.out.println("Pagina " + fxml + " caricata");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore nel cambio schermata: " + fxml);
        }
    }

    public static void openWindow(String fxml, Career career) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource(fxml));
            Parent root = loader.load();

            BaseController controller = loader.getController();
            controller.setController(career);

            stageSetter(root);

            System.out.println("Finestra " + fxml + " aperta");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Errore apertura finestra: " + fxml);
        }
    }

    private static void stageSetter(Parent root) {
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.NONE);
        stage.setTitle(null);
        stage.show();
    }

    public static void openWindow(String fxml, Career career, Course course) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewManager.class.getResource(fxml));
            Parent root = loader.load();

            BaseController controller = loader.getController();

            if (controller instanceof EditCourseController editController) {
                editController.setCourseToEdit(course);
            } else if (controller instanceof EditAptitudinalController aptitudinalController) {
                if (course instanceof AptitudinalCourse aptCourse) {
                    aptitudinalController.setAptitudinalToEdit(aptCourse);
                } else {
                    throw new IllegalArgumentException(
                            "Attempted to open an Aptitudinal edit window with a non-aptitudinal Course: "
                                    + course.getClass().getName());
                }
            }

            controller.setController(career);

            stageSetter(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
