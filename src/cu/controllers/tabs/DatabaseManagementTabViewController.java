package cu.controllers.tabs;

import cu.controllers.MainViewController;
import cu.interfaces.CodeScannerInterface;
import cu.interfaces.DatabaseInterface;
import cu.models.equipment.Equipment;
import cu.models.equipment.EquipmentDatabase;
import cu.models.listeners.CodeScannedListener;
import cu.models.students.Student;
import cu.models.students.StudentDatabase;
import cu.models.utilities.PDFRenderer;
import cu.models.utilities.QRGenerator;
import cu.validations.TextValidation;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.util.converter.IntegerStringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//Test
//TO DO FIX THE CONTEXTMENU TO BE ON ROW RATHER THAN TABLE. FIX EQUIPMENT PRIMARY KEY ISSUE.

/**
 * Created by T on 22/12/2015.
 */
public class DatabaseManagementTabViewController implements DatabaseInterface, CodeScannerInterface
{
    int equipmentTableCurrentRow = 0;
    @FXML
    private TabPane tabPane;
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

    private ObservableList<String> isFunctionalObservableList = FXCollections.observableArrayList("Functional", "Faulty");
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
        CodeScannedListener.activateAgent(this);
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
                        equipmentDatabase.addEquipmentEntry(new Equipment(equipmentObservableList.get(equipmentCount-1).getItemID()+1,"New", "New", true, "New"));
                    else
                        equipmentDatabase.addEquipmentEntry(new Equipment(0,"New", "New", true, "New"));
                    equipmentTableView.edit(equipmentTableView.getItems().size(), equipmentTableView.getColumns().get(0));
                    event.consume();
                    equipmentTableView.getSelectionModel().select(equipmentTableView.getItems().size()-1);
                }
            });
            MenuItem equipmentNewMenuItem = new MenuItem("New item");
            equipmentNewMenuItem.setOnAction(event ->
            {
                System.out.println(equipmentCount);
                if(equipmentDatabase.getAllEquipment().size() != 0)
                {
                    equipmentDatabase.addEquipmentEntry(new Equipment(equipmentObservableList.get(equipmentCount - 1).getItemID() + 1, "New", "New", true, "New"));
                }
                else
                {
                    equipmentDatabase.addEquipmentEntry(new Equipment(0, "New", "New", true, "New"));
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
            MenuItem editItem = new MenuItem("Edit cell");
            editItem.setOnAction(event ->
                equipmentTableView.getSelectionModel().getTableView().edit(equipmentTableView.getFocusModel().getFocusedCell().getRow(),equipmentTableView.getFocusModel().getFocusedCell().getTableColumn()));

            rowMenu.getItems().addAll(equipmentNewMenuItem, equipmentDeleteMenuItem, editItem);
            row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenu).otherwise((ContextMenu) null));
            return row;
        });
        idTableColumn.setEditable(false);
        idTableColumn.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        idTableColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

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
        conditionTableColumn.setCellValueFactory(new PropertyValueFactory<>("functionalWrapper"));
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
        MenuItem equipmentNewMenuItem = new MenuItem("New item");
        equipmentNewMenuItem.setOnAction(event ->
        {
            System.out.println(equipmentCount);
            if(equipmentDatabase.getAllEquipment().size() != 0)
            {
                equipmentDatabase.addEquipmentEntry(new Equipment(equipmentObservableList.get(equipmentCount - 1).getItemID() + 1, "New", "New", true, "New"));
            }
            else
            {
                equipmentDatabase.addEquipmentEntry(new Equipment(0, "New", "New", true, "New"));
            }
            equipmentTableView.scrollTo(equipmentCount+1);
            equipmentTableView.requestFocus();
            equipmentTableView.getSelectionModel().select(equipmentCount);
            equipmentTableView.getFocusModel().focus(equipmentCount);
        });

        ContextMenu equipmentTableContextMenu = new ContextMenu();
        MenuItem deleteAll = new MenuItem("deleteAll");
        deleteAll.setOnAction(event ->
        {
            equipmentObservableList.removeAll();
            equipmentObservableList.clear();
            equipmentDatabase.deleteAllEntries();
        }
        );
        equipmentTableContextMenu.getItems().add(equipmentNewMenuItem);
        equipmentTableContextMenu.getItems().add(deleteAll);
        equipmentTableView.setContextMenu(equipmentTableContextMenu);
        //**********************END TEST***************************************

        //equipmentTableView.setItems(equipmentObservableList);
        FilteredList<Equipment> equipmentFilteredData = new FilteredList<>(equipmentObservableList, p -> true);
        equipmentFilterTextField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            equipmentFilteredData.setPredicate(equipment ->
            {
                String lowerCaseFilter = newValue.toLowerCase();
                String intToString = "";
                try
                {
                    intToString = Integer.toString(equipment.getItemID());
                }
                catch (NumberFormatException e)
                {
                    //e.printStackTrace();
                }
                // If filter text is empty, display all students.
                if (newValue.isEmpty())
                {
                    return true;
                }
                // Compare first name and last name of every person with filter text.
                if (equipment.getItemName().toLowerCase().contains(lowerCaseFilter) || equipment.getItemCategory().toLowerCase().contains(lowerCaseFilter) || equipment.getFunctionalWrapper().toLowerCase().contains(lowerCaseFilter))
                {
                    return true; // Filter matches first name.
                }
                else if(!intToString.isEmpty())
                {
                    if(intToString.equalsIgnoreCase(lowerCaseFilter))
                        return true; // Filter matches student id.
                }

                return false; // Does not match.
            });
        });
        SortedList<Equipment> equipmentSortedList = new SortedList<>(equipmentFilteredData);
        equipmentSortedList.comparatorProperty().bind(equipmentTableView.comparatorProperty());
        equipmentTableView.setItems(equipmentSortedList);




































        ///////////////

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
        studentTableView.setPlaceholder(new Label("No students registered!"));
        studentIDColumn.setEditable(false);
        studentIDColumn.setCellValueFactory(new PropertyValueFactory<>("studentID"));
        studentIDColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        studentIDColumn.setOnEditCommit((CellEditEvent<Student, Integer> event) ->
        {
            tempStudent = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if(validation.isValidStudentID(event.getNewValue().toString()))
                tempStudent.setID(Integer.parseInt(event.getNewValue().toString()));
            System.out.println(tempStudent.toString());
            studentDatabase.editStudentEntry(tempStudent);
        });
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        studentNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        studentNameColumn.setOnEditCommit((CellEditEvent<Student, String> event) ->
        {
            tempStudent = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if(!event.getNewValue().isEmpty())
                tempStudent.setStudentName(event.getNewValue());
            studentDatabase.editStudentEntry(tempStudent);
            System.out.println(tempStudent.toString());
        });

        studentCourseColumn.setCellValueFactory(new PropertyValueFactory<>("studentCourse"));
        studentCourseColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        studentCourseColumn.setOnEditCommit((CellEditEvent<Student, String> event) ->
        {
            tempStudent = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if(!event.getNewValue().isEmpty())
                tempStudent.setStudentCourse(event.getNewValue());
            studentDatabase.editStudentEntry(tempStudent);
        });

        studentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("studentEmail"));
        studentEmailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        studentEmailColumn.setOnEditCommit((CellEditEvent<Student, String> event) ->
        {
            tempStudent = event.getTableView().getItems().get(event.getTablePosition().getRow());
            if(validation.checkValidEmail(event.getNewValue()))
                tempStudent.setStudentEmail(event.getNewValue());
            studentDatabase.editStudentEntry(tempStudent);
        });
        studentPhoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("studentPhoneNumber"));
        studentPhoneNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());
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
        FilteredList<Student> studentFilteredData = new FilteredList<>(studentsObservableList, p -> true);
        filterTextField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            studentFilteredData.setPredicate(studentObject ->
            {
                String lowerCaseFilter = newValue.toLowerCase();
                String intToString = "";
                try
                {
                    intToString = Integer.toString(studentObject.getStudentID());
                }
                catch (NumberFormatException e)
                {
                    //e.printStackTrace();
                }
                // If filter text is empty, display all students.
                if (newValue.isEmpty())
                {
                    return true;
                }
                // Compare first name and last name of every person with filter text.
                if (studentObject.getStudentName().toLowerCase().contains(lowerCaseFilter))
                {
                    return true; // Filter matches first name.
                }
                else if(!intToString.isEmpty())
                {
                    if(intToString.contains(lowerCaseFilter))
                        return true; // Filter matches student id.
                }
                return false; // Does not match.
            });
        });
        SortedList<Student> sortedData = new SortedList<>(studentFilteredData);
        sortedData.comparatorProperty().bind(studentTableView.comparatorProperty());
        studentTableView.setItems(sortedData);
    }

    @Override
    public void onStudentDatabaseUpdate()
    {
        studentsObservableList.clear();
        studentsObservableList.addAll(studentDatabase.getAllStudents());
    }

    @Override
    public void onEquipmentDatabaseUpdate()
    {
        equipmentObservableList = equipmentDatabase.getAllEquipment();
        equipmentTableView.setItems(equipmentObservableList);
        equipmentCount = equipmentObservableList.size();
    }

    @Override
    public void onCodeScanner(String QRCode)
    {
        Platform.runLater(()->
        {
            if(MainViewController.index == 1 && tabPane.getSelectionModel().getSelectedIndex()==1)
            {
                equipmentFilterTextField.clear();
                equipmentFilterTextField.setText(QRCode);
                equipmentFilterTextField.selectAll();
                equipmentFilterTextField.requestFocus();
            }
        });
    }

    @FXML
    private void onPrintClicked()
    {
        if(equipmentTableView.getSelectionModel().getSelectedCells().size() > 0)
        {
            Runnable runnable = ()->
            {
                List<Equipment> listOfEquipment = new ArrayList<>();
                ObservableList<Integer> temp = equipmentTableView.getSelectionModel().getSelectedIndices();
                listOfEquipment.addAll(temp.stream().map(index -> equipmentTableView.getItems().get(index)).collect(Collectors.toList()));
                PDFRenderer pdfRenderer = new PDFRenderer();
                pdfRenderer.createLabelsFromQRCode(listOfEquipment);
            };
            Thread printToPDFThread = new Thread(runnable);
            printToPDFThread.start();
        }
    }
}