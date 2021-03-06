package cu.controllers.tabs;

import cu.models.equipment.EquipmentDatabase;
import cu.models.statistics.StatisticsDatabase;
import cu.models.students.StudentDatabase;
import cu.models.utilities.PropertiesManager;
import gnu.io.CommPortIdentifier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Enumeration;
import java.util.Optional;

/**
 * Created by T on 06/04/2016.
 */
public class SettingsTabViewController
{
    @FXML
    private TextField emailAddressTextField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> portsComboBox;

    private ObservableList<String>  portsComboBoxOptions = FXCollections.observableArrayList();
    private PropertiesManager propertiesManager = new PropertiesManager();

    @FXML
    private void initialize()
    {
        populateComboBox();
        portsComboBox.setItems(portsComboBoxOptions);
    }

    /**
     * populates a combobox with available COM ports
     */
    private void populateComboBox()
    {
        CommPortIdentifier portId;
        Enumeration portList;

        portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements())
        {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
                portsComboBoxOptions.add(portId.getName());
            }
        }
    }

    /**
     * saves the entered details via the property manager
     */
    @FXML
    private void onSaveGmailSettingsButton()
    {
        //Could be enhanced by encrypting the data
        if(!emailAddressTextField.getText().isEmpty() && !passwordField.getText().isEmpty())
        {
            propertiesManager.setProperties("email", emailAddressTextField.getText());
            propertiesManager.setProperties("password", passwordField.getText());
        }
        else
        {
            Alert emptyFields = new Alert(Alert.AlertType.INFORMATION);
            emptyFields.setTitle("Error");
            emptyFields.setHeaderText("Invalid input!");
            emptyFields.setContentText("Please make sure that the email and password fields are valid.");
            emptyFields.show();
        }
    }

    /**
     * saves the default COM port to use via the property manager
     */
    @FXML
    private void onSavePortDefaultButton()
    {
        String selection = portsComboBox.getSelectionModel().getSelectedItem();
        if(selection != null)
        {
            propertiesManager.setProperties("default.port", selection);
        }
    }

    /**
     * completely delete the database
     */
    @FXML
    private void onEquipmentCompleteWipe()
    {
        EquipmentDatabase equipmentDatabase = new EquipmentDatabase("equipment");
        Optional<String> userInput = generateDialog("Warning!", "This can not be undone!","Please enter \"I understand\"").showAndWait();
        if(userInput.isPresent())
        {
            if(userInput.get().equals("I understand"))
            {
                equipmentDatabase.deleteAllEntries();
            }
        }
    }

    @FXML
    private void onStudentCompleteWipe()
    {
        StudentDatabase studentDatabase = new StudentDatabase("students");
        Optional<String> userInput = generateDialog("Warning!", "This can not be undone!","Please enter \"I understand\"").showAndWait();
        if(userInput.isPresent())
        {
            if(userInput.get().equals("I understand"))
            {
                studentDatabase.deleteAll();
            }
        }
    }

    @FXML
    private void onStatCompleteWipe()
    {
        StatisticsDatabase statisticsDatabase = new StatisticsDatabase("stats");
        Optional<String> userInput = generateDialog("Warning!", "This can not be undone!","Please enter \"I understand\"").showAndWait();
        if(userInput.isPresent())
        {
            if(userInput.get().equals("I understand"))
            {
                statisticsDatabase.deleteAll();
            }
        }
    }

    /**
     * generates a dialog box
     * @param title the title of the dialog box
     * @param header the header of the dialog box
     * @param content the contents of the dialog box
     * @return returns whether the user's input
     */
    private TextInputDialog generateDialog(String title, String header, String content)
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        return dialog;
    }
}