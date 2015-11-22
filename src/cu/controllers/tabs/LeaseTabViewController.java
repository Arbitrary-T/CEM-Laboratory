package cu.controllers.tabs;

import cu.Main;
import cu.controllers.dialogues.NewRegistrationDialogueController;
import cu.listeners.CardInterface;
import cu.listeners.CardListener;
import cu.models.StudentDatabase;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import org.controlsfx.control.ListSelectionView;


import java.io.IOException;

/**
 * Created by T on 08/11/2015.
 */

public class LeaseTabViewController implements CardInterface {
    //Non-view related variables
    private StudentDatabase studentDatabase = new StudentDatabase("students");
    //View related variables
    @FXML
    private HBox mainPane;
    @FXML
    private AnchorPane leftAP;
    @FXML
    private SplitPane mainVerticalSplitPane;
    @FXML
    private ImageView studentCardBack;
    @FXML
    private TableView leasedItemsTableView;
    @FXML
    private Label stdNameLabel;
    @FXML
    private Label stdIDLabel;
    @FXML
    private Label stdEmailLabel;
    @FXML
    private Label stdCourseLabel;
    @FXML
    private ListSelectionView listSelectionView;
    @FXML
    private Label stdPhoneNumberLabel;
    @FXML
    private VBox labelsVBox;
    @FXML
    private Group textGroup;
    @FXML
    private AnchorPane mainBottomAP;
    @FXML
    private BarChart barChart;
    @FXML
    private ImageView coventryLogo;
    private boolean isRegistrationWindowOpen = false;
    @FXML
    void initialize()
    {
        CardListener.activateAgent(this);
        for (int i = 0; i < 20; i++)
            listSelectionView.getSourceItems().add(i);
        leftAP.maxWidthProperty().bind(mainVerticalSplitPane.widthProperty().multiply(0.2));
        barChart.maxWidthProperty().bind(leftAP.widthProperty());
        studentCardBack.fitHeightProperty().bind(leftAP.heightProperty());
        studentCardBack.fitWidthProperty().bind(leftAP.widthProperty());
        studentCardBack.boundsInParentProperty().addListener(((observable, oldValue, newValue) ->
        {
            textGroup.setLayoutX(studentCardBack.layoutBoundsProperty().get().getWidth() / 3);
            textGroup.setLayoutY(textGroup.layoutBoundsProperty().get().getHeight());
            textGroup.getTransforms().clear();
            double scale_x = (studentCardBack.getBoundsInParent().getWidth() * 0.5) / 75;
            double scale_y = (studentCardBack.getBoundsInParent().getHeight() * 0.5) / 85;
            double scale_factor = Math.min(scale_x, scale_y);
            double pivot_y = (textGroup.getBoundsInLocal().getMinY() - textGroup.getBoundsInLocal().getMaxY());
            Scale scale = new Scale(scale_factor, scale_factor, 0, pivot_y);
            textGroup.getTransforms().add(scale);
        }));
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
            stdIDLabel.setText("" + Main.currentStudent.getStudentID());
            stdEmailLabel.setText(Main.currentStudent.getStudentEmail());
            stdCourseLabel.setText(Main.currentStudent.getStudentCourse());
            stdPhoneNumberLabel.setText(Main.currentStudent.getStudentPhoneNumber());
        };
        //else if the student is not in the database open a new window and register the student
        if (!isRegistrationWindowOpen && Main.currentStudent == null) {
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
                    }
                    ));
                    dialogStage.showAndWait();

                }
                catch (IOException e)
                {
                    // Exception gets thrown if the fxml file could not be loaded
                    e.printStackTrace();
                }
            };
            Platform.runLater(openRegistrationWindow);
        }
        else if(!isRegistrationWindowOpen)
        {
            Platform.runLater(configureStudentCard);
        }
    }
}
