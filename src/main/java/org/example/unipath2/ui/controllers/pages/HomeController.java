package org.example.unipath2.ui.controllers.pages;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.unipath2.application.statistics.courseList.NextExamsStatistic;
import org.example.unipath2.domain.career.Observer;
import org.example.unipath2.ui.controllers.BaseController;
import org.example.unipath2.domain.course.Course;
import org.example.unipath2.application.statistics.Statistic;
import org.example.unipath2.application.statistics.courseList.LatestPassedStatistic;
import org.example.unipath2.application.statistics.courseList.ListCourseStrategy;
import org.example.unipath2.ui.rings.ringFactory.BaseRingFactory;
import org.example.unipath2.ui.rings.ringFactory.CfuRingFactory;
import org.example.unipath2.ui.rings.ringFactory.RingFactory;
import org.example.unipath2.ui.rings.ringFactory.WeightedAvgRingFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


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

    @FXML
    public TableView<Course> nextTable;
    @FXML
    public TableColumn<Course, String> next_examColumn;
    @FXML
    public TableColumn<Course, String> next_dateColumn;
    @FXML
    public Label baseInfoLabel;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ITALIAN);

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
        updateBaseTooltip();
        updateLatestTable();
        updatePieChart();
        updateAvgChart();
        updateNextTable();
    }

    private void updateRing(StackPane container, RingFactory factory) {
        container.getChildren().setAll(factory.createRingCard(statistic, 120));
    }

    private void updateBaseTooltip() {

        String tooltipText = "La base di laurea si ottiene come:\n" +
                "(Media ponderata × 110) / 30";

        Tooltip tooltip = new Tooltip(tooltipText);
        tooltip.setShowDelay(javafx.util.Duration.millis(100));
        tooltip.setStyle("-fx-font-size: 14px; -fx-background-color: black; -fx-text-fill: white;");

        baseInfoLabel.setTooltip(tooltip);
    }

    private void updateLatestTable() {
        setTableLabel("😐", "Nessun esame sostenuto", latestTable);

        ListCourseStrategy listCourseStrategy = new LatestPassedStatistic();
        latestTable.setItems(FXCollections.observableList(listCourseStrategy.compute(statistic.getValidPassedCourse())));

        latest_courseColumn.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getName()));

        latest_gradeColumn.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getGrade()) {
                });
    }

    private void updateNextTable() {
        setTableLabel("📚", "Nessun esame pianificato", nextTable);

        ListCourseStrategy listCourseStrategy = new NextExamsStatistic();
        nextTable.setItems(FXCollections.observableList(listCourseStrategy.compute(career.getCourses())));

        next_examColumn.setCellValueFactory(c ->
                new ReadOnlyObjectWrapper<>(c.getValue().getName()));

        next_dateColumn.setCellValueFactory(cellData -> {
                    LocalDate date = cellData.getValue().getDate();
                    return new SimpleStringProperty(date != null ? dateTimeFormatter.format(date) : "");
                }
        );
    }

    private void setTableLabel(String icon, String text, TableView<Course> table) {
        VBox placeholder = new VBox(10);
        placeholder.setAlignment(Pos.CENTER);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 18px;");

        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-textLabel-fill: gray;");

        placeholder.getChildren().addAll(iconLabel, textLabel);

        table.setPlaceholder(placeholder);
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

            String label = dateTimeFormatter.format(c.getDate());
            series.getData().add(new XYChart.Data<>(label, avg));
        }

        avgChart.getData().clear();
        avgChart.getData().add(series);

        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> data : series.getData()) {
                if (data.getNode() != null) {

                    Tooltip tooltip = new Tooltip(
                            data.getXValue() + "\n" +
                                    "Media: " + String.format(Locale.ITALIAN, "%.2f", data.getYValue().doubleValue())
                    );
                    tooltip.setShowDelay(javafx.util.Duration.millis(100));
                    tooltip.setStyle("-fx-font-size: 14px; -fx-background-color: black; -fx-text-fill: white;");
                    Tooltip.install(data.getNode(), tooltip);
                }
            }
        });

        xAvgChart.setTickLabelsVisible(false);
        xAvgChart.setTickMarkVisible(false);
        xAvgChart.setOpacity(0);

        yAvgChart.setTickMarkVisible(true);
    }

    @FXML
    public void handleSimulateButton() {
        openWindow("/org/example/unipath2/views/windows/simulateGrade-view.fxml");
    }

    @FXML
    public void handleLibrettoButton(ActionEvent event) {
        switchTab("/org/example/unipath2/views/pages/libretto-view.fxml", event);
    }

    @FXML
    public void handleStatsButton(ActionEvent event) {
        switchTab("/org/example/unipath2/views/pages/stats-view.fxml", event);
    }

    @FXML
    public void handleSettingsButton() {
        openWindow("/org/example/unipath2/views/windows/settings-view.fxml");
    }

    @FXML
    public void HandleSimulateGoalButton() {
        openWindow("/org/example/unipath2/views/windows/simulateGoal-view.fxml");
    }

}
