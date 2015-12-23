package cu.controllers.tabs;

import cu.listeners.DatabaseInterface;
import cu.models.Student;
import cu.models.StudentDatabase;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import java.util.Optional;


/**
 * Created by T on 22/12/2015.
 */
public class DatabaseManagementTabViewController implements DatabaseInterface
{
    @FXML
    TextField filterTextField;

    @FXML
    TableColumn<Student, Integer>  studentIDColumn;

    @FXML
    TableColumn<Student, String>  studentNameColumn;

    @FXML
    TableColumn<Student, String>  studentCourseColumn;

    @FXML
    TableColumn<Student, String>  studentEmailColumn;

    @FXML
    TableColumn<Student, String>  studentPhoneNumberColumn;

    @FXML
    TableView<Student> databaseTableView;

    ObservableList<Student> studentsObservableList;

    StudentDatabase studentDatabase = new StudentDatabase("students");

    @FXML
    void initialize()
    {
        StudentDatabase.activateAgent(this);
        databaseTableView.setEditable(true);
        databaseTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        studentsObservableList = studentDatabase.getAllStudents();

        studentIDColumn.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        studentIDColumn.setEditable(false);

        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        studentNameColumn.setCellFactory(TextFieldTableCell.<Student>forTableColumn());
        studentNameColumn.setOnEditCommit((CellEditEvent<Student, String> event) ->
        {
            Student a = event.getTableView().getItems().get(event.getTablePosition().getRow());
            a.setStudentName(event.getNewValue().toString());
            studentDatabase.editStudentEntry(a);
        });

        studentCourseColumn.setCellValueFactory(new PropertyValueFactory<>("studentCourse"));
        studentCourseColumn.setCellFactory(TextFieldTableCell.<Student>forTableColumn());
        studentCourseColumn.setOnEditCommit((CellEditEvent<Student, String> event) ->
        {
            Student a = event.getTableView().getItems().get(event.getTablePosition().getRow());
            a.setStudentCourse(event.getNewValue().toString());
            studentDatabase.editStudentEntry(a);
        });

        studentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("studentEmail"));
        studentEmailColumn.setCellFactory(TextFieldTableCell.<Student>forTableColumn());
        studentEmailColumn.setOnEditCommit((CellEditEvent<Student, String> event) ->
        {
            Student a = event.getTableView().getItems().get(event.getTablePosition().getRow());
            a.setStudentEmail(event.getNewValue().toString());
            studentDatabase.editStudentEntry(a);
        });
        studentPhoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("studentPhoneNumber"));
        studentPhoneNumberColumn.setCellFactory(TextFieldTableCell.<Student>forTableColumn());
        studentPhoneNumberColumn.setOnEditCommit((CellEditEvent<Student, String> event) ->
        {
            Student a = event.getTableView().getItems().get(event.getTablePosition().getRow());
            a.setPhoneNumber(event.getNewValue().toString());
            studentDatabase.editStudentEntry(a);
        });

        ContextMenu contextMenu = new ContextMenu();
        MenuItem editMenuItem = new MenuItem("Edit cell");
        editMenuItem.setOnAction(event ->
                databaseTableView.getSelectionModel().getTableView().edit(databaseTableView.getFocusModel().getFocusedCell().getRow(),databaseTableView.getFocusModel().getFocusedCell().getTableColumn()));
        MenuItem deleteMenuItem = new MenuItem("Delete student");
        deleteMenuItem.setOnAction(event ->
        {
            //MAKE IT MORE MODULAR (IN METHOD TO CREATE AN ALERT AT ANY TIME.....!)
            Alert confirmDeletion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDeletion.setTitle("Delete student");
            confirmDeletion.setHeaderText("Warning this cannot be undone!");
            confirmDeletion.setContentText("Do you want to continue?");
            Optional<ButtonType> result = confirmDeletion.showAndWait();
            if(result.get() == ButtonType.OK)
            {
                for(Student students : databaseTableView.getSelectionModel().getSelectedItems())
                {
                    studentDatabase.deleteStudentEntry(students.getStudentID());
                }
            }
        });
        databaseTableView.setOnKeyPressed(event ->
        {
            if(event.getCode().getName().equals("Delete"))
            {
                Alert confirmDeletion = new Alert(Alert.AlertType.CONFIRMATION);
                confirmDeletion.setTitle("Delete student");
                confirmDeletion.setHeaderText("Warning this cannot be undone!");
                confirmDeletion.setContentText("Do you want to continue?");
                Optional<ButtonType> result = confirmDeletion.showAndWait();
                if(result.get() == ButtonType.OK)
                {
                    for(Student students : databaseTableView.getSelectionModel().getSelectedItems())
                    {
                        studentDatabase.deleteStudentEntry(students.getStudentID());
                    }
                }
                event.consume();
            }
        });
        contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);
        databaseTableView.setContextMenu(contextMenu);
        databaseTableView.setItems(studentsObservableList);
    }

    @Override
    public void onStudentDatabaseUpdate()
    {
        studentsObservableList = studentDatabase.getAllStudents();
        databaseTableView.setItems(studentsObservableList);
    }

    @Override
    public void onEquipmentDatabaseUpdate()
    {

    }
}
