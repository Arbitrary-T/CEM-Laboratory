package cu.controllers.tabs;

import com.sun.javaws.Main;
import cu.interfaces.DatabaseInterface;
import cu.models.*;
import cu.validations.TextValidation;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import java.util.Optional;

//TO DO FIX THE CONTEXTMENU TO BE ON ROW RATHER THAN TABLE. FIX EQUIPMENT PRIMARY KEY ISSUE.

/**
 * Created by T on 22/12/2015.
 */
public class DatabaseManagementTabViewController implements DatabaseInterface
{
    int equipmentTableCurrentRow = 0;
    @FXML
    private ImageView contextImageView;
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
        equipmentTableView.setRowFactory(param ->
        {
            final TableRow<Equipment> row = new TableRow<>();
            final ContextMenu rowMenu = new ContextMenu();
            param.setOnKeyPressed(event ->
            {
                if(event.getCode().equals(KeyCode.DELETE))
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
                        equipmentTableView.requestFocus();
                        equipmentTableView.getSelectionModel().select(equipmentTableCurrentRow);
                        equipmentTableView.getFocusModel().focus(equipmentTableCurrentRow);
                        event.consume();
                    }
                }
                if(event.isControlDown() && event.getCode().equals(KeyCode.C))
                {
                    System.out.println("CTRL IS DOWN AND C IS PRESSED");
                    if(equipmentObservableList.size() > 0)
                    {
                        int rowa = equipmentTableView.getFocusModel().getFocusedCell().getRow();
                        if (rowa >= 1)
                        {
                            Equipment temp = equipmentObservableList.get(rowa - 1);
                            equipmentDatabase.editEquipmentEntry(new Equipment(equipmentObservableList.get(rowa).getItemID(), temp.getItemName(), temp.getItemCategory(), temp.isFunctional(), temp.getPartOfBundle()), equipmentObservableList.get(rowa).getItemID());
                        }
                        event.consume();
                        if(rowa+1 < equipmentObservableList.size())
                        {
                            equipmentTableView.getFocusModel().focus(rowa+1);
                        }
                        else
                        {
                            equipmentTableView.getFocusModel().focus(rowa);
                        }
                    }
                }
                if(event.isControlDown() && event.getCode().equals(KeyCode.N))
                {
                    if(equipmentDatabase.getAllEquipment().size() != 0)
                        equipmentDatabase.addEquipmentEntry(new Equipment(equipmentObservableList.get(equipmentCount-1).getItemID()+1,"ToDo", "ToDo", true, "ToDo"));
                    else
                        equipmentDatabase.addEquipmentEntry(new Equipment(0,"ToDo", "ToDo", true, "ToDo"));
                    equipmentTableView.edit(equipmentTableView.getItems().size(), equipmentTableView.getColumns().get(0));
                    event.consume();
                    equipmentTableView.getFocusModel().focus(equipmentTableView.getItems().size()-1);
                }
            });
            MenuItem equipmentNewMenuItem = new MenuItem("New item");
            equipmentNewMenuItem.setOnAction(event ->
            {
                System.out.println(equipmentCount);
                if(equipmentDatabase.getAllEquipment().size() != 0)
                {
                    equipmentDatabase.addEquipmentEntry(new Equipment(equipmentObservableList.get(equipmentCount - 1).getItemID() + 1, "ToDo", "ToDo", true, "ToDo"));
                }
                else
                {
                    equipmentDatabase.addEquipmentEntry(new Equipment(0, "ToDo", "ToDo", true, "ToDo"));
                }
                equipmentTableView.scrollTo(equipmentCount+1);
                equipmentTableView.requestFocus();
                equipmentTableView.getSelectionModel().select(equipmentCount);
                equipmentTableView.getFocusModel().focus(equipmentCount);
            });
            MenuItem equipmentDeleteMenuItem = new MenuItem("Delete row");
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
                    equipmentTableView.requestFocus();
                    equipmentTableView.getSelectionModel().select(equipmentTableCurrentRow);
                    equipmentTableView.getFocusModel().focus(equipmentTableCurrentRow);
                    event.consume();
                }
            });
            MenuItem editItem = new MenuItem("Edit row");
            editItem.setOnAction(event ->
                equipmentTableView.getSelectionModel().getTableView().edit(equipmentTableView.getFocusModel().getFocusedCell().getRow(),equipmentTableView.getFocusModel().getFocusedCell().getTableColumn()));

            rowMenu.getItems().addAll(equipmentNewMenuItem, equipmentDeleteMenuItem, editItem);
            row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenu).otherwise((ContextMenu) null));
            return row;
        });

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
        //***********************TEST****************************************
        ContextMenu equipmentTableContextMenu = new ContextMenu();
        MenuItem deleteAll = new MenuItem("deleteAll");
        deleteAll.setOnAction(event ->
        {
            equipmentObservableList.removeAll();
            equipmentObservableList.clear();
            equipmentDatabase.deleteAllEntries();
        }
        );
        equipmentTableContextMenu.getItems().add(deleteAll);
        equipmentTableView.setContextMenu(equipmentTableContextMenu);
        //**********************END TEST***************************************

        equipmentTableView.setItems(equipmentObservableList);


        equipmentTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            if(equipmentObservableList.size() > 0)
            {
                equipmentTableCurrentRow = equipmentTableView.getSelectionModel().getSelectedIndex();
                System.out.println("Current Row: " + equipmentTableCurrentRow);
                Equipment temp = equipmentObservableList.get(equipmentTableView.getSelectionModel().getSelectedIndex());
                contextImageView.setImage(QRGenerator.generateQRCode(temp.getItemID() + temp.getItemName(), 300, 300));
            }
        });


        /////////////////@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@////////////////////////////////////////////////////////////////////////////////////
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
        if(!equipmentObservableList.isEmpty())
        {
            //System.out.println(equipmentCount + "\t" + equipmentObservableList.get(0).getItemID());
        }
        equipmentCount = equipmentObservableList.size();
    }
}