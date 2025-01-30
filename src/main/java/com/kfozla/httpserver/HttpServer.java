package com.kfozla.httpserver;

import com.kfozla.httpserver.config.Configuration;
import com.kfozla.httpserver.config.ConfigurationManager;
import com.kfozla.httpserver.core.ServerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class HttpServer {
    private static Logger LOGGER= LoggerFactory.getLogger(HttpServer.class);
    public static void main(String[] args) {
        LOGGER.info("Server Starting...");

        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration config = ConfigurationManager.getInstance().getCurrentConfiguration();
        LOGGER.info("Using Port:"+config.getPort());
        LOGGER.info("Using WebRoot:"+config.getWebroot());

        ServerListener serverListener = null;
        try {
            serverListener = new ServerListener(config.getPort(), config.getWebroot());
            serverListener.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
