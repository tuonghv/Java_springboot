package com.mycompany.app;

import com.typesafe.config.*;

// Whenever you write a library, allow people to supply a Config but
// also default to ConfigFactory.load if they don't supply one.
// Libraries generally have some kind of Context or other object
// where it's convenient to place the configuration.

public class FeatureLibContext {
    private Config config;

    // we have a constructor allowing the app to provide a custom Config
    public FeatureLibContext(Config config) {
        this.config = config;
        // This verifies that the Config is sane and has our
        // reference config. Importantly, we specify the "simple-lib"
        // path so we only validate settings that belong to this
        // library. Otherwise, we might throw mistaken errors about
        // settings we know nothing about.
        config.checkValid(ConfigFactory.defaultReference(), "feature-lib");
    }

    // This uses the standard default Config, if none is provided,
    // which simplifies apps willing to use the defaults
    public FeatureLibContext() {
        this(ConfigFactory.load());
    }

    // this is the amazing functionality provided by simple-lib
    public void printSetting(String path) {
        System.out.println("The setting from feature '" + path + "' is: " + config.getString(path));
    }
}