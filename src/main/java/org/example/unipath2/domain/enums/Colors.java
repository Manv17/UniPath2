package org.example.unipath2.domain.enums;

import javafx.scene.paint.Color;

public enum Colors {
    PRIMARY(Color.web("#1566FF")),  // AVG
    CFU(Color.LIGHTSEAGREEN),
    BASE(Color.DARKRED),
    IMPROVEMENT(Color.rgb(48, 209,88).darker()),
    SETBACK(Color.RED),
    BLACK(Color.BLACK),
    ACCENT(Color.web("#172154"));


    private final Color color;

    Colors(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return color.toString();
    }
}
