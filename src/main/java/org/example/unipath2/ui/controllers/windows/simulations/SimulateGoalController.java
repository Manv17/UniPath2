package org.example.unipath2.ui.controllers.windows.simulations;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.example.unipath2.application.statistics.Statistic;
import org.example.unipath2.application.statistics.avg.NumericStrategy;
import org.example.unipath2.application.statistics.avg.WeightedAvgStatistic;
import org.example.unipath2.ui.controllers.BaseController;

public class SimulateGoalController extends BaseController {

    @FXML
    public Spinner<Double> goalSpinner;
    @FXML
    public Label remainingCfuLabel;
    @FXML
    public Label gradeLabel;
    @FXML
    public Label descriptionLabel;

    double target;
    int remainingCfu;

    Statistic statistic;

    @Override
    public void onContextSet() {
        statistic = new Statistic(career.getCourses(), career);

        SpinnerValueFactory.DoubleSpinnerValueFactory spinnerValueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(18.0, 30.0, 25.0, 0.5);

        goalSpinner.setValueFactory(spinnerValueFactory);
        goalSpinner.setEditable(true);

        statistic = new Statistic(career.getCourses(), career);

        refreshUI();
    }

    @Override
    public void refreshUI() {
        gradeLabel.setText("");
        descriptionLabel.setText("");
        descriptionLabel.setWrapText(true);

        remainingCfu = (int) statistic.getTotalCfu() - statistic.getEarnedCFU();
        remainingCfuLabel.setText((String.valueOf(remainingCfu)));
    }

    @FXML
    public void handleSimulate() {
        Double spinnerValue = goalSpinner.getValue();
        if (spinnerValue == null) {
            return;
        }
        target = spinnerValue;

        if (remainingCfu <= 0) {
            gradeLabel.setText("");
            descriptionLabel.setText("Non ci sono CFU rimanenti: hai già completato il piano CFU.");
            return;
        }

        double reqAvg = getReqAvg(target, remainingCfu);

        gradeLabel.setText(String.format("%.1f", reqAvg));
        if (reqAvg <= 0) {
            descriptionLabel.setText("Per mantenere una media ponderata di " + target + " nei prossimi " + remainingCfu + " Cfu, dovrai ottenere una media voti di " + String.format("%.1f", reqAvg));
        } else if (reqAvg > 30.0) {
            double maxAvg = getMaxReachableAvg();
            descriptionLabel.setText("Obiettivo media ponderata " + target + " NON raggiungibile. Il massimo che puoi ottenere è " + String.format("%.1f", maxAvg));
            gradeLabel.setText(String.format("%.1f", 30.0));
        } else if (reqAvg < 18.0) {
            double minAvg = getMinReachableAvg();
            descriptionLabel.setText("Obiettivo media ponderata " + target + " NON raggiungibile. Il minimo che puoi ottenere è " + String.format("%.1f", minAvg));
            gradeLabel.setText(String.format("%.1f", 18.0));
        } else {
            descriptionLabel.setText("Per ottenere una media ponderata di " + target + " nei prossimi " + remainingCfu + " Cfu, dovrai ottenere una media voti di " + String.format("%.1f", reqAvg));
        }
    }

    private double getReqAvg(double target, double remainingCfu) {
        NumericStrategy wAvgStrategy = new WeightedAvgStatistic();
        double wAvg = wAvgStrategy.compute(statistic.getValidPassedCourse());
        double totalCfu = statistic.getTotalCfu();
        double earnedCfu = statistic.getEarnedCFU();

        if (earnedCfu <= 0)
            return target;

        return (target * totalCfu - wAvg * earnedCfu) / remainingCfu;
    }

    private double getMaxReachableAvg() {
        NumericStrategy wAvgStrategy = new WeightedAvgStatistic();

        double earnedCfu = statistic.getEarnedCFU();
        double remainingCfu = Math.max(0, statistic.getTotalCfu() - earnedCfu);

        if (earnedCfu <= 0) {
            return 30.0;
        }

        double wAvg = wAvgStrategy.compute(statistic.getValidPassedCourse());

        return (wAvg * earnedCfu + 30.0 * remainingCfu) / statistic.getTotalCfu();
    }

    private double getMinReachableAvg() {
        NumericStrategy wAvgStrategy = new WeightedAvgStatistic();

        double earnedCfu = statistic.getEarnedCFU();
        double remainingCfu = Math.max(0, statistic.getTotalCfu() - earnedCfu);

        if (earnedCfu <= 0) {
            return 18.0;
        }

        double wAvg = wAvgStrategy.compute(statistic.getValidPassedCourse());

        return (wAvg * earnedCfu + 18.0 * remainingCfu)
                / (earnedCfu + remainingCfu);
    }

}
