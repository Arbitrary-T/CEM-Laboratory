package cu.models;

import cu.Main;
import cu.interfaces.DatabaseInterface;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.awt.*;
import java.io.File;
import java.sql.*;

/**
 * Created by T on 12/11/2015.
 */
public class StudentDatabase extends Database
{
    //Enhance program functionality - for future use, automatically reset the equipment, for example ICD.
    private Connection databaseConnection;
    private PreparedStatement insertStudent;
    private PreparedStatement deleteStudent;
    private PreparedStatement updateStudent;
    private PreparedStatement searchStudent;
    private PreparedStatement getAllStudents;
    String createTableStatement = "CREATE TABLE Students(" +
            "cardUID VARCHAR(256) NOT NULL PRIMARY KEY, " +
            "studentName VARCHAR(256), " +
            "studentID INT," +
            "studentEmail VARCHAR(256), " +
            "studentCourse VARCHAR(256), " +
            "studentPhoneNumber VARCHAR(256))";

    private static DatabaseInterface databaseAgent;

    public static void activateAgent(DatabaseInterface mainAgent)
    {
        databaseAgent = mainAgent;
    }
    public StudentDatabase(String database)
    {
        activateAgent(databaseAgent);
        databaseConnection = loadDatabase(database, createTableStatement);
        try
        {
            if(databaseConnection != null)
            {
                insertStudent = databaseConnection.prepareStatement("INSERT INTO Students VALUES (?,?,?,?,?,?)");
                deleteStudent = databaseConnection.prepareStatement("DELETE FROM Students WHERE cardUID = ?");
                updateStudent = databaseConnection.prepareStatement("UPDATE Students SET studentName=?, studentID=? , studentEmail=?, studentCourse=?, studentPhoneNumber=? WHERE cardUID = ?");
                searchStudent = databaseConnection.prepareStatement("SELECT * FROM Students WHERE cardUID = ?"); /////
                getAllStudents = databaseConnection.prepareStatement("SELECT * FROM Students");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public boolean editStudentEntry(Student studentData)
    {
        if(databaseConnection != null)
        {
            try
            {
                updateStudent.setString(1, studentData.getStudentName());
                updateStudent.setInt(2, studentData.getStudentID());
                updateStudent.setString(3, studentData.getStudentEmail());
                updateStudent.setString(4, studentData.getStudentCourse());
                updateStudent.setString(5, studentData.getStudentPhoneNumber());
                updateStudent.setString(6, studentData.getCardUID());
            }
            catch(SQLException e)
            {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }
    public boolean deleteStudentEntry(String cardUID)
    {
        if(databaseConnection != null)
        {
            try
            {
                deleteStudent.setString(1, cardUID);
                deleteStudent.executeUpdate();
                databaseAgent.onStudentDatabaseUpdate();
                return true;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        return false;
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
                databaseAgent.onStudentDatabaseUpdate();
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
            if(resultSet != null)
            {
                    while (resultSet.next())
                    {
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
                    //System.out.println(resultSet.getString(1) + resultSet.getString(2) + resultSet.getInt(3) + resultSet.getString(4) + resultSet.getString(5) + resultSet.getString(6));
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

}