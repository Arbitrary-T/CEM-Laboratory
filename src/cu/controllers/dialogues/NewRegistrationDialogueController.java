package cu.controllers.dialogues;

import cu.models.students.CurrentStudent;
import cu.models.students.Student;
import cu.models.students.StudentDatabase;
import cu.validations.TextValidation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 * Created by T on 08/11/2015.
 */
public class NewRegistrationDialogueController
{
    private StudentDatabase studentDatabase= new StudentDatabase("students");
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

    private String cardUID;

    private TextValidation validation = new TextValidation();

    /**
     * sets up the new registration window
     */
    @FXML
    private void initialize()
    {
        submitButton.setDisable(true);
        stdName.textProperty().addListener(((observable, oldValue, newValue) ->
        {
            if(newValue.isEmpty())
            {
                submitButton.setDisable(true);
                stdName.setStyle("-fx-background-color: #DBB1B1, #FFF0F0;");
            }
            else
            {
                validateFields();
                stdName.setStyle("-fx-background-color: #B1DBB1, #F0FFF0;");
            }
        }));

        stdEmail.textProperty().addListener(((observable, oldValue, newValue) ->
        {
            if(!validation.checkValidEmail(newValue))
            {
                submitButton.setDisable(true);
                stdEmail.setStyle("-fx-background-color: #DBB1B1, #FFF0F0;");
            }
            else
            {
                validateFields();
                stdEmail.setStyle("-fx-background-color: #B1DBB1, #F0FFF0;");

            }
        }));
        stdID.textProperty().addListener(((observable, oldValue, newValue) ->
        {
            if(!validation.isValidStudentID(newValue))
            {
                stdID.setText(newValue.replaceAll("[^\\d]", ""));
                stdID.setStyle("-fx-background-color: #DBB1B1, #FFF0F0;");
                submitButton.setDisable(true);
            }
            if(newValue.length() > 6)
            {
                validateFields();
                stdID.setStyle("-fx-background-color: #B1DBB1, #F0FFF0;");

            }
        }));
        stdCourse.textProperty().addListener(((observable, oldValue, newValue) ->
        {
            if(newValue.isEmpty())
            {
                submitButton.setDisable(true);
                stdCourse.setStyle("-fx-background-color: #DBB1B1, #FFF0F0;");
            }
            else
            {
                validateFields();
                stdCourse.setStyle("-fx-background-color: #B1DBB1, #F0FFF0;");
            }

        }));
        stdPhoneNumber.textProperty().addListener(((observable, oldValue, newValue) ->
        {
            if(!validation.isValidPhoneNumber(newValue))
            {
                stdPhoneNumber.setText(newValue.replaceAll("[^\\d]", ""));
                stdPhoneNumber.setStyle("-fx-background-color: #DBB1B1, #FFF0F0;");
                submitButton.setDisable(true);
            }
            if(newValue.length() == 11)
            {
                validateFields();
                stdPhoneNumber.setStyle("-fx-background-color: #B1DBB1, #F0FFF0;");
            }
        }));

    }
    @FXML
    private void onSubmit()
    {
        if(studentDatabase != null)
        {
            Student newStudent = new Student(cardUID, stdName.getText(), Integer.parseInt(stdID.getText()), stdEmail.getText(), stdCourse.getText(), stdPhoneNumber.getText(), 0, 0, 0, 0);
            if(!studentDatabase.addStudentEntry(newStudent))
            {
                System.out.println("Error: could not maintain a connection to the database!");
            }
            else
            {
                System.out.println("Successfully added " + stdName.getText() + " to the 'Students' database.");
                CurrentStudent.getInstance().setLoadedStudent(newStudent);
            }
            dialogStage.fireEvent(new WindowEvent(dialogStage, WindowEvent.WINDOW_CLOSE_REQUEST));
            dialogStage.close();
        }
    }

    /**
     * closes the window when canceled
     */
    @FXML
    private void onCancel()
    {
        dialogStage.fireEvent(new WindowEvent(dialogStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        dialogStage.close();
        System.out.println("Canceled by user.");
    }

    /**
     * sets up the dialog window
     * @param dialogStage the dialog window
     * @param cardUID the scanned card to be associated with the registration
     */
    public void configureDialogStage(Stage dialogStage, String cardUID)
    {
        this.dialogStage = dialogStage;
        this.cardUID = cardUID;
    }

    /**
     * validates if the user's input is correct
     */
    private void validateFields()
    {
        if(stdID.getText().length() < 6  || stdName.getText().isEmpty() || !isValidEmail(stdEmail.getText()) || stdCourse.getText().isEmpty() || stdPhoneNumber.getText().length() != 11)
        {
            submitButton.setDisable(true);
        }
        else
        {
            submitButton.setDisable(false);
        }
    }
    private boolean isValidEmail(String email)
    {
        return validation.checkValidEmail(email);
    }
}