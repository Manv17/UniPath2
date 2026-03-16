package org.example.unipath2.ui;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.unipath2.domain.career.Career;
import org.example.unipath2.domain.career.CareerActions;
import org.example.unipath2.infrastructure.AppInfo;
import org.example.unipath2.infrastructure.MenuBuilder;
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

        MenuBar menuBar = MenuBuilder.create(controller, stage, AppInfo.VERSION);

        VBox rootWithMenu = new VBox(menuBar, root);

        stage.setScene(new Scene(rootWithMenu));
        stage.setTitle(null);
        stage.show();

        UpdateManager updateManager = new UpdateManager(
                AppInfo.GITHUB_OWNER,
                AppInfo.GITHUB_REPO,
                AppInfo.VERSION
        );

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> updateManager.checkForUpdatesAsync(stage));
        delay.play();
    }
}