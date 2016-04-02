package cu.models.statistics;


import cu.models.utilities.Database;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by T on 01/04/2016.
 */
public class StatisticsDatabase extends Database
{
    private Connection databaseConnection;
    private PreparedStatement insertData;
    private PreparedStatement deleteData;
    private PreparedStatement updateData;
    private PreparedStatement doesExist;
    private PreparedStatement getData;

    String createTableStatement = "CREATE TABLE Stats(" +
            "entryDate DATE NOT NULL PRIMARY KEY, " +
            "averageUsage BIGINT, " +
            "faultyReturns INT, " +
            "totalReturns INT)";

    public StatisticsDatabase(String database)
    {
        databaseConnection = loadDatabase(database, createTableStatement);
        try
        {
            if(databaseConnection != null)
            {
                insertData = databaseConnection.prepareStatement("INSERT INTO Stats VALUES (?,?,?,?)");
                deleteData = databaseConnection.prepareStatement("DELETE FROM Stats WHERE entryDate = ?");
                updateData = databaseConnection.prepareStatement("UPDATE Stats SET averageUsage=?, faultyReturns=? , totalReturns=? WHERE entryDate = ?");
                doesExist = databaseConnection.prepareStatement("SELECT * FROM Stats WHERE entryDate = ?");
                getData = databaseConnection.prepareStatement("SELECT * FROM Stats");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public boolean addEntry(Statistics statistics)
    {
        if(databaseConnection != null)
        {
            try
            {
                insertData.setDate(1, Date.valueOf(statistics.getDate()));
                insertData.setLong(2, statistics.getAverageUsage());
                insertData.setInt(3, statistics.getFaultyReturns());
                insertData.setInt(4, statistics.getTotalReturns());
                insertData.executeUpdate();
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

    public boolean deleteEntry(LocalDate date)
    {
        if(databaseConnection != null)
        {
            try
            {
                deleteData.setDate(1, Date.valueOf(date));
                deleteData.executeUpdate();
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

    public Statistics doesExist(LocalDate date)
    {
        Statistics temp = null;
        if(databaseConnection != null)
        {
            try
            {
                doesExist.setDate(1, Date.valueOf(date));
                ResultSet resultSet = doesExist.executeQuery();
                if(resultSet!=null)
                {
                    while(resultSet.next())
                    {
                        temp = new Statistics(resultSet.getDate(1).toLocalDate(), resultSet.getLong(2), resultSet.getInt(3), resultSet.getInt(3));
                    }
                    resultSet.close();
                }
                return temp;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        return temp;
    }
    public boolean editEntry(Statistics statistics)
    {
        if(databaseConnection != null)
        {
            try
            {
                updateData.setLong(1, statistics.getAverageUsage());
                updateData.setInt(2, statistics.getFaultyReturns());
                updateData.setInt(3, statistics.getTotalReturns());
                updateData.setDate(4, Date.valueOf(statistics.getDate()));
                updateData.executeUpdate();
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

    public ArrayList<Statistics> getAll()
    {
        ArrayList<Statistics> statArrayList = new ArrayList<>();
        try
        {
            ResultSet resultSet = getData.executeQuery();
            if(resultSet != null)
            {
                while (resultSet.next())
                {
                    statArrayList.add(new Statistics(resultSet.getDate(1).toLocalDate(), resultSet.getLong(2), resultSet.getInt(3), resultSet.getInt(3)));
                }
                resultSet.close();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return statArrayList;
    }
}
