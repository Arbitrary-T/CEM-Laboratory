package cu.models;

import cu.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.sql.*;

/**
 * Created by T on 12/11/2015.
 */
public class StudentDatabase
{
    private Connection databaseConnection;
    private PreparedStatement insertStudent;
    private PreparedStatement deleteStudent;
    private PreparedStatement updateStudent;
    private PreparedStatement searchStudent;
    private PreparedStatement getAllStudents;
    public boolean firstRun = true;
    public StudentDatabase(String database)
    {
        loadDatabase(database);
        try
        {
            insertStudent = databaseConnection.prepareStatement("INSERT INTO Students VALUES (?,?,?,?,?,?)");
            deleteStudent = databaseConnection.prepareStatement("DELETE FROM Students WHERE studentID = ?");
            //updateStudent = databaseConnection.prepareStatement("INSERT INTO Students VALUES (?,?,?,?,?,?)"); /////
            searchStudent = databaseConnection.prepareStatement("SELECT * FROM Students WHERE cardUID = ?"); /////
            getAllStudents = databaseConnection.prepareStatement("SELECT * FROM Students");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

    }
    public boolean addStudentEntry(Student studentData)
    {
        Main.currentStudent = studentData;
        if(databaseConnection != null)
        {
            try
            {
                insertStudent.setString(1, studentData.getCardUID());
                insertStudent.setString(2, studentData.getStudentName());
                insertStudent.setInt(3, studentData.getStudentID());
                insertStudent.setString(4, studentData.getStudentEmail());
                insertStudent.setString(5, studentData.getStudentCourse());
                insertStudent.setString(6, studentData.getStudentPhoneNumber());
                insertStudent.executeUpdate();
                return true;
            }
            catch(SQLException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
    public ObservableList<Student> getAllStudents()
    {
        ObservableList<Student> studentsFromDatabase = FXCollections.observableArrayList();
        try
        {
            ResultSet resultSet = getAllStudents.executeQuery();
            if (resultSet != null)
            {
                    while (resultSet.next())
                    {
                        System.out.println(resultSet.getString(1) + resultSet.getString(2) + resultSet.getInt(3) + resultSet.getString(4) + resultSet.getString(5) + resultSet.getString(6));
                        studentsFromDatabase.add(new Student(resultSet.getString(1), resultSet.getString(2), resultSet.getInt(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6)));
                    }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return studentsFromDatabase;
    }
    public Student searchDatabase(String cardUID)
    {
        try
        {
            searchStudent.setString(1, cardUID);
            ResultSet resultSet = searchStudent.executeQuery();

            if(resultSet != null)
            {
                while(resultSet.next())
                {
                    System.out.println(resultSet.getString(1) + resultSet.getString(2) + resultSet.getInt(3) + resultSet.getString(4) + resultSet.getString(5) + resultSet.getString(6));
                    return new Student(resultSet.getString(1), resultSet.getString(2), resultSet.getInt(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6));
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    private boolean loadDatabase(String database)
    {
        File databaseFile = new File(database);
        String existsDatabaseURL = "jdbc:derby:" + database;
        String newDatabaseURL = "jdbc:derby:" + database + ";create=true;";

        if(databaseFile.exists())
        {
            try
            {
                databaseConnection = DriverManager.getConnection(existsDatabaseURL);
                System.out.println("Successfully connected to existing 'Students' database.");
                firstRun = false;
                return true;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        else
        {
            try
            {
                databaseConnection = DriverManager.getConnection(newDatabaseURL);
                Statement firstRunStatement = databaseConnection.createStatement();
                firstRunStatement.executeUpdate("CREATE TABLE Students(" +
                        "cardUID VARCHAR(256) NOT NULL PRIMARY KEY, " +
                        "studentName VARCHAR(256), " +
                        "studentID INT," +
                        "studentEmail VARCHAR(256), " +
                        "studentCourse VARCHAR(256), " +
                        "studentPhoneNumber VARCHAR(256))");
                firstRunStatement.close();
                System.out.println("Successfully connected to newly created 'Students' database.");
                return true;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                return false;
            }
        }
    }
}