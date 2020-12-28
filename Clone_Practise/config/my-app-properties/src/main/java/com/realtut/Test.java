package com.realtut;
 
public class Test {
    public static void main(String[] args) {
        ReadPropertiesSingleton demo = ReadPropertiesSingleton.getInstance();
        System.out.println(demo.getProperty("username"));
        System.out.println(demo.getProperty("password"));
        demo.printProperties();
        
    }
}