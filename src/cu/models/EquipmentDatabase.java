package cu.models;

import cu.interfaces.DatabaseInterface;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.sql.*;

/**
 * Created by T on 22/11/2015.
 */
public class EquipmentDatabase
{
    Connection databaseConnection;
    PreparedStatement insertItem;
    PreparedStatement deleteItem;
    PreparedStatement updateItem;
    PreparedStatement searchItem;
    PreparedStatement getAllEquipment;
    ObservableList<Equipment> equipmentObservableList = FXCollections.observableArrayList();

    private  static DatabaseInterface agent;

    public static void activateAgent(DatabaseInterface mainAgent)
    {
        agent = mainAgent;
    }

    public EquipmentDatabase(String database)
    {
        activateAgent(agent);
        loadDatabase(database);
        try
        {
            insertItem = databaseConnection.prepareStatement("INSERT INTO Equipment VALUES (?,?,?,?,?)");
            deleteItem = databaseConnection.prepareStatement("DELETE FROM Equipment WHERE itemID = ?");
            updateItem = databaseConnection.prepareStatement("UPDATE Equipment SET itemName=?, itemCategory=? , functional=?, partOfBundle=? WHERE itemID = ?");
            searchItem = databaseConnection.prepareStatement("SELECT * FROM Equipment WHERE itemID = ?");
            getAllEquipment = databaseConnection.prepareStatement("SELECT * FROM Equipment");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
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
                        "itemID INT NOT NULL PRIMARY KEY, " +
                        "itemName VARCHAR(256), " +
                        "itemCategory VARCHAR(256), " +
                        "functional BOOLEAN, " +
                        "partOfBundle VARCHAR(256))");
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

    public boolean addEquipmentEntry(Equipment equipmentData)
    {
        if(databaseConnection != null)
        {
            try
            {
                insertItem.setInt(1, equipmentData.getItemID());
                insertItem.setString(2, equipmentData.getItemName());
                insertItem.setString(3, equipmentData.getItemCategory());
                insertItem.setBoolean(4, equipmentData.isFunctional());
                insertItem.setString(5, equipmentData.getPartOfBundle());
                insertItem.executeUpdate();
                agent.onEquipmentDatabaseUpdate();
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

    public boolean deleteEquipmentEntry(Equipment equipment)
    {
        if(databaseConnection != null)
        {
            try
            {
                deleteItem.setInt(1, equipment.getItemID());
                deleteItem.executeUpdate();
                agent.onEquipmentDatabaseUpdate();
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

    public boolean editEquipmentEntry(Equipment equipment)
    {
        if(databaseConnection != null)
        {
            //SET itemName=?, itemCategory=? , functional=?, partOfBundle=? WHERE itemID
            try
            {
                updateItem.setString(1, equipment.getItemName());
                updateItem.setString(2, equipment.getItemCategory());
                updateItem.setBoolean(3, equipment.isFunctional());
                updateItem.setString(4, equipment.getPartOfBundle());
                updateItem.setInt(5, equipment.getItemID());
                updateItem.executeUpdate();
                agent.onEquipmentDatabaseUpdate();
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
    public boolean searchEquipment(Equipment equipment)
    {
        if(databaseConnection != null)
        {
            try
            {
                searchItem.setInt(1, equipment.getItemID());
                searchItem.executeUpdate();
                agent.onEquipmentDatabaseUpdate();
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
    public ObservableList<Equipment> getAllEquipment()
    {
        equipmentObservableList.clear();
        try
        {
            //if(getAllEquipment != null)
            //{
                ResultSet resultSet = getAllEquipment.executeQuery();
                if (resultSet != null)
                {
                    while (resultSet.next())
                    {
                        System.out.println(resultSet.getInt(1) + resultSet.getString(2) + resultSet.getString(3) + resultSet.getBoolean(4) + resultSet.getString(5));
                        equipmentObservableList.add(new Equipment(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getBoolean(4), resultSet.getString(5)));
                    }
                    return equipmentObservableList;
                }
           // }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return equipmentObservableList;
    }
}
