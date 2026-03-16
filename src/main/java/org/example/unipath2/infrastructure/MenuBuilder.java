package org.example.unipath2.infrastructure;

import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;
import org.example.unipath2.ui.controllers.BaseController;

public final class MenuBuilder {

    private MenuBuilder() {
    }

    public static MenuBar create(BaseController controller, Stage stage, String currentVersion) {
        MenuBar menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);

        Menu appMenu = new Menu("UniPath2");

        MenuItem aboutItem = new MenuItem("Informazioni su UniPath2");
        aboutItem.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informazioni su UniPath2");
            alert.setHeaderText("UniPath2");
            alert.setContentText("Versione: " + AppInfo.VERSION);
            alert.showAndWait();
        });

        MenuItem settingsItem = new MenuItem("Impostazioni...");
        settingsItem.setOnAction(e -> controller.openWindow("/org/example/unipath2/views/windows/settings-view.fxml"));

        MenuItem checkUpdatesItem = new MenuItem("Verifica disponibilità aggiornamenti...");
        checkUpdatesItem.setOnAction(e -> {
            UpdateManager updateManager = new UpdateManager(
                    AppInfo.GITHUB_OWNER,
                    AppInfo.GITHUB_REPO,
                    AppInfo.VERSION
            );
            updateManager.checkForUpdatesAsync(stage);
        });

        appMenu.getItems().addAll(
                aboutItem,
                new SeparatorMenuItem(),
                settingsItem,
                checkUpdatesItem
        );

        menuBar.getMenus().add(appMenu);
        return menuBar;
    }
}
