package org.example.unipath2.ui.rings;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ProgressRing extends StackPane {
    private final DoubleProperty progress = new SimpleDoubleProperty(0); // [0..1]
    private final Circle track;
    private final Circle bar;
    private final Text centerText;
    private final Text bottomText;

    // thickness as a fraction of the widget size
    private final double thicknessRatio = 0.1; // 10% of min(width,height)

    public ProgressRing() {
        this(120); // default diameter
    }

    public ProgressRing(double diameter) {
        setPrefSize(diameter, diameter);
        setMinSize(diameter, diameter);
        setMaxSize(diameter, diameter);
        setAlignment(Pos.CENTER);

        // --- Base (track) ---
        track = new Circle();
        track.setFill(null);
        track.setStroke(Color.web("#E0E0E0"));
        track.setStrokeLineCap(StrokeLineCap.BUTT);

        // --- Foreground (progress) ---
        bar = new Circle();
        bar.setFill(null);
        bar.setStroke(Color.web("#4CAF50"));
        bar.setStrokeLineCap(StrokeLineCap.BUTT); // use ROUND if you prefer rounded ends

        // Center both circles inside this StackPane
        track.centerXProperty().bind(widthProperty().divide(2));
        track.centerYProperty().bind(heightProperty().divide(2));
        bar.centerXProperty().bind(widthProperty().divide(2));
        bar.centerYProperty().bind(heightProperty().divide(2));

        // Size-dependent bindings (radius and stroke width scale with min(width,height))
        DoubleBinding size = (DoubleBinding) Bindings.min(widthProperty(), heightProperty());
        DoubleBinding strokeW = size.multiply(thicknessRatio);
        track.strokeWidthProperty().bind(strokeW);
        bar.strokeWidthProperty().bind(strokeW);

        DoubleBinding radius = size.divide(2).subtract(strokeW.divide(2));
        track.radiusProperty().bind(radius);
        bar.radiusProperty().bind(radius);

        // Put 0° at 12 o'clock (by default it’s at 3 o’clock for circles)
        track.setRotate(-90);
        bar.setRotate(-90);

        // --- Center text ---
        centerText = new Text();
        centerText.setFont(Font.font("SF Pro Display", FontWeight.BOLD, 20));
        centerText.setFill(Color.web("#222"));

        // --- Bottom text ----
        bottomText = new Text();
        bottomText.setFont(Font.font("SF Pro Display", FontWeight.BOLD, 12));
        bottomText.setFill(Color.web("#a0a0a0"));

        // VBox for texts
        javafx.scene.layout.VBox textBox = new javafx.scene.layout.VBox();
        textBox.setAlignment(Pos.CENTER);
        textBox.setSpacing(2); // adjust as needed for spacing between texts
        textBox.getChildren().addAll(centerText, bottomText);

        getChildren().addAll(track, bar, textBox);

        // Keep dash pattern in sync with progress and size
        Runnable updater = () -> {
            double r = radius.get();
            if (r <= 0) return;
            double c = 2 * Math.PI * r; // circumference in pixels

            double p = clamp(progress.get(), 0.0, 1.0);
            // Avoid exact 1.0 (some renderers treat [c,0] oddly). Keep 0 exact.
            double visible = (p == 1.0) ? (c * 0.9999) : (c * p);
            double gap = Math.max(0.0001, c - visible);

            bar.getStrokeDashArray().setAll(visible, gap);
            bar.setStrokeDashOffset(0); // start already at 12 o’clock thanks to rotate
        };

        progress.addListener((o, ov, nv) -> updater.run());
        radius.addListener(o -> updater.run());
        strokeW.addListener(o -> updater.run());
        updater.run();
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    // --- Public API ---

    /**
     * Sets the progress in [0,1].
     */
    public void setProgress(double value) {
        progress.set(clamp(value, 0, 1));
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    /**
     * Sets the text shown at the center.
     */
    public void setCenterText(String text) {
        centerText.setText(text == null ? "" : text);
    }

    /**
     * Changes the color of the progress ring.
     */
    public void setRingColor(Color color) {
        bar.setStroke(color == null ? Color.web("#4CAF50") : color);
    }

    public void setBottomText(String text) {
        bottomText.setText(text == null ? "" : text);
    }

    /**
     * Changes the color of the background track.
     */
    public void setTrackColor(Color color) {
        track.setStroke(color == null ? Color.web("#E0E0E0") : color);
    }
}