package cu.controllers.tabs;

import cu.Main;
import cu.controllers.dialogues.NewRegistrationDialogueController;
import cu.listeners.CardInterface;
import cu.listeners.CardListener;
import cu.models.Student;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


import java.io.IOException;

/**
 * Created by T on 08/11/2015.
 */

public class MainTabViewController implements CardInterface
{
    //Non-view related variables
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
   void initialize()
   {
       CardListener.activateAgent(this);
       leftAP.maxWidthProperty().bind(mainVerticalSplitPane.widthProperty().multiply(0.2));
       studentCardBack.fitHeightProperty().bind(imageAP.heightProperty());
       studentCardBack.fitWidthProperty().bind(imageAP.widthProperty());
   }

    @Override
    public void onCardScanned(String cardUID)
    {
        Main.currentStudent = Main.studentDatabase.searchDatabase(cardUID);
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
    }
}
