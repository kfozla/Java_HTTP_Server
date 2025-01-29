package com.kfozla.httpserver.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.kfozla.httpserver.util.Json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
    public void loadConfigurationFile(String filepath)  {
        FileReader fileReader= null;
        try {
            fileReader = new FileReader(filepath);
        } catch (FileNotFoundException e) {
            throw new HttpConfigurationException(e);
        }
        StringBuffer sb= new StringBuffer();
        int i ;
        while (true){
            try {
                if (!((i = fileReader.read()) !=-1)) break;
            } catch (IOException e) {
                throw new HttpConfigurationException(e);
            }
            sb.append((char) i);
        }
        JsonNode config = null;
        try {
            config = Json.parse(sb.toString());
        } catch (JsonProcessingException e) {
            throw new HttpConfigurationException("Error while parsing configuration file",e);
        }
        try {
            myCurrentConfiguration= Json.fromJson(config,Configuration.class);
        } catch (JsonProcessingException e) {
            throw new HttpConfigurationException("Error while parsing configuration file, internal",e);
        }

    }

    public static Configuration getCurrentConfiguration() {
        if(myCurrentConfiguration==null){
            throw new HttpConfigurationException("No current configuration is available");
        }
        return  myCurrentConfiguration;
    }
}
