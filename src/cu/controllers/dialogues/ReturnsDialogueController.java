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
import cu.validations.TextValidation;
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

    private int itemCount = -1;
    private StatisticsDatabase statisticsDatabase = new StatisticsDatabase("stats");
    private StudentDatabase studentDatabase;
    private EquipmentDatabase equipmentDatabase;
    private Stage dialogStage;
    private EquipmentOnLoan equipmentOnLoan;
    private ArrayList<Equipment> scannedEquipmentList = new ArrayList<>();
    private TableView<EquipmentOnLoan> leasedTableView;
    private TextValidation textValidation = new TextValidation();

    /**
     * sets up a listener to the COM port
     */
    @FXML
    private void initialize()
    {
        CodeScannedListener.activateAgent(this);
        confirmButton.setDisable(true);
    }

    /**
     * once a qr code is scanned, the items are removed from the 'leased' items list
     * @param QRCode the QR Code's data
     */
    @Override
    public void onCodeScanner(String QRCode)
    {
        int equipmentId = textValidation.textToFirstInt(QRCode);
        Equipment scannedEquipment = equipmentDatabase.getItem(equipmentId);
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
                }
            }
        }
    }

    /**
     * set's up the dialog
     * @param dialogStage the dialog itself
     * @param studentDatabase the database to edit
     * @param equipmentDatabase the equipment to edit
     * @param equipmentOnLoan the loaned items object
     * @param leasedTableView the table to edit
     */
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

    /**
     * the input is validated and the student/equipment databases are updated accordingly
     */
    @FXML
    private void onConfirm()
    {
        int faultyItems = 0;
        for(int i = 0; i < itemsToScanList.getItems().size(); i++)
        {
            if(!itemsToScanList.getCheckModel().isChecked(i))
            {
                faultyItems++;
                itemsToScanList.getItems().get(i).setFunctional(false);
                equipmentDatabase.editEquipmentEntry(itemsToScanList.getItems().get(i), itemsToScanList.getItems().get(i).getItemID());
            }
        }
        Student currentStudent = CurrentStudent.getInstance().getLoadedStudent();
        currentStudent.setFaultyReturns(currentStudent.getFaultyReturns() + faultyItems);
        currentStudent.setTotalReturns(currentStudent.getTotalReturns()+itemsToScanList.getItems().size());
        currentStudent.setEquipmentUsageTime(currentStudent.getEquipmentUsageTime()+equipmentOnLoan.getLeaseTimeDuration());
        studentDatabase.editStudentEntry(currentStudent);
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
