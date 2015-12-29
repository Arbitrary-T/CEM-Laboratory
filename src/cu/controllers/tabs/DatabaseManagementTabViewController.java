package cu.controllers.tabs;

import cu.interfaces.DatabaseInterface;
import cu.models.Equipment;
import cu.models.EquipmentDatabase;
import cu.models.Student;
import cu.models.StudentDatabase;
import cu.validations.TextValidation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.util.converter.IntegerStringConverter;
import java.util.Optional;

//TO DO FIX THE CONTEXTMENU TO BE ON ROW RATHER THAN TABLE. FIX EQUIPMENT PRIMARY KEY ISSUE.

/**
 * Created by T on 22/12/2015.
 */
public class DatabaseManagementTabViewController implements DatabaseInterface
{
    @FXML
    private TextField filterTextField;
    @FXML
    private TableColumn<Student, Integer>  studentIDColumn;
    @FXML
    private TableColumn<Student, String>  studentNameColumn;
    @FXML
    private TableColumn<Student, String>  studentCourseColumn;
    @FXML
    private TableColumn<Student, String>  studentEmailColumn;
    @FXML
    private TableColumn<Student, String>  studentPhoneNumberColumn;
    @FXML
    private TableView<Student> studentTableView;
    @FXML
    private TextField equipmentFilterTextField;
    @FXML
    private TableView<Equipment> equipmentTableView;
    @FXML
    private TableColumn<Equipment, Integer> idTableColumn;
    @FXML
    private TableColumn<Equipment, String> nameTableColumn;
    @FXML
    private TableColumn<Equipment, String> categoryTableColumn;
    @FXML
    private TableColumn<Equipment, String> conditionTableColumn;
    @FXML
    private TableColumn<Equipment, String> partOfBundleTableColumn;
    @FXML
    private ListView availableBundleListView;
    @FXML
    private ListView newBundleListView;
    @FXML
    private Button addBundleButton;
    @FXML
    private Button clearBundleButton;

    private ObservableList<String> isFunctionalObservableList = FXCollections.observableArrayList("Yes", "No");
    private ObservableList<Student> studentsObservableList;
    private ObservableList<Equipment> equipmentObservableList;
    private StudentDatabase studentDatabase = new StudentDatabase("students");
    private EquipmentDatabase equipmentDatabase = new EquipmentDatabase("equipment");
    private TextValidation validation = new TextValidation();
    private Student tempStudent;
    private Equipment tempEquipment;
    private int equipmentCount;
    @FXML
    void initialize()
    {
        StudentDatabase.activateAgent(this);
        EquipmentDatabase.activateAgent(this);
        studentTableView.setEditable(true);
        studentTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        studentsObservableList = studentDatabase.getAllStudents();
        equipmentObservableList = equipmentDatabase.getAllEquipment();
        equipmentCount = equipmentObservableList.size();
        equipmentTableView.setEditable(true);
        equipmentTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        idTableColumn.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        idTableColumn.setCellFactory(TextFieldTableCell.<Equipment, Integer>forTableColumn(new IntegerStringConverter()));
        idTableColumn.setOnEditCommit((CellEditEvent<Equipment, Integer> event) ->
        {
            tempEquipment = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if(!event.getNewValue().toString().isEmpty())
            {
                if(event.getNewValue().toString().matches("\\d*"))
                {
                    System.out.println(event.getNewValue());

                    tempEquipment.setItemID(event.getNewValue());
                    equipmentDatabase.editEquipmentEntry(tempEquipment, event.getOldValue());
                }
            }
        });
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        nameTableColumn.setCellFactory(TextFieldTableCell.<Equipment>forTableColumn());
        nameTableColumn.setOnEditCommit((CellEditEvent<Equipment, String> event) ->
        {
            tempEquipment = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if(!event.getNewValue().isEmpty())
            {
                tempEquipment.setItemName(event.getNewValue());
                equipmentDatabase.editEquipmentEntry(tempEquipment, tempEquipment.getItemID());
            }
        });
        categoryTableColumn.setCellValueFactory(new PropertyValueFactory<>("itemCategory"));
        categoryTableColumn.setCellFactory(TextFieldTableCell.<Equipment>forTableColumn());
        categoryTableColumn.setOnEditCommit((CellEditEvent<Equipment, String> event) ->
        {
            tempEquipment = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if(!event.getNewValue().isEmpty())
            {
                tempEquipment.setItemCategory(event.getNewValue());
                equipmentDatabase.editEquipmentEntry(tempEquipment, tempEquipment.getItemID());
            }
        });
        conditionTableColumn.setCellValueFactory(new PropertyValueFactory<>("functional"));
        conditionTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(isFunctionalObservableList));
        conditionTableColumn.setOnEditCommit((CellEditEvent<Equipment, String> event) ->
        {
            tempEquipment = event.getTableView().getItems().get(event.getTablePosition().getRow());
            System.out.println("THE COMBO BOX SAYS: ? " + event.getNewValue());
            if(event.getNewValue().equals("Yes"))
            {
                tempEquipment.setFunctional(true);
            }
            else
            {
                tempEquipment.setFunctional(false);
            }
            equipmentDatabase.editEquipmentEntry(tempEquipment, tempEquipment.getItemID());
        });
        partOfBundleTableColumn.setCellValueFactory(new PropertyValueFactory<>("partOfBundle"));
        partOfBundleTableColumn.setCellFactory(TextFieldTableCell.<Equipment>forTableColumn());
        partOfBundleTableColumn.setOnEditCommit((CellEditEvent<Equipment, String> event) ->
        {
            tempEquipment = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if(!event.getNewValue().isEmpty())
            {
                tempEquipment.setPartOfBundle(event.getNewValue());
                equipmentDatabase.editEquipmentEntry(tempEquipment, tempEquipment.getItemID());
            }
        });

        ContextMenu equipmentTableContextMenu = new ContextMenu();
        MenuItem equipmentEditMenuItem = new MenuItem("Modify cell");
        equipmentEditMenuItem.setOnAction(event ->
                equipmentTableView.getSelectionModel().getTableView().edit(equipmentTableView.getFocusModel().getFocusedCell().getRow(),equipmentTableView.getFocusModel().getFocusedCell().getTableColumn()));
        MenuItem equipmentDeleteMenuItem = new MenuItem("Delete item");
        equipmentDeleteMenuItem.setOnAction(event ->
        {
            if(equipmentTableView.getSelectionModel().getSelectedCells().size() > 0)
            {
                Alert confirmDeletion = new Alert(Alert.AlertType.CONFIRMATION);
                confirmDeletion.setTitle("Delete item");
                confirmDeletion.setHeaderText("Warning this cannot be undone!");
                confirmDeletion.setContentText("Do you want to continue?");
                Optional<ButtonType> result = confirmDeletion.showAndWait();
                if (result.get() == ButtonType.OK)
                {
                    for (Equipment equipment : equipmentTableView.getSelectionModel().getSelectedItems())
                    {
                        equipmentDatabase.deleteEquipmentEntry(equipment);

                    }
                }
                event.consume();
            }
        });
        MenuItem equipmentNewMenuItem = new MenuItem("New item");
        equipmentNewMenuItem.setOnAction(event ->
        {
            System.out.println(equipmentCount);
            equipmentDatabase.addEquipmentEntry(new Equipment(equipmentCount,"ToDo", "ToDo", true, "ToDo"));
            equipmentTableView.edit(equipmentTableView.getItems().size(), equipmentTableView.getColumns().get(0));
        });
        equipmentTableContextMenu.getItems().addAll(equipmentNewMenuItem, equipmentEditMenuItem, equipmentDeleteMenuItem);
        equipmentTableView.setContextMenu(equipmentTableContextMenu);
        equipmentTableView.setItems(equipmentObservableList);


        studentIDColumn.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        studentIDColumn.setCellFactory(TextFieldTableCell.<Student,Integer>forTableColumn(new IntegerStringConverter()));
        studentIDColumn.setOnEditCommit((CellEditEvent<Student, Integer> event) ->
        {
            tempStudent = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if(validation.isValidStudentID(event.getNewValue().toString()))
                tempStudent.setID(Integer.parseInt(event.getNewValue().toString()));
            studentDatabase.editStudentEntry(tempStudent);
        });
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        studentNameColumn.setCellFactory(TextFieldTableCell.<Student>forTableColumn());
        studentNameColumn.setOnEditCommit((CellEditEvent<Student, String> event) ->
        {
            tempStudent = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if(!event.getNewValue().isEmpty())
                tempStudent.setStudentName(event.getNewValue());
            studentDatabase.editStudentEntry(tempStudent);
        });

        studentCourseColumn.setCellValueFactory(new PropertyValueFactory<>("studentCourse"));
        studentCourseColumn.setCellFactory(TextFieldTableCell.<Student>forTableColumn());
        studentCourseColumn.setOnEditCommit((CellEditEvent<Student, String> event) ->
        {
            tempStudent = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if(!event.getNewValue().isEmpty())
                tempStudent.setStudentCourse(event.getNewValue());
            studentDatabase.editStudentEntry(tempStudent);
        });

        studentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("studentEmail"));
        studentEmailColumn.setCellFactory(TextFieldTableCell.<Student>forTableColumn());
        studentEmailColumn.setOnEditCommit((CellEditEvent<Student, String> event) ->
        {
            tempStudent = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if(validation.checkValidEmail(event.getNewValue()))
                tempStudent.setStudentEmail(event.getNewValue());
            studentDatabase.editStudentEntry(tempStudent);
        });
        studentPhoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("studentPhoneNumber"));
        studentPhoneNumberColumn.setCellFactory(TextFieldTableCell.<Student>forTableColumn());
        studentPhoneNumberColumn.setOnEditCommit((CellEditEvent<Student, String> event) ->
        {
            tempStudent = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if(validation.isValidPhoneNumber(event.getNewValue()))
                tempStudent.setPhoneNumber(event.getNewValue());
            studentDatabase.editStudentEntry(tempStudent);
        });

        studentTableView.setOnKeyReleased(event ->
        {
            if(event.getCode().equals(KeyCode.DELETE))
            {
                if(studentTableView.getSelectionModel().getSelectedCells().size() > 0)
                {
                    Alert confirmDeletion = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmDeletion.setTitle("Delete student");
                    confirmDeletion.setHeaderText("Warning this cannot be undone!");
                    confirmDeletion.setContentText("Do you want to continue?");
                    Optional<ButtonType> result = confirmDeletion.showAndWait();
                    if(result.get() == ButtonType.OK)
                    {
                        for(Student students : studentTableView.getSelectionModel().getSelectedItems())
                        {
                            studentDatabase.deleteStudentEntry(students.getCardUID());
                        }
                    }
                    event.consume();
                }
            }
        });

        ContextMenu studentTableContextMenu = new ContextMenu();
        MenuItem editMenuItem = new MenuItem("Modify cell");
        editMenuItem.setOnAction(event ->
                studentTableView.getSelectionModel().getTableView().edit(studentTableView.getFocusModel().getFocusedCell().getRow(),studentTableView.getFocusModel().getFocusedCell().getTableColumn()));
        MenuItem deleteMenuItem = new MenuItem("Delete student");
        deleteMenuItem.setOnAction(event ->
        {
            if(studentTableView.getSelectionModel().getSelectedCells().size() > 0)
            {
                Alert confirmDeletion = new Alert(Alert.AlertType.CONFIRMATION);
                confirmDeletion.setTitle("Delete student");
                confirmDeletion.setHeaderText("Warning this cannot be undone!");
                confirmDeletion.setContentText("Do you want to continue?");
                Optional<ButtonType> result = confirmDeletion.showAndWait();
                if (result.get() == ButtonType.OK)
                {
                    for (Student students : studentTableView.getSelectionModel().getSelectedItems())
                    {
                        studentDatabase.deleteStudentEntry(students.getCardUID());
                    }
                }
            }
        });
        studentTableContextMenu.getItems().addAll(editMenuItem, deleteMenuItem);
        studentTableView.setContextMenu(studentTableContextMenu);
        studentTableView.setItems(studentsObservableList);
    }

    @Override
    public void onStudentDatabaseUpdate()
    {
        studentsObservableList = studentDatabase.getAllStudents();
        studentTableView.setItems(studentsObservableList);
    }

    @Override
    public void onEquipmentDatabaseUpdate()
    {
        equipmentObservableList = equipmentDatabase.getAllEquipment();
        equipmentTableView.setItems(equipmentObservableList);
        System.out.println(equipmentCount + "\t" + equipmentObservableList.get(0).getItemID());
        equipmentCount = equipmentObservableList.size();
    }
}