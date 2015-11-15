package cu.models;

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
    //other.
    public StudentDatabase(String database)
    {
        loadDatabase(database);
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
                firstRunStatement.executeUpdate("CREATE TABLE Students(cardUID VARCHAR(256)," +
                        "studentName VARCHAR(256), " +
                        "studentEmail VARCHAR(256)," +
                        "studentCourse VARCHAR(256)," +
                        "studentPhoneNumber INT," +
                        "studentID INT NOT NULL PRIMARY KEY)");
                firstRunStatement.close();
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