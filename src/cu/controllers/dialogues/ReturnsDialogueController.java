package cu.controllers.dialogues;

import cu.interfaces.CodeScannerInterface;
import cu.models.equipment.Equipment;
import cu.models.equipment.EquipmentDatabase;
import cu.models.equipment.EquipmentOnLoan;
import cu.models.listeners.CodeScannedListener;
import cu.models.statistics.Statistics;
import cu.models.statistics.StatisticsDatabase;
import cu.models.students.CurrentStudent;
import cu.models.students.Student;
import cu.models.students.StudentDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.controlsfx.control.CheckListView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by T on 29/03/2016.
 */
public class ReturnsDialogueController implements CodeScannerInterface
{
    @FXML
    private CheckListView<Equipment> itemsToScanList = new CheckListView<>();
    @FXML
    private Label itemsLeftLabel;
    @FXML
    private Button confirmButton;
    StatisticsDatabase statisticsDatabase = new StatisticsDatabase("stats");
    private StudentDatabase studentDatabase;
    private EquipmentDatabase equipmentDatabase;
    private Stage dialogStage;
    private int itemCount = -1;
    EquipmentOnLoan equipmentOnLoan;
    ArrayList<Equipment> scannedEquipmentList = new ArrayList<>();
    TableView<EquipmentOnLoan> leasedTableView;
    @FXML
    private void initialize()
    {
        CodeScannedListener.activateAgent(this);
        confirmButton.setDisable(true);
    }

    @Override
    public void onCodeScanner(String QRCode)
    {
        System.out.println(QRCode);
        Pattern intsOnly = Pattern.compile("^[\\d]*");
        Matcher makeMatch = intsOnly.matcher(QRCode);
        makeMatch.find();
        String inputInt = makeMatch.group();
        int inputToInt = -1;
        try
        {
            inputToInt = Integer.parseInt(inputInt);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
        Equipment scannedEquipment = equipmentDatabase.getItem(inputToInt);
        if(scannedEquipment != null)
        {
            if(itemsToScanList.getItems().contains(scannedEquipment) && !scannedEquipmentList.contains(scannedEquipment))
            {
                scannedEquipmentList.add(scannedEquipment);
                itemCount--;
                itemsLeftLabel.setText(itemCount+"");
                if(itemCount == 0)
                {
                    confirmButton.setDisable(false);

                    //System.out.println("DEAL WITH REMOVAL OF STUDENT & DATABASE OPERATIONS"); (Method)
                }
            }
        }
    }

    public void setup(Stage dialogStage, StudentDatabase studentDatabase, EquipmentDatabase equipmentDatabase,  EquipmentOnLoan equipmentOnLoan, TableView<EquipmentOnLoan> leasedTableView)
    {
        this.dialogStage = dialogStage;
        this.dialogStage.setOnCloseRequest(e -> scannedEquipmentList.clear());
        this.dialogStage.setResizable(false);
        this.studentDatabase = studentDatabase;
        this.equipmentDatabase = equipmentDatabase;
        this.equipmentOnLoan = equipmentOnLoan;
        this.leasedTableView = leasedTableView;
        ObservableList<Equipment> list = FXCollections.observableArrayList(equipmentOnLoan.getEquipmentIDs());
        itemsToScanList.setItems(list);
        itemCount = list.size();
        itemsLeftLabel.setText(itemCount+"");
    }
    @FXML
    private void onConfirm()
    {
        int faultyItems = 0;
        for(int i = 0; i < itemsToScanList.getItems().size(); i++)
        {
            if(itemsToScanList.getCheckModel().isChecked(i))
            {
                System.out.println(itemsToScanList.getItems().get(i) + " is Checked!");
            }
            else if(!itemsToScanList.getCheckModel().isChecked(i))
            {
                faultyItems++;
                itemsToScanList.getItems().get(i).setFunctional(false);
                equipmentDatabase.editEquipmentEntry(itemsToScanList.getItems().get(i), itemsToScanList.getItems().get(i).getItemID());
            }
        }
        Student temp = CurrentStudent.getInstance().getLoadedStudent();
        temp.setFaultyReturns(temp.getFaultyReturns()+faultyItems);
        //another int to check number of returns ONTIME / NOT
        temp.setTotalReturns(temp.getTotalReturns()+itemsToScanList.getItems().size());
        temp.setEquipmentUsageTime(temp.getEquipmentUsageTime()+equipmentOnLoan.getLeaseTimeDuration());
        studentDatabase.editStudentEntry(temp);
        equipmentOnLoan.stopTimer(true);
        leasedTableView.getItems().remove(equipmentOnLoan);
        Statistics loadStat = statisticsDatabase.doesExist(LocalDate.now());
        if(loadStat != null)
        {
            statisticsDatabase.editEntry(new Statistics(LocalDate.now(), loadStat.getAverageUsage()+equipmentOnLoan.getLeaseTimeDuration(), loadStat.getFaultyReturns()+faultyItems, loadStat.getTotalReturns()+itemsToScanList.getItems().size()));
        }
        else
        {
            statisticsDatabase.addEntry(new Statistics(LocalDate.now(), equipmentOnLoan.getLeaseTimeDuration(), faultyItems, itemsToScanList.getItems().size()));
        }
        dialogStage.fireEvent(new WindowEvent(dialogStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        dialogStage.close();
    }
}
