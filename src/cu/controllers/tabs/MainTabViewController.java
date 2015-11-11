package cu.controllers.tabs;

import cu.listeners.CardInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;


import java.io.IOException;

/**
 * Created by T on 08/11/2015.
 */
public class MainTabViewController implements CardInterface
{
    @FXML
    private SplitPane mainVerticalSplitPane;
    @FXML
    private ImageView studentCardBack;
    @FXML
    private ListView equipmentListView;
    @FXML
    private TableView leasedItemsTableView;

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

        } catch (IOException exc)
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
