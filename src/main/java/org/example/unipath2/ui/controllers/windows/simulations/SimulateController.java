package org.example.unipath2.ui.controllers.windows.simulations;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import org.example.unipath2.ui.controllers.BaseController;
import org.example.unipath2.domain.enums.Colors;
import org.example.unipath2.application.statistics.Statistic;
import org.example.unipath2.application.statistics.avg.BaseCalculatorStatistic;
import org.example.unipath2.application.statistics.avg.NumericStrategy;
import org.example.unipath2.application.statistics.avg.WeightedAvgStatistic;

public class SimulateController extends BaseController {

    @FXML
    public ChoiceBox<Integer> cfuChoice;
    @FXML
    public ChoiceBox<Integer> gradeChoice;
    @FXML
    public Label wAvgLabel;
    @FXML
    public Label cfuLabel;
    @FXML
    public Label baseLabel;

    NumericStrategy wAvgStatistic = new WeightedAvgStatistic();
    NumericStrategy baseStatistic = new BaseCalculatorStatistic();

    @Override
    public void onContextSet() {
        statistic = new Statistic(career.getCourses(), career);

        for (int i = 18; i <= 30; i++) {
            gradeChoice.getItems().add(i);
        }
        gradeChoice.getSelectionModel().select(0);

        for (int i = 5; i <= 12; i++) {
            cfuChoice.getItems().add(i);
        }
        cfuChoice.getSelectionModel().select(0);


        initializeLabels();
    }

    @Override
    public void refreshUI() {
        int selectedGrade = gradeChoice.getValue();
        int selectedCfu = cfuChoice.getValue();

        int cfu = statistic.getEarnedCFU();

        double wAvg = wAvgStatistic.compute(statistic.getValidPassedCourse());
        double base = baseStatistic.compute(statistic.getValidPassedCourse());

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
        refreshUI();
    }

    private void setLabels(double wAvg, int cfu, double base) {
        wAvgLabel.setText(String.valueOf(Math.round((wAvg) * 100.0) / 100.0));
        cfuLabel.setText(String.valueOf(cfu));
        baseLabel.setText(String.format("%.0f", base));
    }

    private void initializeLabels() {
        wAvgLabel.setText(String.valueOf(wAvgStatistic.compute(statistic.getValidPassedCourse())));
        cfuLabel.setText(String.valueOf(statistic.getEarnedCFU()));
        baseLabel.setText(String.format("%.0f", baseStatistic.compute(statistic.getValidPassedCourse())));

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
