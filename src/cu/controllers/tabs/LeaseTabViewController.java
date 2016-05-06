package cu.controllers.tabs;

import cu.Main;
import cu.controllers.dialogues.NewRegistrationDialogueController;
import cu.controllers.dialogues.ReturnsDialogueController;
import cu.interfaces.CardInterface;
import cu.interfaces.CodeScannerInterface;
import cu.models.equipment.Equipment;
import cu.models.equipment.EquipmentDatabase;
import cu.models.equipment.EquipmentOnLoan;
import cu.models.listeners.CardListener;
import cu.models.listeners.CodeScannedListener;
import cu.models.students.CurrentStudent;
import cu.models.students.Student;
import cu.models.students.StudentDatabase;
import cu.validations.TextValidation;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.IOException;

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
    private Label returnsNotOnTimeLabel;
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
    private ImageView coventryLogo;
    @FXML
    private TextArea remarksTextArea;
    @FXML
    private ComboBox<String> timeComboBox;
    @FXML
    private TextField customTimeTextField;
    @FXML
    private Button confirmLeaseButton;
    @FXML
    private Button clearOptionsButton;
    @FXML
    private PieChart totalNumberOfReturns;
    @FXML
    private ListView<Equipment> selectedItemsListView;
    @FXML
    private HBox mainPane;
    private TextValidation textValidation = new TextValidation();
    private TabPane rootPane;
    private ObservableList<EquipmentOnLoan> itemsOnLeaseObservableList = FXCollections.observableArrayList();

    private Runnable configureStudentCard = () ->
    {
        stdNameLabel.setText(CurrentStudent.getInstance().getLoadedStudent().getStudentName());
        stdIDLabel.setText(""+ CurrentStudent.getInstance().getLoadedStudent().getStudentID());
        stdEmailLabel.setText(CurrentStudent.getInstance().getLoadedStudent().getStudentEmail());
        stdCourseLabel.setText(CurrentStudent.getInstance().getLoadedStudent().getStudentCourse());
        stdPhoneNumberLabel.setText(CurrentStudent.getInstance().getLoadedStudent().getStudentPhoneNumber());
    };

    private Runnable openReturnsWindow = () ->
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
            controller.setup(dialogStage, studentDatabase, equipmentDatabase, getEquipmentOnLoan(CurrentStudent.getInstance().getLoadedStudent()), leasedItemsTableView);
            dialogStage.setOnCloseRequest((event ->
            {
                isReturnsWindowOpen = false;
                Platform.runLater(this::clearMainStudent);
            }));
            dialogStage.showAndWait();

        }
        catch(IOException e)
        {
            // Exception gets thrown if the fxml file could not be loaded
            e.printStackTrace();
        }
    };

    /**
     * sets up the view-variables and the leasedItems TableView
     */
    @FXML
    private void initialize()
    {
        CardListener.activateAgent(this);
        CodeScannedListener.activateAgent(this);

        totalNumberOfReturns.setLabelsVisible(false);
        totalNumberOfReturns.setVisible(false);
        leasedItemsTableView.setPlaceholder(new Label("No borrowed items!"));
        customTimeTextField.disableProperty().bind(timeComboBox.getSelectionModel().selectedItemProperty().isNotEqualTo("CUSTOM"));
        timeComboBox.setItems(timeComboBoxOptions);
        timeComboBox.setValue(timeComboBoxOptions.get(2));
        leftAnchorPane.maxWidthProperty().bind(mainVerticalSplitPane.widthProperty().multiply(0.2));
        totalNumberOfReturns.maxWidthProperty().bind(leftAnchorPane.widthProperty());
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
        itemTableColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getEquipmentIDs().toString()));
        timeLeftTableColumn.setEditable(false);
        timeLeftTableColumn.setCellValueFactory(p -> p.getValue().getLeaseTimeLeft());
        leasedOnTableColumn.setEditable(false);
        leasedOnTableColumn.setCellValueFactory(p->p.getValue().getLeaseStartTime());
        leasedToTableColumn.setEditable(false);
        leasedToTableColumn.setCellValueFactory(p-> new ReadOnlyObjectWrapper<>(p.getValue().getStudent().getStudentName()+" ("+p.getValue().getStudent().getStudentID() +")"));
        remarksTableColumn.setEditable(false);
        remarksTableColumn.setCellValueFactory(p-> new ReadOnlyObjectWrapper<>(p.getValue().getRemarks()));
    }

    /**
     * loads the student into the CurrentStudent singleton, if they do not exist a registration window is opened
     * @param cardUID the scanned NFC Card's Unique Identification string
     */
    @Override
    public void onCardScanned(String cardUID)
    {
        System.out.println("Card scanned event received: " + System.currentTimeMillis());
        //If the student is in the database, the CardImage update's with the student details.
        CurrentStudent.getInstance().setLoadedStudent(studentDatabase.searchDatabase(cardUID));
        if(CurrentStudent.getInstance().getLoadedStudent() != null)
        {
            Platform.runLater(()->
            {
                if(!totalNumberOfReturns.isVisible())
                {
                    returnsNotOnTimeLabel.setText("Number of times not returned on time: " + CurrentStudent.getInstance().getLoadedStudent().getReturnNotOnTime());
                    totalNumberOfReturns.setTitle("Total number of returns");
                    totalNumberOfReturns.setVisible(true);
                    ObservableList<Data> pieChartData = FXCollections.observableArrayList(new Data("Faulty", CurrentStudent.getInstance().getLoadedStudent().getFaultyReturns()), new Data("Functioning", CurrentStudent.getInstance().getLoadedStudent().getTotalReturns() - CurrentStudent.getInstance().getLoadedStudent().getFaultyReturns()));
                    totalNumberOfReturns.setData(pieChartData);
                }
            });
        }

        //else if the student is not in the database open a new window and register the student
        if(!isRegistrationWindowOpen && CurrentStudent.getInstance().getLoadedStudent() == null) {
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
                        if(CurrentStudent.getInstance().getLoadedStudent() != null)
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
        else if(CurrentStudent.getInstance().getLoadedStudent() != null && studentExistsInTable() && !isReturnsWindowOpen)
        {

            Platform.runLater(configureStudentCard);
            Platform.runLater(openReturnsWindow);
        }
        else if (!isRegistrationWindowOpen)
        {
            Platform.runLater(configureStudentCard);
        }
    }

    /**
     * handles the QR code scan  for a new lease
     * @param QRCode the QR label scanned
     */
    @Override
    public void onCodeScanner(String QRCode)
    {
        rootPane = (TabPane) mainPane.getParent().getParent().getParent().getChildrenUnmodifiable().get(0);

        if(rootPane.getSelectionModel().getSelectedIndex() == 0)
        {
            if(CurrentStudent.getInstance().getLoadedStudent() != null && !isReturnsWindowOpen)
            {
                int inputToInt = textValidation.textToFirstInt(QRCode);
                Equipment s = equipmentDatabase.getItem(inputToInt);
                if(s != null)
                {
                    if (!scannedItems.contains(s))
                    {
                        if(!itemAlreadyExists(s))
                        {
                            if(s.isFunctional())
                            {
                                scannedItems.add(s);
                            }
                            else
                            {
                                alertBuilder("Error!", "Faulty item", "The item you're attempting to borrow is faulty!");
                            }
                        }
                        else
                        {
                            alertBuilder("Error!", "Item lent already!", "Please make sure that the item is returned by the borrower!");
                        }
                    }
                    selectedItemsListView.setItems(scannedItems);
                }
            }
            else if(CurrentStudent.getInstance().getLoadedStudent() == null && !isReturnsWindowOpen)
            {
                alertBuilder("Error!", "Student/Items not found!", "Please scan your student card and the wanted items prior to confirming!");
            }
        }
    }

    /**
     * checks if a student has item(s) leased to
     * @return true if the system already leased equipment
     */
    private boolean studentExistsInTable()
    {
        for(EquipmentOnLoan equipmentOnLoan:leasedItemsTableView.getItems())
        {
            if(equipmentOnLoan.getStudent().equals(CurrentStudent.getInstance().getLoadedStudent()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * checks if an item is already leased
     * @param scannedItem the QR code scanned by the user
     * @return true if the item is already leased
     */
    private boolean itemAlreadyExists(Equipment scannedItem)
    {
        for(EquipmentOnLoan equipmentOnLoan:leasedItemsTableView.getItems())
        {
            if(equipmentOnLoan.getEquipmentIDs().contains(scannedItem))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * returns the equipment leased by a student
     * @param student the Student with the intended lease
     * @return EuipmentOnLoan pojo
     */
    private EquipmentOnLoan getEquipmentOnLoan(Student student)
    {
        for(EquipmentOnLoan equipmentOnLoan : leasedItemsTableView.getItems())
        {
            if(equipmentOnLoan.getStudent().equals(student))
            {
                return equipmentOnLoan;
            }
        }
        return null;
    }

    /**
     * creates a dialog window
     * @param title of the window
     * @param header header of the window
     * @param content of the window
     */
    private void alertBuilder(String title, String header, String content)
    {
        Alert notifyCardReader = new Alert(Alert.AlertType.INFORMATION);
        notifyCardReader.setTitle(title);
        notifyCardReader.setHeaderText(header);
        notifyCardReader.setContentText(content);
        notifyCardReader.show();
    }

    /**
     * clears the GUI and resets the loaded student
     */
    private void clearMainStudent()
    {
        CurrentStudent.getInstance().setLoadedStudent(null);
        totalNumberOfReturns.setVisible(false);
        VBox s = (VBox) studentDetailsTextGroup.getChildren().get(0);
        for(int i=0;i<5; i++)
        {
            Label temp = (Label) s.getChildren().get(i);
            temp.setText("");
        }
    }

    /**
     * adds a new entry to the leases tableview
     */
    @FXML
    private void onConfirmButtonClicked()
    {
        if(selectedItemsListView.getItems().size() > 0)
        {
            if(CurrentStudent.getInstance().getLoadedStudent() != null)
            {
                switch(timeComboBox.getSelectionModel().getSelectedIndex())
                {
                    case 0:
                        itemsOnLeaseObservableList.add(new EquipmentOnLoan(studentDatabase, CurrentStudent.getInstance().getLoadedStudent(), scannedItems.subList(0,scannedItems.size()),1,remarksTextArea.getText()));
                        break;
                    case 1:
                        itemsOnLeaseObservableList.add(new EquipmentOnLoan(studentDatabase, CurrentStudent.getInstance().getLoadedStudent(), scannedItems.subList(0,scannedItems.size()),2,remarksTextArea.getText()));
                        break;
                    case 2:
                        itemsOnLeaseObservableList.add(new EquipmentOnLoan(studentDatabase, CurrentStudent.getInstance().getLoadedStudent(), scannedItems.subList(0,scannedItems.size()),3,remarksTextArea.getText()));
                        break;
                    case 3:
                        try
                        {
                            itemsOnLeaseObservableList.add(new EquipmentOnLoan(studentDatabase, CurrentStudent.getInstance().getLoadedStudent(), scannedItems.subList(0,scannedItems.size()),Integer.parseInt(customTimeTextField.getText()),remarksTextArea.getText()));
                        }
                        catch (NumberFormatException e)
                        {
                            alertBuilder("Error!", "Unrecognised input!", "Please make sure the time entered is a whole number!");
                        }
                        break;
                }

            }
            onClearButtonClicked();
        }
        else if(CurrentStudent.getInstance().getLoadedStudent() != null)
        {
            alertBuilder("Error!", "No items scanned!", "Please scan the wanted items prior to confirming!");
        }
        else
        {
            alertBuilder("Error!", "Student not found!", "Please scan your student card prior to scanning items!");
        }
    }

    /**
     * clears the GUI TextFields
     */
    @FXML
    private void onClearButtonClicked()
    {
        searchDatabaseTextField.clear();
        customTimeTextField.clear();
        scannedItems.clear();
        remarksTextArea.clear();
        returnsNotOnTimeLabel.setText("");
        clearMainStudent();
    }
}
