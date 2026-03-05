package org.example.unipath2.ui.rings;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class RingCard extends StackPane{

    public RingCard (Color color, int diameter, String centerLabel, String bottomLabel, double progress) {
        ProgressRing ring = new ProgressRing(diameter);
        ring.setProgress(progress);
        ring.setRingColor(color);
        ring.setCenterText(String.valueOf(centerLabel));
        ring.setBottomText(bottomLabel);

        getChildren().add(ring);
    }

}
