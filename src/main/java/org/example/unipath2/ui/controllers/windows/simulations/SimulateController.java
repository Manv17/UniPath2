package org.example.unipath2.ui.controllers.windows.simulations;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import org.example.unipath2.domain.course.Course;
import org.example.unipath2.ui.controllers.BaseController;
import org.example.unipath2.domain.enums.Colors;
import org.example.unipath2.domain.enums.CourseStatus;
import org.example.unipath2.application.statistics.Statistic;
import org.example.unipath2.application.statistics.numeric.BaseCalculatorStatistic;
import org.example.unipath2.application.statistics.numeric.NumericStrategy;
import org.example.unipath2.application.statistics.numeric.WeightedAvgStatistic;

public class SimulateController extends BaseController {

    @FXML
    public ChoiceBox<Integer> gradeChoice;
    @FXML
    public Label wAvgLabel;
    @FXML
    public Label cfuLabel;
    @FXML
    public Label baseLabel;
    @FXML
    public Label courseCfuLabel;
    @FXML
    public ChoiceBox<Course> courseChoice;

    NumericStrategy wAvgStatistic = new WeightedAvgStatistic();
    NumericStrategy baseStatistic = new BaseCalculatorStatistic();

    @Override
    public void onContextSet() {
        statistic = new Statistic(career.getCourses(), career);

        for (int i = 18; i <= 30; i++) {
            gradeChoice.getItems().add(i);
        }
        gradeChoice.getSelectionModel().select(0);

        courseChoice.getItems().setAll(
                career.getCourses().stream()
                        .filter(course -> course.getStatus() != CourseStatus.SUPERATO)
                        .toList()
        );

        courseChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldCourse, newCourse) -> {
            if (newCourse != null) {
                courseCfuLabel.setText(String.valueOf(newCourse.getCfu()));
            } else {
                courseCfuLabel.setText("");
            }
        });

        initializeLabels();
    }

    @Override
    public void refreshUI() {
        Course selectedCourse = courseChoice.getValue();
        Integer selectedGrade = gradeChoice.getValue();

        if (selectedCourse == null || selectedGrade == null) {
            initializeLabels();
            return;
        }

        int selectedCfu = selectedCourse.getCfu();
        int cfu = statistic.getEarnedCFU();

        double wAvg = wAvgStatistic.compute(statistic.getValidPassedCourse(), statistic);
        double base = baseStatistic.compute(statistic.getValidPassedCourse(), statistic);

        int newCfu = cfu + selectedCfu;
        double newWAvg = (wAvg * cfu + selectedGrade * selectedCfu) / (cfu + selectedCfu);
        double newBase = newWAvg * (110.0 / 30.0);

        setLabels(newWAvg, newCfu, newBase);
        changeLabelColor(wAvgLabel, wAvg, newWAvg);
        changeLabelColor(baseLabel, base, newBase);
        changeLabelColor(cfuLabel, cfu, newCfu);
    }

    public void simulateGrade() {
        resetLabelColor(wAvgLabel);
        resetLabelColor(baseLabel);
        resetLabelColor(cfuLabel);
        refreshUI();
    }

    private void setLabels(double wAvg, int cfu, double base) {
        wAvgLabel.setText(String.valueOf(Math.round((wAvg) * 100.0) / 100.0));
        cfuLabel.setText(String.valueOf(cfu));
        baseLabel.setText(String.format("%.0f", base));
    }

    private void initializeLabels() {
        wAvgLabel.setText(String.valueOf(wAvgStatistic.compute(statistic.getValidPassedCourse(), statistic)));
        cfuLabel.setText(String.valueOf(statistic.getEarnedCFU()));
        baseLabel.setText(String.format("%.0f", baseStatistic.compute(statistic.getValidPassedCourse(), statistic)));

        resetLabelColor(wAvgLabel);
        resetLabelColor(cfuLabel);
        resetLabelColor(baseLabel);
    }

    private void changeLabelColor(Label label, double oldValue, double newValue) {
        if (newValue > oldValue) {
            label.setTextFill(Colors.IMPROVEMENT.getColor());
        } else {
            label.setTextFill(Colors.SETBACK.getColor());
        }
    }

    private void resetLabelColor(Label label) {
        label.setTextFill(Colors.BLACK.getColor());
    }
}
