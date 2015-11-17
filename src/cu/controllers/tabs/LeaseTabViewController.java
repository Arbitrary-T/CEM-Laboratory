package cu.controllers.tabs;

import cu.Main;
import cu.controllers.MainController;
import cu.controllers.dialogues.NewRegistrationDialogueController;
import cu.listeners.CardInterface;
import cu.listeners.CardListener;
import cu.models.Student;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


import java.io.IOException;

/**
 * Created by T on 08/11/2015.
 */

public class LeaseTabViewController implements CardInterface
{
    //Non-view related variables
    private MainController mainController;
    //View related variables
    @FXML
    private AnchorPane imageAP;
    @FXML
    private AnchorPane leftAP;
    @FXML
    private SplitPane mainVerticalSplitPane;
    @FXML
    private ImageView studentCardBack;
    @FXML
    private ListView equipmentListView;
    @FXML
    private TableView leasedItemsTableView;
    @FXML
    private TextField searchEquipment;
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
    private Group textGroup;
    @FXML
    private AnchorPane bottomLeftAP;
    @FXML
    private SplitPane leftSplitPane;

    @FXML
    void initialize()
    {
        CardListener.activateAgent(this);
        leftAP.maxWidthProperty().bind(mainVerticalSplitPane.widthProperty().multiply(0.2));
        studentCardBack.fitHeightProperty().bind(imageAP.heightProperty());
        studentCardBack.fitWidthProperty().bind(imageAP.widthProperty());

        studentCardBack.boundsInParentProperty().addListener(((observable, oldValue, newValue) ->
        {
            textGroup.setLayoutX(studentCardBack.layoutBoundsProperty().get().getWidth() / 3);
            textGroup.setLayoutY(textGroup.layoutBoundsProperty().get().getHeight());
            textGroup.getTransforms().clear();
            double scale_x = (studentCardBack.getBoundsInParent().getWidth() * 0.5) / 75;
            double scale_y = (studentCardBack.getBoundsInParent().getHeight() * 0.5) / 85;
            double scale_factor = Math.min(scale_x, scale_y);
            double pivot_y = (textGroup.getBoundsInLocal().getMinY() - textGroup.getBoundsInLocal().getMaxY()) ;
            Scale scale = new Scale(scale_factor, scale_factor, 0, pivot_y);
            textGroup.getTransforms().add(scale);
        }));
    }

    @Override
    public void onCardScanned(String cardUID)
    {
        Main.currentStudent = Main.studentDatabase.searchDatabase(cardUID);
        Runnable configureStudentCard = () ->
        {
            stdNameLabel.setText(Main.currentStudent.getStudentName());
            stdIDLabel.setText(""+Main.currentStudent.getStudentID());
            stdEmailLabel.setText(Main.currentStudent.getStudentEmail());
            stdCourseLabel.setText(Main.currentStudent.getStudentCourse());
            stdPhoneNumberLabel.setText(Main.currentStudent.getStudentPhoneNumber());
            System.out.println(stdEmailLabel.getWidth() + " WIDTH");
        };
        if(!Main.isRegistrationWindowOpen && Main.currentStudent == null)
        {
            System.out.println("Student does no exist in database -> Opening registration window");
            //Running the UI manipulating code in the UI thread
            Runnable openRegistrationWindow = () ->
            {
                try {
                    Main.isRegistrationWindowOpen = true;
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
                        Main.isRegistrationWindowOpen = false;
                        System.out.println("Canceled by user.");
                    }
                    ));
                    dialogStage.showAndWait();

                } catch (IOException e) {
                    // Exception gets thrown if the fxml file could not be loaded
                    e.printStackTrace();
                }
            };
            Platform.runLater(openRegistrationWindow);
        }
        else
        {
            Platform.runLater(configureStudentCard);
        }
    }
    public void init(MainController mainController)
    {

    }
}
