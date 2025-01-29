package com.kfozla.httpserver;

import com.kfozla.httpserver.config.Configuration;
import com.kfozla.httpserver.config.ConfigurationManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    public static void main(String[] args) {
        System.out.println("Starting Server");
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration config = ConfigurationManager.getInstance().getCurrentConfiguration();
        System.out.println("Using Port:"+config.getPort());
        System.out.println("Using WebRoot:"+config.getWebroot());

        try {
            ServerSocket serverSocket = new ServerSocket(config.getPort());
            Socket socket = serverSocket.accept();

            InputStream inputStream= socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            String html = "<html><head><title>Simple Http Server</title></head><body><h1>Simple Java HTTP Server</h1></body></html>";
            final String crlf ="\n\r";
            String response="HTTP/1.1 200 OK" + crlf+
                        "Content-Length: " +html.getBytes().length+crlf+
                        crlf+
                        html+
                        crlf+crlf;
            outputStream.write(response.getBytes());
            inputStream.close();
            outputStream.close();
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
