package cu.controllers.tabs;

import cu.Main;
import cu.models.StatisticsDatabase;
import cu.models.Student;
import cu.models.StudentDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by T on 01/04/2016.
 */
public class AnalyticsTabViewController
{
    StatisticsDatabase statisticsDatabase = new StatisticsDatabase("stats");
    StudentDatabase studentDatabase = new StudentDatabase("students");
    @FXML
    Label bottomRightLabel;
    @FXML
    Label bottomLeftLabel;
    @FXML
    Label topLeftLabel;
    @FXML
    Label topRightLabel;

    @FXML
    LineChart topLeftChart;
    @FXML
    LineChart topRightChart;
    @FXML
    PieChart bottomLeftPieChart;
    @FXML
    BarChart bottomRightBarChart;

    private int globalFaultyReturns = 0;
    private int globalTotalReturns = 0;

    @FXML
    private void initialize()
    {
        computeGlobalReturns();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(new PieChart.Data("Faulty",globalFaultyReturns), new PieChart.Data("Functioning", globalTotalReturns));
        bottomLeftPieChart.setData(pieChartData);
    }

    private void computeGlobalReturns()
    {
        List<Student> studentList = studentDatabase.getAllStudents();
        for(Student student : studentList)
        {
            globalFaultyReturns+=student.getFaultyReturns();
            globalTotalReturns+=student.getTotalReturns();
        }
    }
}
