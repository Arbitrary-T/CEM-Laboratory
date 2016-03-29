package cu.controllers.dialogues;

import cu.interfaces.CodeScannerInterface;
import cu.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.controlsfx.control.CheckListView;

import java.util.List;
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
    private StudentDatabase studentDatabase;
    private EquipmentDatabase equipmentDatabase;
    private Stage dialogStage;
    private int itemCount = -1;
    @FXML
    private void initialize()
    {
        CodeScannerCOM.activateAgent(this);
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
        Equipment s = equipmentDatabase.getItem(inputToInt);
        if(s != null)
        {
            if(itemsToScanList.getItems().contains(s))
            {
                itemCount--;
                itemsLeftLabel.setText(itemCount+"");
                if(itemCount == 0)
                {
                    confirmButton.setDisable(false);
                    //System.out.println("DEAL WITH REMOVAL OF STUDENT & DATABASE OPERATIONS");
                }
            }
        }
    }

    public void setup(Stage dialogStage, StudentDatabase studentDatabase, EquipmentDatabase equipmentDatabase, List<Equipment> listOfItems)
    {
        this.dialogStage = dialogStage;
        this.dialogStage.setResizable(false);
        this.studentDatabase = studentDatabase;
        this.equipmentDatabase = equipmentDatabase;
        ObservableList<Equipment> list = FXCollections.observableArrayList(listOfItems);
        itemsToScanList.setItems(list);
        itemCount = listOfItems.size();
        itemsLeftLabel.setText(itemCount+"");
    }
    @FXML
    private void onConfirm()
    {

    }
}
