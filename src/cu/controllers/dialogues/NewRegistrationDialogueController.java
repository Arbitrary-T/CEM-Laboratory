package cu.controllers.dialogues;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by T on 08/11/2015.
 */
public class NewRegistrationDialogueController
{
    @FXML
    private TextField stdName;

    @FXML
    private TextField stdID;

    @FXML
    private TextField stdCourse;

    @FXML
    private TextField stdEmail;

    @FXML
    private TextField stdPhoneNumber;

    @FXML
    private Button submitButton;

    @FXML
    private Button cancelButton;

    private Stage dialogStage;

    @FXML
    private void initialize() {

    }

    public void setDialogStage(Stage dialogStage)
    {
        this.dialogStage = dialogStage;
    }
}
