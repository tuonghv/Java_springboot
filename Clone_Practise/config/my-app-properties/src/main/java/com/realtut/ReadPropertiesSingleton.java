package com.realtut;
 
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.*; 
 
public class ReadPropertiesSingleton {
    private static final String FILE_CONFIG = "/config.properties";
    private static ReadPropertiesSingleton instance = null;
    private Properties properties = new Properties();
 
    /**
     * Use singleton pattern to create ReadConfig object one time and use
     * anywhere
     *
     * @return instance of ReadConfig object
     */
    public static ReadPropertiesSingleton getInstance() {
        if (instance == null) {
            instance = new ReadPropertiesSingleton();
            instance.readConfig();
        }
        return instance;
    }
 
    /**
     * get property with key
     *
     * @param key
     * @return value of key
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
 

 /**
     * get property with key
     *
     */
    public void printProperties() {
        Set set = properties.entrySet(); 
  
        // iterate over the set 
        Iterator itr = set.iterator(); 
        while (itr.hasNext()) { 
  
            // print each property 
            Map.Entry entry = (Map.Entry)itr.next(); 
            System.out.println(entry.getKey() + " = "
                               + entry.getValue()); 
        } 
    }

    /**
     * read file .properties
     */
    private void readConfig() {
        InputStream inputStream = null;
        try {
            String currentDir = System.getProperty("user.dir");
            inputStream = new FileInputStream(currentDir + FILE_CONFIG);
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close objects
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}