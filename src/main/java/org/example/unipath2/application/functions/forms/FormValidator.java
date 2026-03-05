package org.example.unipath2.application.functions.forms;

import javafx.scene.control.Control;
import javafx.scene.control.TextInputControl;

public class FormValidator {

    private static final String ERROR_STYLE =
            "-fx-border-color: red; -fx-border-width: 3px;";

    private boolean valid = true;

    public boolean isValid() {
        return valid;
    }

    private FormValidator validate(boolean condition, Control control) {
        if (condition) {
            control.setStyle("");
        } else {
            control.setStyle(ERROR_STYLE);
            valid = false;
        }
        return this;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public FormValidator validateNotNull(Object value, Control control) {
        return validate(value != null, control);
    }

    public FormValidator validateNotBlank(String value, TextInputControl control) {
        return validate(value != null && !value.isBlank(), control);
    }
}
