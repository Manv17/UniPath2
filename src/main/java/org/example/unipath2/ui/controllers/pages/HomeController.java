package org.example.unipath2.ui.controllers.pages;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import org.example.unipath2.domain.career.Observer;
import org.example.unipath2.ui.controllers.BaseController;
import org.example.unipath2.domain.course.Course;
import org.example.unipath2.application.statistics.Statistic;
import org.example.unipath2.application.statistics.latest.LatestPassedStatistic;
import org.example.unipath2.application.statistics.latest.ListCourseStrategy;
import org.example.unipath2.ui.rings.ringFactory.BaseRingFactory;
import org.example.unipath2.ui.rings.ringFactory.CfuRingFactory;
import org.example.unipath2.ui.rings.ringFactory.RingFactory;
import org.example.unipath2.ui.rings.ringFactory.WeightedAvgRingFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class HomeController extends BaseController implements Observer {

    private final RingFactory cfuFactory = new CfuRingFactory();
    private final RingFactory weightedAvgRingFactoryAvgFactory = new WeightedAvgRingFactory();
    private final RingFactory baseFactory = new BaseRingFactory();

    @FXML
    public StackPane cfuContainer;
    @FXML
    public StackPane wAvgContainer;
    @FXML
    public StackPane baseContainer;

    @FXML
    public TableView<Course> latestTable;
    @FXML
    public TableColumn<Course, String> latest_courseColumn;
    @FXML
    public TableColumn<Course, Integer> latest_gradeColumn;

    @FXML
    public PieChart pieChart;

    @FXML
    public LineChart<String, Number> avgChart;
    @FXML
    public CategoryAxis xAvgChart;
    @FXML
    public NumberAxis yAvgChart;


    @Override
    public void onContextSet() {
        statistic = new Statistic(career.getCourses(), career);
        refreshUI();
    }

    @Override
    public void refreshUI() {
        updateRing(cfuContainer, cfuFactory);
        updateRing(wAvgContainer, weightedAvgRingFactoryAvgFactory);
        updateRing(baseContainer, baseFactory);
        updateLatestTable();
        updatePieChart();
        updateAvgChart();
    }

    private void updateRing(StackPane container, RingFactory factory) {
        container.getChildren().setAll(factory.createRingCard(statistic, 120));
    }

    private void updateLatestTable() {
        ListCourseStrategy listCourseStrategy = new LatestPassedStatistic();
        latestTable.setItems(FXCollections.observableList(listCourseStrategy.compute(statistic.getValidPassedCourse())));

        latest_courseColumn.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getName()));

        latest_gradeColumn.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getGrade()) {
                });
    }

    private void updatePieChart() {

        int passed = statistic.getCntPassedCourse();
        int todo = statistic.getCntToDOCourse();
        int planned = statistic.getCntPlannedCourse();

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
                new PieChart.Data("Passati (" + passed + ")", passed),
                new PieChart.Data("Da fare (" + todo + ")", todo),
                new PieChart.Data("Prenotati (" + planned + ")", planned)
        );

        pieChart.setData(data);
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(false);
        pieChart.setTitle("");
    }

    private void updateAvgChart() {

        avgChart.getData().clear();
        xAvgChart.setLabel(null);
        yAvgChart.setLabel(null);

        List<Course> passed = new ArrayList<>(statistic.getValidPassedCourse());
        passed.sort(Comparator.comparing(Course::getDate));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Andamento Media Ponderata");

        double weightedSum = 0;
        double cfuSum = 0;

        for (Course c : passed) {
            weightedSum += c.getGrade() * c.getCfu();
            cfuSum += c.getCfu();

            double avg = weightedSum / cfuSum;

            String label = c.getDate().toString();
            series.getData().add(new XYChart.Data<>(label, avg));
        }

        avgChart.getData().clear();
        avgChart.getData().add(series);


        xAvgChart.setTickLabelsVisible(false);
        xAvgChart.setTickMarkVisible(false);
        xAvgChart.setOpacity(0);

        yAvgChart.setTickMarkVisible(true);
    }

    @FXML
    public void handleSimulateButton(ActionEvent event) {
        openWindow("/org/example/unipath2/simulateGrade-view.fxml");
    }

    @FXML
    public void handleLibrettoButton(ActionEvent event) {
        switchTab("/org/example/unipath2/libretto-view.fxml", event);
    }

    @FXML
    public void handleStatsButton(ActionEvent event) {
        switchTab("/org/example/unipath2/stats-view.fxml", event);
    }

    @FXML
    public void handleSettingsButton(ActionEvent event) {
        openWindow("/org/example/unipath2/settings-view.fxml");
    }

    @FXML
    public void HandleSimulateGoalButton(ActionEvent event) {
        openWindow("/org/example/unipath2/simulateGoal-view.fxml");
    }
}
