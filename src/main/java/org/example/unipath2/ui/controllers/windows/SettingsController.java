package org.example.unipath2.ui.controllers.windows;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import org.example.unipath2.domain.career.CareerActions;
import org.example.unipath2.domain.enums.DegreeType;
import org.example.unipath2.ui.controllers.BaseController;

public class SettingsController extends BaseController {

    @FXML
    public ChoiceBox<DegreeType> degreeTypeSelect;

    private DegreeType degreeType;
    private CareerActions actions = new CareerActions();

    public void setDegreeType(DegreeType degreeType) {
        this.degreeType = degreeType;
    }

    @Override
    public void onContextSet() {
        setDegreeType(career.getDegreeType());
        refreshUI();
    }

    @Override
    public void refreshUI() {
        if (degreeTypeSelect.getItems().isEmpty()) {
            degreeTypeSelect.getItems().addAll(DegreeType.values());
        }
        degreeTypeSelect.getSelectionModel().select(degreeType);
    }

    @FXML
    public void onCancelButton(ActionEvent event) {
        closePage(event);
    }

    @FXML
    public void onSaveButton(ActionEvent event) {
        setDegreeType(degreeTypeSelect.getValue());
        career.setDegreeType(degreeType);
        actions.saveCareer(career);
        closePage(event);
    }

    @FXML
    public void deleteCareer(ActionEvent event) {
        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Sei sicuro di voler eliminare la carriera?\n\nQuesta operazione azzera tutti i corsi salvati.",
                ButtonType.OK,
                ButtonType.CANCEL
        );
        confirm.setTitle("Conferma eliminazione carriera");
        confirm.setHeaderText(null);

        ButtonType result = confirm.showAndWait().orElse(ButtonType.CANCEL);
        if (result != ButtonType.OK) {
            return;
        }

        career.resetCareer();
        actions.saveCareer(career);

        setDegreeType(career.getDegreeType());
        refreshUI();

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Carriera eliminata");
        info.setHeaderText(null);
        info.setContentText("Carriera eliminata con successo");
        info.showAndWait();

        closePage(event);
    }
}
