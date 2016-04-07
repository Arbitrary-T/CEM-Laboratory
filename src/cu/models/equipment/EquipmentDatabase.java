package cu.models.equipment;

import cu.interfaces.DatabaseInterface;
import cu.models.utilities.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * Created by T on 22/11/2015.
 */
public class EquipmentDatabase extends Database
{
    private Connection databaseConnection;
    private PreparedStatement insertItem;
    private PreparedStatement deleteItem;
    private PreparedStatement updateItem;
    private PreparedStatement deleteAll;
    private PreparedStatement getAllEquipment;
    private PreparedStatement getItem;
    private ObservableList<Equipment> equipmentObservableList = FXCollections.observableArrayList();
    private  static DatabaseInterface agent;

    public static void activateAgent(DatabaseInterface mainAgent)
    {
        agent = mainAgent;
    }

    public EquipmentDatabase(String database)
    {
        String createTableStatement = "CREATE TABLE Equipment(" +
                                      "itemID INT NOT NULL PRIMARY KEY, " +
                                      "itemName VARCHAR(256), " +
                                      "itemCategory VARCHAR(256), " +
                                      "functional BOOLEAN, " +
                                      "partOfBundle VARCHAR(256))";
        activateAgent(agent);

        databaseConnection = loadDatabase(database, createTableStatement);
        try
        {
            if(databaseConnection != null)
            {
                insertItem = databaseConnection.prepareStatement("INSERT INTO Equipment VALUES (?,?,?,?,?)");
                deleteItem = databaseConnection.prepareStatement("DELETE FROM Equipment WHERE itemID = ?");
                updateItem = databaseConnection.prepareStatement("UPDATE Equipment SET itemID=?, itemName=?, itemCategory=?, functional=?, partOfBundle=? WHERE itemID=?");
                deleteAll = databaseConnection.prepareStatement("DELETE FROM Equipment WHERE 1=1");
                getItem = databaseConnection.prepareStatement("SELECT * FROM Equipment WHERE itemID = ?");
                getAllEquipment = databaseConnection.prepareStatement("SELECT * FROM Equipment");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
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
    public boolean deleteAllEntries()
    {
        try
        {
            deleteAll.executeUpdate();
            agent.onEquipmentDatabaseUpdate();
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    public boolean editEquipmentEntry(Equipment equipment, int oldID)
    {
        if(databaseConnection != null)
        {
            try
            {
                System.out.println(equipment.toString());
                updateItem.setInt(1, equipment.getItemID());
                updateItem.setString(2, equipment.getItemName());
                updateItem.setString(3, equipment.getItemCategory());
                updateItem.setBoolean(4, equipment.isFunctional());
                updateItem.setString(5, equipment.getPartOfBundle());
                updateItem.setInt(6, oldID);
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
    public Equipment getItem(int itemID)
    {
        if(databaseConnection != null)
        {
            try
            {
                getItem.setInt(1, itemID);
                ResultSet resultSet = getItem.executeQuery();
                Equipment result = null;
                if(resultSet != null)
                {
                    if(resultSet.next())
                    {
                        result= new Equipment(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getBoolean(4), resultSet.getString(5));
                    }
                    resultSet.close();
                    return result;
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
    public ObservableList<Equipment> getAllEquipment()
    {
        equipmentObservableList.clear();
        try
        {
                ResultSet resultSet = getAllEquipment.executeQuery();
                if (resultSet != null)
                {
                    while (resultSet.next())
                    {
                        //System.out.println(resultSet.getInt(1) + resultSet.getString(2) + resultSet.getString(3) + resultSet.getBoolean(4) + resultSet.getString(5));
                        equipmentObservableList.add(new Equipment(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getBoolean(4), resultSet.getString(5)));
                    }
                    resultSet.close();
                    return equipmentObservableList;
                }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return equipmentObservableList;
    }
}