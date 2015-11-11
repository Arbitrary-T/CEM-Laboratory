package cu.controllers.tabs;

import cu.Main;
import cu.controllers.dialogues.NewRegistrationDialogueController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by T on 08/11/2015.
 */
public class MainTabViewController
{
    @FXML
    private Button buttonAdd;

    @FXML
    private void openRegisterWindow(ActionEvent e)
    {
        System.out.println(" OPEN WINDOW ");
        try {
            // Load the fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/dialogues/NewRegistrationDialogue.fxml"));
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
        }
    }
}
