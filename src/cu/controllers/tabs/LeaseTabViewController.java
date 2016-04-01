package cu.controllers.tabs;

import cu.Main;
import cu.controllers.MainViewController;
import cu.controllers.dialogues.NewRegistrationDialogueController;
import cu.controllers.dialogues.ReturnsDialogueController;
import cu.interfaces.CardInterface;
import cu.interfaces.CodeScannerInterface;
import cu.models.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by T on 08/11/2015.
 */

public class LeaseTabViewController implements CardInterface, CodeScannerInterface
{
    //Non-view related variables
    private StudentDatabase studentDatabase = new StudentDatabase("students");
    private EquipmentDatabase equipmentDatabase = new EquipmentDatabase("equipment");

    private boolean isRegistrationWindowOpen = false;
    private boolean isReturnsWindowOpen = false;
    private ObservableList<String> timeComboBoxOptions = FXCollections.observableArrayList("1 Hour", "2 Hours", "3 Hours", "CUSTOM");
    private ObservableList<Equipment> scannedItems = FXCollections.observableArrayList();

    //View related variables
    @FXML
    private AnchorPane leftAnchorPane;
    @FXML
    private SplitPane mainVerticalSplitPane;
    @FXML
    private ImageView studentCardBack;
    @FXML
    private TableView<EquipmentOnLoan> leasedItemsTableView;
    @FXML
    private TableColumn<EquipmentOnLoan, String> itemTableColumn;
    @FXML
    private TableColumn<EquipmentOnLoan, String> leasedToTableColumn;
    @FXML
    private TableColumn<EquipmentOnLoan, String> leasedOnTableColumn;
    @FXML
    private TableColumn<EquipmentOnLoan, String> timeLeftTableColumn;
    @FXML
    private TableColumn<EquipmentOnLoan, String> remarksTableColumn;
    @FXML
    private TextField searchDatabaseTextField;
    @FXML
    private ListView equipmentResultsListView;
    @FXML
    private Label stdNameLabel;
    @FXML
    private Label stdIDLabel;
    @FXML
    private Label stdEmailLabel;
    @FXML
    private Label stdCourseLabel;
    @FXML
    private Label stdPhoneNumberLabel;
    @FXML
    private VBox labelsVBox;
    @FXML
    private Group studentDetailsTextGroup;
    @FXML
    private BarChart barChart;
    @FXML
    private ImageView coventryLogo;
    @FXML
    private TextArea remarksTextArea;
    @FXML
    private ComboBox timeComboBox;
    @FXML
    private TextField customTimeTextField;
    @FXML
    private Button confirmLeaseButton;
    @FXML
    private Button clearOptionsButton;
    @FXML
    private ListView<Equipment> selectedItemsListView;

    private ObservableList<EquipmentOnLoan> itemsOnLeaseObservableList = FXCollections.observableArrayList();

    @FXML
    void initialize()
    {
        CardListener.activateAgent(this);
        CodeScannerCOM.activateAgent(this);

        timeComboBox.valueProperty().addListener((observable1, oldValue1, newValue1) ->
        {
            if(newValue1.equals("CUSTOM"))
            {
                customTimeTextField.setDisable(false);
            }
            else
            {
                customTimeTextField.setDisable(true);
            }
        });
        timeComboBox.setItems(timeComboBoxOptions);
        timeComboBox.setValue(timeComboBoxOptions.get(2));
        leftAnchorPane.maxWidthProperty().bind(mainVerticalSplitPane.widthProperty().multiply(0.2));
        barChart.maxWidthProperty().bind(leftAnchorPane.widthProperty());
        studentCardBack.fitHeightProperty().bind(leftAnchorPane.heightProperty());
        studentCardBack.fitWidthProperty().bind(leftAnchorPane.widthProperty());
        studentCardBack.boundsInParentProperty().addListener((observable ->
        {
            studentDetailsTextGroup.setLayoutX(studentCardBack.layoutBoundsProperty().get().getWidth() / 3);
            studentDetailsTextGroup.setLayoutY(studentDetailsTextGroup.layoutBoundsProperty().get().getHeight());
            studentDetailsTextGroup.getTransforms().clear();
            double scale_x = (studentCardBack.getBoundsInParent().getWidth() * 0.5) / 75;
            double scale_y = (studentCardBack.getBoundsInParent().getHeight() * 0.5) / 85;
            double scale_factor = Math.min(scale_x, scale_y);
            double pivot_y = (studentDetailsTextGroup.getBoundsInLocal().getMinY() - studentDetailsTextGroup.getBoundsInLocal().getMaxY());
            Scale scale = new Scale(scale_factor, scale_factor, 0, pivot_y);
            studentDetailsTextGroup.getTransforms().add(scale);
        }));
        leasedItemsTableView.setItems(itemsOnLeaseObservableList);
        itemTableColumn.setEditable(false);
        itemTableColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getEquipmentID().toString()));
        timeLeftTableColumn.setEditable(false);
        timeLeftTableColumn.setCellValueFactory(p -> p.getValue().getLeaseTimeLeft());
        leasedOnTableColumn.setEditable(false);
        leasedOnTableColumn.setCellValueFactory(p->p.getValue().getLeaseStartTime());
        leasedToTableColumn.setEditable(false);
        leasedToTableColumn.setCellValueFactory(p-> new ReadOnlyObjectWrapper<>(p.getValue().getStudent().getStudentName()+" ("+p.getValue().getStudent().getStudentID() +")"));
        remarksTableColumn.setEditable(false);
        remarksTableColumn.setCellValueFactory(p-> new ReadOnlyObjectWrapper<>(p.getValue().getRemarks()));
        confirmLeaseButton.setOnMouseClicked(event ->
        {
            if(selectedItemsListView.getItems().size() > 0)
            {
                if(Main.currentStudent != null)
                {
                    switch(timeComboBox.getSelectionModel().getSelectedIndex())
                    {
                        case 0:
                            itemsOnLeaseObservableList.add(new EquipmentOnLoan(Main.currentStudent, scannedItems.subList(0,scannedItems.size()),1,remarksTextArea.getText()));
                            break;
                        case 1:
                            itemsOnLeaseObservableList.add(new EquipmentOnLoan(Main.currentStudent, scannedItems.subList(0,scannedItems.size()),2,remarksTextArea.getText()));
                            break;
                        case 2:
                            itemsOnLeaseObservableList.add(new EquipmentOnLoan(Main.currentStudent, scannedItems.subList(0,scannedItems.size()),3,remarksTextArea.getText()));
                            break;
                        case 3:
                            try
                            {
                                itemsOnLeaseObservableList.add(new EquipmentOnLoan(Main.currentStudent, scannedItems.subList(0,scannedItems.size()),Integer.parseInt(customTimeTextField.getText()),remarksTextArea.getText()));
                            }
                            catch (NumberFormatException e)
                            {
                                Alert notifyCardReader = new Alert(Alert.AlertType.INFORMATION);
                                notifyCardReader.setTitle("Error!");
                                notifyCardReader.setHeaderText("Unrecognised input!");
                                notifyCardReader.setContentText("Please make sure the time entered is a whole number!");
                                notifyCardReader.show();
                                e.printStackTrace();
                            }
                            break;
                    }

                }
                clearOptionsButton.fireEvent(event);
            }
            else if(Main.currentStudent != null)
            {
                Alert notifyCardReader = new Alert(Alert.AlertType.INFORMATION);
                notifyCardReader.setTitle("Error!");
                notifyCardReader.setHeaderText("No items scanned!");
                notifyCardReader.setContentText("Please scan the wanted items prior to confirming!");
                notifyCardReader.show();
            }
            else
            {
                Alert notifyCardReader = new Alert(Alert.AlertType.INFORMATION);
                notifyCardReader.setTitle("Error!");
                notifyCardReader.setHeaderText("Student not found!");
                notifyCardReader.setContentText("Please scan your student card prior to scanning items!");
                notifyCardReader.show();
            }
        });

        clearOptionsButton.setOnMouseClicked(event ->
        {
            searchDatabaseTextField.clear();
            customTimeTextField.clear();
            scannedItems.clear();
            remarksTextArea.clear();
            Main.currentStudent = null;
            VBox s = (VBox) studentDetailsTextGroup.getChildren().get(0);
            for(int i=0;i<5; i++)
            {
                Label temp = (Label) s.getChildren().get(i);
                temp.setText("");
            }
        });
    }

    @Override
    public void onCardScanned(String cardUID)
    {
        System.out.println("Card scanned event received: " + System.currentTimeMillis());
        //If the student is in the database, the CardImage update's with the student details.
        Main.currentStudent = studentDatabase.searchDatabase(cardUID);
        Runnable configureStudentCard = () ->
        {
            stdNameLabel.setText(Main.currentStudent.getStudentName());
            stdIDLabel.setText(""+ Main.currentStudent.getStudentID());
            stdEmailLabel.setText(Main.currentStudent.getStudentEmail());
            stdCourseLabel.setText(Main.currentStudent.getStudentCourse());
            stdPhoneNumberLabel.setText(Main.currentStudent.getStudentPhoneNumber());
        };
        //else if the student is not in the database open a new window and register the student
        if(!isRegistrationWindowOpen && Main.currentStudent == null) {
            System.out.println("Student does no exist in database -> Opening registration window");
            //Running the UI manipulating code in the UI thread
            Runnable openRegistrationWindow = () ->
            {
                try
                {
                    isRegistrationWindowOpen = true;
                    FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/dialogues/NewRegistrationDialogue.fxml")); // Load the fxml file and create a new stage for the popup
                    DialogPane page = loader.load();
                    Stage dialogStage = new Stage();
                    Scene scene = new Scene(page);
                    dialogStage.setScene(scene);
                    dialogStage.setTitle("New Student Registration");
                    NewRegistrationDialogueController controller = loader.getController();
                    controller.configureDialogStage(dialogStage, cardUID);
                    dialogStage.setOnCloseRequest((event ->
                    {
                        isRegistrationWindowOpen = false;
                        System.out.println("Registration window closed.");
                        if(Main.currentStudent != null)
                        {
                            Platform.runLater(configureStudentCard);
                        }
                    }));
                    dialogStage.showAndWait();
                }
                catch(IOException e)
                {
                    // Exception gets thrown if the fxml file could not be loaded
                    e.printStackTrace();
                }
            };
            Platform.runLater(openRegistrationWindow);
        }
        else if(Main.currentStudent != null && studentExistsInTable() && !isReturnsWindowOpen)
        {
            System.out.println("HI");
            Runnable openRegistrationWindow = () ->
            {
                try
                {
                    isReturnsWindowOpen = true;
                    FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/dialogues/ReturnsDialogue.fxml")); // Load the fxml file and create a new stage for the popup
                    DialogPane page = loader.load();
                    Stage dialogStage = new Stage();
                    Scene scene = new Scene(page);
                    dialogStage.setScene(scene);
                    dialogStage.setTitle("Return Items");
                    ReturnsDialogueController controller = loader.getController();
                    controller.setup(dialogStage, studentDatabase, equipmentDatabase, getLoanedItems(Main.currentStudent));
                    dialogStage.setOnCloseRequest((event ->
                    {
                        isReturnsWindowOpen = false;
                        System.out.println("Registration window closed.");
                    }));
                    dialogStage.showAndWait();

                }
                catch(IOException e)
                {
                    // Exception gets thrown if the fxml file could not be loaded
                    e.printStackTrace();
                }
            };
            Platform.runLater(configureStudentCard);
            Platform.runLater(openRegistrationWindow);
        }
        else if (!isRegistrationWindowOpen)
        {
            Platform.runLater(configureStudentCard);
        }
    }

    @Override
    public void onCodeScanner(String QRCode)
    {
        System.out.println(QRCode);
        if(MainViewController.index == 0)
        {
            if(Main.currentStudent != null && !isReturnsWindowOpen)
            {
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
                System.out.println(s.toString());
                if (!scannedItems.contains(s) && !itemAlreadyExists(s))
                {
                    scannedItems.add(s);
                }
                selectedItemsListView.setItems(scannedItems);
            }
            else if(Main.currentStudent == null && !isReturnsWindowOpen)
            {
                Alert notifyCardReader = new Alert(Alert.AlertType.INFORMATION);
                notifyCardReader.setTitle("Error!");
                notifyCardReader.setHeaderText("Student/Items not found!");
                notifyCardReader.setContentText("Please scan your student card and the wanted items prior to confirming!");
                notifyCardReader.show();
            }
        }
    }
    private boolean studentExistsInTable()
    {
        for(EquipmentOnLoan equipmentOnLoan:leasedItemsTableView.getItems())
        {
            if(equipmentOnLoan.getStudent().equals(Main.currentStudent))
            {
                return true;
            }
        }
        return false;
    }
    private boolean itemAlreadyExists(Equipment scannedItem)
    {
        for(EquipmentOnLoan equipmentOnLoan:leasedItemsTableView.getItems())
        {
            if(equipmentOnLoan.getEquipmentID().contains(scannedItem))
            {
                return true;
            }
        }
        return false;
    }
    private List<Equipment> getLoanedItems(Student student)
    {
        for(EquipmentOnLoan equipmentOnLoan : leasedItemsTableView.getItems())
        {
            if(equipmentOnLoan.getStudent().equals(student))
            {
                return equipmentOnLoan.getEquipmentID();
            }
        }
        return null;
    }
}
