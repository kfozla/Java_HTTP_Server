package com.kfozla.httpserver.config;

public class ConfigurationManager {
    private static ConfigurationManager myConfigurationManager;
    private static Configuration myCurrentConfiguration;
    private ConfigurationManager(){
    }
    public static ConfigurationManager getInstance(){
        if (myConfigurationManager==null) {
            myConfigurationManager = new ConfigurationManager();
        }
        return myConfigurationManager;
    }
    //load a config file
    public void loadConfigurationFile(String filepath){

    }

    public static Configuration getCurrentConfiguration() {
        return myCurrentConfiguration;
    }
}
