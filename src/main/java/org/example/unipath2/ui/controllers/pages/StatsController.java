package org.example.unipath2.ui.controllers.pages;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.example.unipath2.domain.career.Observer;
import org.example.unipath2.domain.enums.DegreeType;
import org.example.unipath2.ui.controllers.BaseController;
import org.example.unipath2.domain.course.Course;
import org.example.unipath2.application.statistics.Statistic;
import org.example.unipath2.application.statistics.numeric.AvgStatistic;
import org.example.unipath2.application.statistics.minmax.CourseStrategy;
import org.example.unipath2.application.statistics.minmax.MaxStatistic;
import org.example.unipath2.application.statistics.minmax.MinStatistic;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    public HBox statusCard;
    @FXML
    public Label statusLabel;
    @FXML
    public BarChart<String, Number> semesterCfuChart;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ITALIAN);

    @Override
    public void onContextSet() {
        statistic = new Statistic(career.getCourses(), career);
        refreshUI();
    }

    @Override
    public void refreshUI() {
        AvgStatistic avgStatistic = new AvgStatistic();
        double avg = avgStatistic.compute(statistic.getValidPassedCourse(), statistic);
        avgLabel.setText(String.format("%.0f", avg));

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
        updateStatus();
    }

    private void updateStatus() {
        LocalDate deadline = getDeadline();

        LocalDate today = LocalDate.now();

        if (!today.isAfter(deadline)) {
            statusLabel.setText("Stato: in corso");
            statusLabel.setStyle("-fx-text-fill: rgb(52,118,89);");
            statusCard.setStyle("-fx-background-color: rgb(246,251,249);" +
                    " -fx-border-color: rgb(214,238,226);" +
                    "-fx-background-radius: 12px; " +
                    "-fx-border-radius: 12px;");
        } else {
            statusLabel.setText("Stato: fuori corso");
            statusLabel.setStyle("-fx-text-fill: rgb(168,58,58);");

            statusCard.setStyle("-fx-background-color: rgb(255,245,245);" +
                    "-fx-border-color: rgb(255,220,220);" +
                    "-fx-background-radius: 12px;" +
                    "-fx-border-radius: 12px;");
        }
    }

    private LocalDate getDeadline() {
        Integer enrollmentYear = career.getEnrollmentYear();
        DegreeType degreeType = career.getDegreeType();

        LocalDate enrollmentDate = LocalDate.of(enrollmentYear, 9, 15);

        LocalDate deadline;

        if (degreeType == DegreeType.TRIENNALE) {
            deadline = enrollmentDate.plusYears(3).plusMonths(6);
        } else if (degreeType == DegreeType.CICLO_UNICO) {
            deadline = enrollmentDate.plusYears(5).plusMonths(6);
        } else {
            deadline = enrollmentDate.plusYears(2).plusMonths(6);
        }
        return deadline;
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
        List<String> courseNames = new ArrayList<>();

        for (Course c : passed) {
            weightedSum += c.getGrade() * c.getCfu();
            cfuSum += c.getCfu();

            double avg = weightedSum / cfuSum;
            String label = dateTimeFormatter.format(c.getDate());
            courseNames.add(c.getName());

            avgSeries.getData().add(new XYChart.Data<>(label, avg));
            gradesSeries.getData().add(new XYChart.Data<>(label, c.getGrade()));
        }

        weightedAvgGradesChart.getData().addAll(avgSeries, gradesSeries);

        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> data : avgSeries.getData()) {
                if (data.getNode() != null) {
                    Tooltip tooltip = new Tooltip(
                            data.getXValue() + "\n" +
                                    "Media ponderata: " + String.format(Locale.ITALIAN, "%.2f", data.getYValue().doubleValue())
                    );
                    tooltip.setShowDelay(Duration.millis(100));
                    tooltip.setStyle("-fx-font-size: 14px; -fx-background-color: black; -fx-text-fill: white;");
                    Tooltip.install(data.getNode(), tooltip);
                }
            }

            for (int i = 0; i < gradesSeries.getData().size(); i++) {
                XYChart.Data<String, Number> data = gradesSeries.getData().get(i);
                if (data.getNode() != null) {
                    Tooltip tooltip = new Tooltip(
                            data.getXValue() + "\n" +
                                    "Corso: " + courseNames.get(i) + "\n" +
                                    "Voto: " + data.getYValue().intValue()
                    );
                    tooltip.setShowDelay(Duration.millis(100));
                    tooltip.setStyle("-fx-font-size: 14px; -fx-background-color: black; -fx-text-fill: white;");
                    Tooltip.install(data.getNode(), tooltip);
                }
            }
        });
    }

    private void updateCfuChart() {
        semesterCfuChart.getData().clear();
        hideXaxis(semesterCfuChart);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("CFU per semestre");

        List<Course> passed = new ArrayList<>(statistic.getPassedCourse());

        passed.sort(Comparator.comparing(Course::getDate));

        Map<String, Integer> cfuBySemester = new LinkedHashMap<>();

        for (Course course : passed) {
            LocalDate date = course.getDate();
            int month = date.getMonthValue();
            int year = date.getYear();

            String label;

            if (month >= 10) {
                int academicEndYear = year + 1;
                label = year + "/" + String.valueOf(academicEndYear).substring(2) + " - Primo Semestre";
            } else if (month <= 3) {
                int academicStartYear = year - 1;
                label = academicStartYear + "/" + String.valueOf(year).substring(2) + " - Primo Semestre";
            } else {
                int academicStartYear = year - 1;
                label = academicStartYear + "/" + String.valueOf(year).substring(2) + " - Secondo Semestre";
            }

            cfuBySemester.put(label, cfuBySemester.getOrDefault(label, 0) + course.getCfu());
        }

        for (Map.Entry<String, Integer> entry : cfuBySemester.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        semesterCfuChart.getData().add(series);

        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> data : series.getData()) {
                if (data.getNode() != null) {
                    Tooltip tooltip = new Tooltip(
                            data.getXValue() + "\n" +
                                    "CFU acquisiti: " + data.getYValue().intValue()
                    );
                    tooltip.setShowDelay(Duration.millis(100));
                    tooltip.setStyle("-fx-font-size: 14px; -fx-background-color: black; -fx-text-fill: white;");
                    Tooltip.install(data.getNode(), tooltip);
                }
            }
        });
    }

    private void hideXaxis(LineChart<String, Number> chart) {
        var xAxis = (CategoryAxis) chart.getXAxis();
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setOpacity(1);
    }
    private void hideXaxis(BarChart<String, Number> chart) {
        var xAxis = (CategoryAxis) chart.getXAxis();
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setOpacity(1);
    }

    @FXML
    public void handleHomeButton(ActionEvent event) {
        switchTab("/org/example/unipath2/views/pages/home-view.fxml", event);
    }

    @FXML
    public void handleLibrettoButton(ActionEvent event) {
        switchTab("/org/example/unipath2/views/pages/libretto-view.fxml", event);
    }

    @FXML
    public void handleSettingsButton() {
        openWindow("/org/example/unipath2/views/windows/settings-view.fxml");
    }

}
