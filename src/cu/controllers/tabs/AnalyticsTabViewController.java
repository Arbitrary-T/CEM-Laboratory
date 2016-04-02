package cu.controllers.tabs;

import cu.models.equipment.Equipment;
import cu.models.equipment.EquipmentDatabase;
import cu.models.statistics.StatisticsDatabase;
import cu.models.students.Student;
import cu.models.students.StudentDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.util.HashMap;
import java.util.List;
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
    Label bottomLeftLabel;
    @FXML
    Label topLeftLabel;
    @FXML
    Label topRightLabel;
    @FXML
    Label numberOfStudentsComputed;
    @FXML
    ComboBox<String> pieChartComboBox;
    @FXML
    LineChart topLeftChart;
    @FXML
    LineChart topRightChart;
    @FXML
    PieChart bottomLeftPieChart;
    @FXML
    BarChart<String, Integer> bottomRightBarChart;

    private int totalNumberOfStudents = 0;
    private ObservableList<String> listOfPieChartOptions = FXCollections.observableArrayList("Functional Vs Faulty Returns", "Faulty Returns By Course Contribution");

    @FXML
    private void initialize()
    {
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
            bottomLeftLabel.setText(newValue);
        });
        computeGlobalReturns();
        computeFaultyItemsByCategory();
    }

    private void computeGlobalReturns()
    {
        List<Student> studentList = studentDatabase.getAllStudents();
        int globalFaultyReturns = 0;
        int globalTotalReturns = 0;
        totalNumberOfStudents = studentList.size();
        for(Student student : studentList)
        {
            globalFaultyReturns+=student.getFaultyReturns();
            globalTotalReturns+=student.getTotalReturns();
        }
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(new PieChart.Data("Faulty", globalFaultyReturns), new PieChart.Data("Functioning", globalTotalReturns));
        bottomLeftPieChart.setData(pieChartData);
        numberOfStudentsComputed.setText("A total of " + totalNumberOfStudents + " students were computed");
    }

    private void computeContributionByCourse()
    {
        HashMap<String, Integer> pieChartHashMap = new HashMap<>();
        List<Student> studentList = studentDatabase.getAllStudents();
        totalNumberOfStudents = studentList.size();
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
        numberOfStudentsComputed.setText("A total of " + totalNumberOfStudents + " students were computed");
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
        System.out.println(barChartHashMap);
    }
}
