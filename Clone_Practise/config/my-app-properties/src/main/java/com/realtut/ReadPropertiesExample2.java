package com.realtut;
 
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
 
public class ReadPropertiesExample2 {
    private static final String FILE_CONFIG = "/config.properties";
 
    public static void main(String[] args) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = ReadPropertiesExample2.class.getClassLoader()
                    .getResourceAsStream(FILE_CONFIG);
 
            // load properties from file
            properties.load(inputStream);
 
            // get property by name
            System.out.println(properties.getProperty("username"));
            System.out.println(properties.getProperty("password"));
 
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