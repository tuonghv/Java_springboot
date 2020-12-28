package com.mycompany.app;

import com.typesafe.config.*;
/**
 * Hello world!
 *
 */
public class App 
{

    // Simple-lib is a library in this same examples/ directory.
    // This method demos usage of that library with the config
    // provided.
    private static void demoConfigInSimpleLib(Config config) {
        SimpleLibContext context = new SimpleLibContext(config);
        context.printSetting("simple-lib.foo");
        context.printSetting("simple-lib.hello");
        context.printSetting("simple-lib.whatever");
    }


    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        // Config config = ConfigFactory.load();
        // Config config = ConfigFactory.parseResources("application.conf"); 
        // retrieve key
        Config conf = ConfigFactory.load();
        System.out.println( "Hello World! xxxx" );
        System.out.println("The answer is: " + conf.getString("simple-app.answer"));
        // retrieve object
        // Config demoConfig = config.getConfig("demo");

        Config config1 = ConfigFactory.load("complex1");

        // use the config ourselves
        System.out.println("config1, complex-app.something="
                + config1.getString("complex-app.something"));

        // use the config for a library
        demoConfigInSimpleLib(config1);


        System.out.println("using SimpleLibContext");
        SimpleLibContext context = new SimpleLibContext();
        context.printSetting("simple-lib.foo");
        context.printSetting("simple-lib.hello");
        context.printSetting("simple-lib.whatever");


        FeatureLibContext featurecontext = new FeatureLibContext();
        featurecontext.printSetting("feature-lib.foo");
        featurecontext.printSetting("feature-lib.hello");
        featurecontext.printSetting("feature-lib.whatever");       


    }
}
