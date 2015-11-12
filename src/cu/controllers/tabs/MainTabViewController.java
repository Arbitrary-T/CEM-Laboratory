package cu.controllers.tabs;

import cu.Main;
import cu.listeners.CardInterface;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by T on 08/11/2015.
 */

public class MainTabViewController implements CardInterface {

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
       leftAP.maxWidthProperty().bind(mainVerticalSplitPane.widthProperty().multiply(0.2));
       studentCardBack.fitHeightProperty().bind(imageAP.heightProperty());
       studentCardBack.fitWidthProperty().bind(imageAP.widthProperty());
   }

    @FXML
    private void openRegisterWindow(ActionEvent e)
    {

        System.out.println(" OPEN WINDOW ");
        /*
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/dialogues/NewRegistrationDialogue.fxml"));
            DialogPane page = loader.load();
            Stage dialogStage = new Stage();
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.setTitle("New Student Registration");

            NewRegistrationDialogueController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();

        }
        catch (IOException exc)
        {
            // Exception gets thrown if the fxml file could not be loaded
            exc.printStackTrace();
        }*/
    }

    @Override
    public void cardDetected(String cardUID)
    {

    }
}
