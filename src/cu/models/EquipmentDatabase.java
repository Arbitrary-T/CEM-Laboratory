package cu.models;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by T on 22/11/2015.
 */
public class EquipmentDatabase
{
    Connection databaseConnection;

    public EquipmentDatabase(String database)
    {
        loadDatabase(database);
    }

    private boolean loadDatabase(String database)
    {
        File databaseFile = new File(database);
        String existsDatabaseURL = "jdbc:derby:" + database;
        String newDatabaseURL = "jdbc:derby:" + database + ";create=true;";

        if (databaseFile.exists())
        {
            try
            {
                databaseConnection = DriverManager.getConnection(existsDatabaseURL);
                System.out.println("Successfully connected to existing 'Equipment' database.");
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
                firstRunStatement.executeUpdate(
                        "CREATE TABLE Equipment(" +
                        "itemID VARCHAR(256) NOT NULL PRIMARY KEY, " +
                        "itemName VARCHAR(256), " +
                        "itemCount INT," +
                        "itemCategory VARCHAR(256), " +
                        "functional BOOLEAN)");
                firstRunStatement.close();
                System.out.println("Successfully connected to newly created 'Equipment' database.");
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
