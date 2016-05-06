package cu.models.utilities;

import java.io.*;
import java.util.Properties;

/**
 * Created by T on 07/04/2016.
 */
public class PropertiesManager
{
    private final String CONFIG_FILE_NAME = "config.properties";
    private Properties properties = new Properties();
    private OutputStream outputStream;
    private InputStream inputStream;

    public PropertiesManager()
    {
        configureProperties();
    }

    /**
     * configures the properties manager i.e. create/load properties file
     */
    private void configureProperties()
    {
        try
        {
            inputStream = new FileInputStream(CONFIG_FILE_NAME);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        if(inputStream != null)
        {
            try
            {
                properties.load(inputStream);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * sets/saves a property the method also updates existing properties
     * @param key the property identifies e.g. firstRun
     * @param value the property's value e.g. false
     */
    public void setProperties(String key, String value)
    {
        try
        {
            outputStream = new FileOutputStream(CONFIG_FILE_NAME);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        if(properties.getProperty(key) != null)
        {
            properties.replace(key, value);
        }
        else
        {
            properties.put(key, value);
        }
        try
        {
            properties.store(outputStream, null);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                outputStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * returns a property value
     * @param key the property to return
     * @return
     */
    public String getProperty(String key)
    {
        return properties.getProperty(key);
    }

    /**
     * accessor method
     * @return properties
     */
    public Properties getProperties()
    {
        return this.properties;
    }
}
