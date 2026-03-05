package org.example.unipath2.ui.controllers.pages;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import org.example.unipath2.domain.career.Observer;
import org.example.unipath2.ui.controllers.BaseController;
import org.example.unipath2.domain.course.Course;
import org.example.unipath2.application.statistics.Statistic;
import org.example.unipath2.application.statistics.avg.AvgStatistic;
import org.example.unipath2.application.statistics.minmax.CourseStrategy;
import org.example.unipath2.application.statistics.minmax.MaxStatistic;
import org.example.unipath2.application.statistics.minmax.MinStatistic;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StatsController extends BaseController implements Observer {

    private final CourseStrategy bestCourseStategy = new MaxStatistic();
    private final CourseStrategy worstCourseStategy = new MinStatistic();

    @FXML
    public Label avgLabel;
    @FXML
    public Label bestCourseLabel;
    @FXML
    public Label worstCourseLabel;
    @FXML
    public Label careerProgressLabel;
    @FXML
    public Label courseProgressLabel;
    @FXML
    public Label plannedCourseLabel;
    @FXML
    public LineChart<String, Number> weightedAvgGradesChart;
    @FXML
    public LineChart<String, Number> cfuChart;

    @Override
    public void onContextSet() {
        statistic = new Statistic(career.getCourses(), career);
        refreshUI();
    }

    @Override
    public void refreshUI() {
        AvgStatistic avgStatistic = new AvgStatistic();
        double avg = avgStatistic.compute(statistic.getValidPassedCourse());
        avgLabel.setText(String.format(String.valueOf(avg), "%.0f"));

        careerProgressLabel.setText(statistic.getCfuProgressText() + "%");
        courseProgressLabel.setText(statistic.getPassedCourse().size() + "/" + career.getCourses().size());
        plannedCourseLabel.setText(String.valueOf(statistic.getCntPlannedCourse()));

        Optional<Course> bestCourse = bestCourseStategy.compute(statistic.getValidPassedCourse());
        bestCourseLabel.setText("");
        bestCourse.ifPresent(course -> bestCourseLabel.setText(course.getName() + " (" + course.getGrade() + ")"));

        Optional<Course> worstCourse = worstCourseStategy.compute(statistic.getValidPassedCourse());
        worstCourseLabel.setText("");
        worstCourse.ifPresent(course -> worstCourseLabel.setText(course.getName() + " (" + course.getGrade() + ")"));

        updateWeightedAvgChart();
        updateCfuChart();
    }

    private void updateWeightedAvgChart() {
        weightedAvgGradesChart.getData().clear();
        hideXaxis(weightedAvgGradesChart);

        XYChart.Series<String, Number> avgSeries = new XYChart.Series<>();
        avgSeries.setName("Media Ponderata");

        XYChart.Series<String, Number> gradesSeries = new XYChart.Series<>();
        gradesSeries.setName("Voti");

        List<Course> passed = statistic.getValidPassedCourse();
        passed.sort(Comparator.comparing(Course::getDate));

        double weightedSum = 0;
        double cfuSum = 0;

        for (Course c : passed) {
            weightedSum += c.getGrade() * c.getCfu();
            cfuSum += c.getCfu();

            double avg = weightedSum / cfuSum;
            String label = c.getDate().toString();

            avgSeries.getData().add(new XYChart.Data<>(label, avg));
            gradesSeries.getData().add(new XYChart.Data<>(label, c.getGrade()));
        }

        weightedAvgGradesChart.getData().addAll(avgSeries, gradesSeries);
    }

    private void updateCfuChart() {
        cfuChart.getData().clear();
        hideXaxis(cfuChart);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Andamento CFU");

        int totalCfu = 0;

        List<Course> passed = statistic.getValidPassedCourse();
        passed.sort(Comparator.comparing(Course::getDate));

        for (Course course : passed) {
            totalCfu += course.getCfu();
            series.getData().add(new XYChart.Data<>(course.getName(), totalCfu));
        }

        cfuChart.getData().add(series);
    }

    private void hideXaxis(LineChart<String, Number> chart) {
        var xAxis = (CategoryAxis) chart.getXAxis();
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setOpacity(1);
    }

    public void handleHomeButton(ActionEvent event) {
        switchTab("/org/example/unipath2/home-view.fxml", event);
    }

    @FXML
    public void handleLibrettoButton(ActionEvent event) {
        switchTab("/org/example/unipath2/libretto-view.fxml", event);
    }
}
