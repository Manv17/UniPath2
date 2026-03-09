package org.example.unipath2.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.unipath2.domain.career.Career;
import org.example.unipath2.domain.career.CareerActions;
import org.example.unipath2.infrastructure.UpdateManager;
import org.example.unipath2.ui.controllers.BaseController;

import java.io.IOException;

public class Home extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        CareerActions actions = new CareerActions();
        Career career = actions.load();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/unipath2/views/pages/home-view.fxml")
        );
        Parent root = loader.load();

        BaseController controller = loader.getController();
        controller.setController(career);

        stage.setScene(new Scene(root));
        stage.setTitle(null);
        stage.show();

        UpdateManager updateManager = new UpdateManager(
                "Manv17",
                "UniPath2",
                "2.2.1"
        );

        javafx.animation.PauseTransition delay =
                new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));

        delay.setOnFinished(e -> updateManager.checkForUpdatesAsync(stage));
        delay.play();
    }
}