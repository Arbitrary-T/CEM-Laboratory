package cu.controllers.tabs;

import cu.models.Student;
import cu.models.StudentDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Created by T on 22/12/2015.
 */
public class DatabaseManagementTabViewController
{
    @FXML
    TextField filterTextField;

    @FXML
    TableColumn studentIDColumn;

    @FXML
    TableColumn studentNameColumn;

    @FXML
    TableColumn studentCourseColumn;

    @FXML
    TableColumn studentEmailColumn;

    @FXML
    TableColumn studentPhoneNumberColumn;

    @FXML
    TableView databaseTableView;

    ObservableList<Student> a;
    StudentDatabase studentDatabase = new StudentDatabase("students");

    @FXML
    void initialize()
    {
        if (!studentDatabase.firstRun)
        {
            a = studentDatabase.getAllStudents();
        }

        studentIDColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("studentID"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("studentName"));
        studentCourseColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("studentCourse"));
        studentEmailColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("studentEmail"));
        studentPhoneNumberColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("studentPhoneNumber"));
        databaseTableView.setItems(a);
    }
}
