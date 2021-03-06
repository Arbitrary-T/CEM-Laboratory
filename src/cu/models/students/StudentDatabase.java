package cu.models.students;

import cu.interfaces.DatabaseInterface;
import cu.models.utilities.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * Created by T on 12/11/2015.
 */
public class StudentDatabase extends Database
{
    private Connection databaseConnection;
    private PreparedStatement insertStudent;
    private PreparedStatement deleteStudent;
    private PreparedStatement updateStudent;
    private PreparedStatement searchStudent;
    private PreparedStatement getAllStudents;
    private PreparedStatement deleteAll;

    private String createTableStatement = "CREATE TABLE Students(" +
            "cardUID VARCHAR(256) NOT NULL PRIMARY KEY, " +
            "studentName VARCHAR(256), " +
            "studentID INT," +
            "studentEmail VARCHAR(256), " +
            "studentCourse VARCHAR(256), " +
            "studentPhoneNumber VARCHAR(256), " +
            "studentFaultyReturns INT, " +
            "studentTotalReturns INT, " +
            "studentUsageTime BIGINT," +
            "returnNotOnTime INT)";

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
                //setup the prepared statements to call when needed.
                insertStudent = databaseConnection.prepareStatement("INSERT INTO Students VALUES (?,?,?,?,?,?,?,?,?,?)");
                deleteStudent = databaseConnection.prepareStatement("DELETE FROM Students WHERE cardUID = ?");
                updateStudent = databaseConnection.prepareStatement("UPDATE Students SET studentName=?, studentID=? , studentEmail=?, studentCourse=?, studentPhoneNumber=?,studentFaultyReturns=?, studentTotalReturns=?, studentUsageTime=?, returnNotOnTime=? WHERE cardUID = ?");
                searchStudent = databaseConnection.prepareStatement("SELECT * FROM Students WHERE cardUID = ?"); /////
                getAllStudents = databaseConnection.prepareStatement("SELECT * FROM Students");
                deleteAll = databaseConnection.prepareStatement("DELETE FROM Students WHERE 1=1");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * edits a student entry in the database
     * @param studentData the Student to edit
     * @return true if the operation was successful
     */
    public boolean editStudentEntry(Student studentData)
    {
        if(databaseConnection != null)
        {
            try
            {
                System.out.println(studentData.toString());
                updateStudent.setString(1, studentData.getStudentName());
                updateStudent.setInt(2, studentData.getStudentID());
                updateStudent.setString(3, studentData.getStudentEmail());
                updateStudent.setString(4, studentData.getStudentCourse());
                updateStudent.setString(5, studentData.getStudentPhoneNumber());
                updateStudent.setInt(6, studentData.getFaultyReturns());
                updateStudent.setInt(7, studentData.getTotalReturns());
                updateStudent.setLong(8, studentData.getEquipmentUsageTime());
                updateStudent.setInt(9, studentData.getReturnNotOnTime());
                updateStudent.setString(10, studentData.getCardUID());

                updateStudent.executeUpdate();
                databaseAgent.onStudentDatabaseUpdate();
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

    /**
     * deletes a student in the database
     * @param cardUID the NFC card's Unique Identification Number (stored in the database once a student registers)
     * @return true if operation was successful
     */
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

    /**
     * deletes every entry in the database
     * @return true if the operation was successful
     */
    public boolean deleteAll()
    {
        if(databaseConnection != null)
        {
            try
            {
                deleteAll.executeUpdate();
                databaseAgent.onStudentDatabaseUpdate();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * adds a new student to the database
     * @param studentData the Student to add
     * @return true if the operation was successful
     */
    public boolean addStudentEntry(Student studentData)
    {
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
                insertStudent.setInt(7, studentData.getFaultyReturns());
                insertStudent.setInt(8, studentData.getTotalReturns());
                insertStudent.setLong(9, studentData.getEquipmentUsageTime());
                insertStudent.setInt(10, studentData.getReturnNotOnTime());

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

    /**
     * loads the database into a ObservableList
     * @return returns the loaded database
     */
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
                    studentsFromDatabase.add(new Student(resultSet.getString(1), resultSet.getString(2), resultSet.getInt(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getInt(7), resultSet.getInt(8), resultSet.getInt(9),resultSet.getInt(10)));
                }
                resultSet.close();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return studentsFromDatabase;
    }

    /**
     * search the database for a Student
     * @param cardUID the student's NFC Card UID
     * @return the Student if found, else returns null
     */
    public Student searchDatabase(String cardUID)
    {
        try
        {
            searchStudent.setString(1, cardUID);
            ResultSet resultSet = searchStudent.executeQuery();
            Student result = null;
            if(resultSet.next())
            {
                result = new Student(resultSet.getString(1), resultSet.getString(2), resultSet.getInt(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getInt(7), resultSet.getInt(8), resultSet.getInt(9),resultSet.getInt(10));
            }
            resultSet.close();
            return result;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}