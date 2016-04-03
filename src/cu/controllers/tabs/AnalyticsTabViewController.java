package cu.controllers.tabs;

import cu.models.equipment.Equipment;
import cu.models.equipment.EquipmentDatabase;
import cu.models.statistics.Statistics;
import cu.models.statistics.StatisticsDatabase;
import cu.models.students.Student;
import cu.models.students.StudentDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


/**
 * Created by T on 01/04/2016.
 */
public class AnalyticsTabViewController
{
    StatisticsDatabase statisticsDatabase = new StatisticsDatabase("stats");
    StudentDatabase studentDatabase = new StudentDatabase("students");
    EquipmentDatabase equipmentDatabase = new EquipmentDatabase("equipment");

    @FXML
    Label bottomRightLabel;
    @FXML
    Label topLeftLabel;
    @FXML
    Label topRightLabel;
    @FXML
    Label numberOfStudentsComputed;
    @FXML
    ComboBox<String> pieChartComboBox;
    @FXML
    LineChart<String, Number> topLeftChart;
    @FXML
    LineChart topRightChart;
    @FXML
    PieChart bottomLeftPieChart;
    @FXML
    BarChart<String, Integer> bottomRightBarChart;

    Map<LocalDate, Long> sortedMap = new TreeMap<>();


    private ObservableList<String> listOfPieChartOptions = FXCollections.observableArrayList("Functional Vs Faulty Returns", "Faulty Returns By Course Contribution");

    @FXML
    private void initialize()
    {
        /*for(int i = 0; i < 12; i++)
        {
            statisticsDatabase.addEntry(new Statistics(LocalDate.of(2016, i+1, ThreadLocalRandom.current().nextInt(1,31)),ThreadLocalRandom.current().nextLong(650000), 5, 5));
            statisticsDatabase.addEntry(new Statistics(LocalDate.of(2016, i+1, ThreadLocalRandom.current().nextInt(1,31)),ThreadLocalRandom.current().nextLong(650000), 5, 5));
        }*/
        pieChartComboBox.setItems(listOfPieChartOptions);
        pieChartComboBox.setValue(listOfPieChartOptions.get(0));
        pieChartComboBox.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.equals(listOfPieChartOptions.get(0)))
            {
                computeGlobalReturns();
            }
            else if(newValue.equals(listOfPieChartOptions.get(1)))
            {
                computeContributionByCourse();
            }
        });
        computeGlobalReturns();
        computeFaultyItemsByCategory();
        computeLineChart();
        computeLineChartForPeriod(LocalDate.of(2016, 8, 28), LocalDate.of(2016, 12, 21)); // 2016-08-28 2016-12-21);


    }

    private void computeGlobalReturns()
    {
        List<Student> studentList = studentDatabase.getAllStudents();
        int globalFaultyReturns = 0;
        int globalTotalReturns = 0;

        for(Student student : studentList)
        {
            globalFaultyReturns+=student.getFaultyReturns();
            globalTotalReturns+=student.getTotalReturns();
        }
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(new PieChart.Data("Faulty", globalFaultyReturns), new PieChart.Data("Functioning", globalTotalReturns));
        bottomLeftPieChart.setData(pieChartData);
        bottomLeftPieChart.setTitle(pieChartComboBox.getSelectionModel().getSelectedItem());
        numberOfStudentsComputed.setText("A total of " + studentList.size() + " students were computed");
    }

    private void computeContributionByCourse()
    {
        HashMap<String, Integer> pieChartHashMap = new HashMap<>();
        List<Student> studentList = studentDatabase.getAllStudents();
        for(Student student : studentList)
        {
            if(pieChartHashMap.containsKey(student.getStudentCourse()))
            {
                pieChartHashMap.put(student.getStudentCourse(), pieChartHashMap.get(student.getStudentCourse()) + student.getFaultyReturns());
            }
            else
            {
                pieChartHashMap.put(student.getStudentCourse(), student.getFaultyReturns());
            }
        }
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        pieChartData.addAll(pieChartHashMap.keySet().stream().map(key -> new PieChart.Data(key, pieChartHashMap.get(key))).collect(Collectors.toList()));
        bottomLeftPieChart.setData(pieChartData);
        bottomLeftPieChart.setTitle(pieChartComboBox.getSelectionModel().getSelectedItem());
        numberOfStudentsComputed.setText("A total of " + studentList.size() + " students were computed");
        createChartDurationAlert();
    }

    private void computeFaultyItemsByCategory()
    {
        HashMap<String, Integer> barChartHashMap = new HashMap<>();
        List<Equipment> equipmentList = equipmentDatabase.getAllEquipment();

        for(Equipment equipment : equipmentList)
        {
            if(barChartHashMap.containsKey(equipment.getItemCategory()))
            {
                if(!equipment.isFunctional())
                {
                    barChartHashMap.put(equipment.getItemCategory(), barChartHashMap.get(equipment.getItemCategory())+1);
                }
            }
            else
            {
                if(!equipment.isFunctional())
                {
                    barChartHashMap.put(equipment.getItemCategory(), 1);
                }
                else
                {
                    barChartHashMap.put(equipment.getItemCategory(),0);
                }
            }
        }

        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Category");
        for(String key : barChartHashMap.keySet())
        {
            series.getData().add(new XYChart.Data<>(key, barChartHashMap.get(key)));
        }
        bottomRightBarChart.getData().addAll(series);

        for(int i = 0; i < bottomRightBarChart.getData().get(0).getData().size(); i++)
        {
            bottomRightBarChart.getData().get(0).getData().get(i).getNode().setStyle("-fx-bar-fill: CHART_COLOR_"+ThreadLocalRandom.current().nextInt(1,9)+";");
        }
        bottomRightBarChart.getYAxis().setLabel("Number of faulty items");
        bottomRightLabel.setText("A total of " + equipmentList.size() + " records were computed");
        System.out.println(barChartHashMap);
    }

    private void computeLineChart()
    {
        int[] numberOfOccurrencesPerMonth = new int[12];
        sortedMap = new TreeMap<>();
        List<Statistics> statisticsList = statisticsDatabase.getAll();

        for(Statistics s : statisticsList)
        {
            //Populate Hashmap
            if(sortedMap.containsKey(s.getDate()))
            {
                numberOfOccurrencesPerMonth[s.getDate().getMonth().getValue()-1]++;
                sortedMap.put(s.getDate(), sortedMap.get(s.getDate()) + s.getAverageUsage());
                System.out.println(sortedMap.get(s.getDate()) + s.getAverageUsage());
            }
            else
            {
                numberOfOccurrencesPerMonth[s.getDate().getMonth().getValue()-1]++;
                sortedMap.put(s.getDate(), s.getAverageUsage());
            }
        }
        for(LocalDate s : sortedMap.keySet())
        {
            //compute avg..
            sortedMap.put(s,sortedMap.get(s)/numberOfOccurrencesPerMonth[s.getMonth().getValue()-1]);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        System.out.println(sortedMap);

        for(LocalDate s : sortedMap.keySet())
        {
            series.getData().add(new XYChart.Data<>(s.toString(), sortedMap.get(s)/3600));
        }
        topLeftChart.getData().add(series);
        topLeftChart.getXAxis().setLabel("Day");
        topLeftChart.getYAxis().setLabel("Average usage of equipment (hours)");
    }

    private void computeLineChartForPeriod(LocalDate startDate, LocalDate endDate)
    {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        System.out.println(startDate.toString() + endDate.toString());
        for(LocalDate s : sortedMap.keySet())
        {
            if(s.isEqual(startDate) || s.isEqual(endDate) || (s.isAfter(startDate) && s.isBefore(endDate)))
            {
                System.out.println("Hello");
                series.getData().add(new XYChart.Data<>(s.toString(), sortedMap.get(s)/3600));
            }
        }
        topLeftChart.getData().clear();
        topLeftChart.getData().add(series);
        topLeftChart.getXAxis().setLabel("Day");
        topLeftChart.getYAxis().setLabel("Average usage of equipment (hours)");
    }

    private void createChartDurationAlert()
    {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Input Dialog");
        dialog.setHeaderText("Please enter the duration for the chart");

        ButtonType loginButtonType = new ButtonType("Compute", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField startDate = new TextField();
        startDate.setPromptText("yyyy-MM-dd");
        TextField endDate = new TextField();
        endDate.setPromptText("yyyy-MM-dd");

        grid.add(new Label("Start Date:"), 0, 0);
        grid.add(startDate, 1, 0);
        grid.add(new Label("End Date:"), 0, 1);
        grid.add(endDate, 1, 1);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton ->
        {
            if (dialogButton == loginButtonType)
            {
                return new Pair<>(startDate.getText(), endDate.getText());
            }
            return null;
        });
        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(startEndDate ->
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try
            {
                simpleDateFormat.parse(startDate.getText());
                simpleDateFormat.parse(endDate.getText());
                System.out.println("DATE = " + startDate.getText() + "END DATE = " + endDate.getText());
                computeLineChartForPeriod(
                        LocalDate.of(Integer.parseInt(startDate.getText().substring(0,3)),
                                Integer.parseInt(startDate.getText().substring(5,6)),
                                        Integer.parseInt(startDate.getText().substring(8,9))),

                        LocalDate.of(Integer.parseInt(endDate.getText().substring(0,3)),
                                Integer.parseInt(endDate.getText().substring(5,6)),
                                Integer.parseInt(endDate.getText().substring(8,9))));
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        });

    }
}
