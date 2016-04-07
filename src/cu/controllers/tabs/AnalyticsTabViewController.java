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
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.controlsfx.control.RangeSlider;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


/**
 * Created by T on 01/04/2016.
 */
public class AnalyticsTabViewController
{
    private StatisticsDatabase statisticsDatabase = new StatisticsDatabase("stats");
    private StudentDatabase studentDatabase = new StudentDatabase("students");
    private EquipmentDatabase equipmentDatabase = new EquipmentDatabase("equipment");

    @FXML
    private Label bottomRightLabel;
    @FXML
    private Label topLeftLabel;
    @FXML
    private Label topRightLabel;
    @FXML
    private Label numberOfStudentsComputed;
    @FXML
    private ComboBox<String> pieChartComboBox;
    @FXML
    private LineChart<String, Number> topLeftChart;
    @FXML
    private PieChart bottomLeftPieChart;
    @FXML
    private BarChart<String, Integer> bottomRightBarChart;
    @FXML
    private RangeSlider test;
    @FXML
    private ComboBox<String> topComboBox;
    private ObservableList<String> listOfPieChartOptions = FXCollections.observableArrayList("Functional Vs Faulty Returns", "Faulty Returns By Course Contribution");
    private ObservableList<String> listOfLineChartOptions = FXCollections.observableArrayList("Average usage", "Item returns condition");

    private Map<LocalDate, Long> sortedMap = new TreeMap<>();
    private List<LocalDate> ss = new ArrayList<>();
    private List<LocalDate> t1 = new ArrayList<>();

    private TreeMap<LocalDate, Integer> lineC = new TreeMap<>();
    private TreeMap<LocalDate, Integer> lineC2 = new TreeMap<>();
    @FXML
    private void initialize()
    {
        /*for(int i = 0; i < 12; i++)
        {
            for(int j = 1; j < 28; j++)
            {
                statisticsDatabase.addEntry(new Statistics(LocalDate.of(2017, i+1, j),ThreadLocalRandom.current().nextLong(650000), ThreadLocalRandom.current().nextInt(0,20), ThreadLocalRandom.current().nextInt(20,30)));
            }
        }*/

        topLeftChart.setLegendVisible(false);
        topComboBox.setItems(listOfLineChartOptions);
        topComboBox.setValue(listOfLineChartOptions.get(0));
        topComboBox.valueProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue.equals(listOfLineChartOptions.get(0)))
            {
                if(!ss.isEmpty())
                {
                    computeLineChartForPeriod(ss.get((int)test.getLowValue()),ss.get((int)test.getHighValue()));
                }
                else
                {
                    computeLineChart();
                }
            }
            else
            {
                if(!t1.isEmpty())
                {
                    computeLineReturnsForPeriod(t1.get((int)test.getLowValue()),t1.get((int)test.getHighValue()));
                }
                else
                {
                    computeLineReturns();
                }
            }
        });
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
        setupLineChart();
        computeLineReturns();
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
        globalTotalReturns = globalTotalReturns-globalFaultyReturns;
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
    }

    private void computeLineReturns()
    {
        List<Statistics> statisticsList = statisticsDatabase.getAll();

        for(Statistics s : statisticsList)
        {
            t1.add(s.getDate());
            lineC.put(s.getDate(), s.getTotalReturns());
            lineC2.put(s.getDate(), s.getFaultyReturns());
        }
        XYChart.Series<String, Number> tot = new XYChart.Series<>();
        XYChart.Series<String, Number> fault = new XYChart.Series<>();
        lineC.keySet().stream().forEach(e -> tot.getData().add(new XYChart.Data<>(e.toString(),lineC.get(e))));
        lineC2.keySet().stream().forEach(e -> fault.getData().add(new XYChart.Data<>(e.toString(),lineC2.get(e))));

       // topLeftChart.getData().clear();
        //topLeftChart.getData().addAll(tot,fault);
        System.out.println(lineC);
        System.out.println(lineC2);

    }
    private void computeLineReturnsForPeriod(LocalDate startDate, LocalDate endDate)
    {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        System.out.println(startDate.toString() + endDate.toString());
        lineC.keySet().stream().filter(s -> s.isEqual(startDate) || s.isEqual(endDate) || (s.isAfter(startDate) && s.isBefore(endDate))).forEach(s -> series.getData().add(new XYChart.Data<>(s.toString(), lineC.get(s))));
        lineC2.keySet().stream().filter(s -> s.isEqual(startDate) || s.isEqual(endDate) || (s.isAfter(startDate) && s.isBefore(endDate))).forEach(s -> series2.getData().add(new XYChart.Data<>(s.toString(), lineC2.get(s))));
        topLeftChart.getData().clear();
        topLeftChart.getData().addAll(series,series2);
        topLeftChart.getXAxis().setLabel("Day");
        topLeftChart.getYAxis().setLabel("Number of Returns");
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
            //Populate HashMap
            if(sortedMap.containsKey(s.getDate()))
            {
                numberOfOccurrencesPerMonth[s.getDate().getMonth().getValue()-1]++;
                sortedMap.put(s.getDate(), sortedMap.get(s.getDate()) + s.getAverageUsage());
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
        for(LocalDate s : sortedMap.keySet())
        {
            ss.add(s);
            series.getData().add(new XYChart.Data<>(s.toString(), sortedMap.get(s)/3600));
        }
        topLeftChart.getData().clear();
        topLeftChart.getData().add(series);
        topLeftChart.getXAxis().setLabel("Day");
        topLeftChart.getYAxis().setLabel("Average usage of equipment (hours)");
    }

    private void computeLineChartForPeriod(LocalDate startDate, LocalDate endDate)
    {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        System.out.println(startDate.toString() + endDate.toString());
        sortedMap.keySet().stream().filter(s -> s.isEqual(startDate) || s.isEqual(endDate) || (s.isAfter(startDate) && s.isBefore(endDate))).forEach(s -> series.getData().add(new XYChart.Data<>(s.toString(), sortedMap.get(s) / 3600)));
        topLeftChart.getData().clear();
        topLeftChart.getData().add(series);
        topLeftChart.getXAxis().setLabel("Day");
        topLeftChart.getYAxis().setLabel("Average usage of equipment (hours)");
    }

    private void setupLineChart()
    {
        topLeftLabel.setText("");
        test.setLabelFormatter(new StringConverter<Number>()
        {
            @Override
            public String toString(Number object)
            {
                if(ss.size() > 0)
                {
                    return ss.get(object.intValue()).toString();
                }
                return "No Data";
            }

            @Override
            public Number fromString(String string)
            {
                for(int i = 0; i<ss.size();i++)
                {
                    if(ss.get(i).toString().equalsIgnoreCase(string))
                    {
                        return i;
                    }
                }
                return 0;
            }
        });
        test.setShowTickMarks(true);
        test.setShowTickLabels(true);

        if(ss.size() > 0)
        {
            test.setMax(ss.size()-1);
            test.lowValueProperty().setValue(0);
            test.highValueProperty().setValue(ss.size()-1);
            test.highValueChangingProperty().addListener((observable, oldValue, newValue) ->
            {
                if(topComboBox.getSelectionModel().getSelectedIndex() == 0)
                {
                    topLeftChart.getData().clear();
                    topLeftLabel.setText("Showing the period between " +ss.get((int)test.getLowValue()) + " and " + ss.get((int) test.getHighValue()));
                    computeLineChartForPeriod(ss.get((int)test.getLowValue()), ss.get((int) test.getHighValue()));
                }
                else
                {
                    topLeftChart.getData().clear();
                    computeLineReturnsForPeriod(t1.get((int) test.getLowValue()), t1.get((int) test.getHighValue()));
                }
            });
            test.lowValueChangingProperty().addListener(((observable, oldValue, newValue) ->
            {
                if(!test.isHighValueChanging())
                {
                    if(topComboBox.getSelectionModel().getSelectedIndex() == 0)
                    {
                        topLeftLabel.setText("Showing the period between " + ss.get((int) test.getLowValue()) + " and " + ss.get((int) test.getHighValue()));
                        topLeftChart.getData().clear();
                        computeLineChartForPeriod(ss.get((int) test.getLowValue()), ss.get((int) test.getHighValue()));
                    }
                    else
                    {
                        topLeftChart.getData().clear();
                        computeLineReturnsForPeriod(t1.get((int) test.getLowValue()), t1.get((int) test.getHighValue()));
                    }
                }
            }));
            topLeftLabel.setText("Showing the period between " +ss.get(0) + " and " + ss.get(ss.size()-1));
        }

    }
}
