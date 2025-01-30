package com.kfozla.httpserver.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.IIOException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpConnectionWorker extends Thread{
    private Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorker.class);
    private Socket socket;
    public  HttpConnectionWorker (Socket socket){
        this.socket=socket;
    }
    @Override
    public void run(){
        InputStream inputStream= null;
        OutputStream outputStream= null;
        try{
             inputStream = socket.getInputStream();
             outputStream = socket.getOutputStream();

            String html = "<html><head><title>Simple Http Server</title></head><body><h1>Simple Java HTTP Server</h1></body></html>";
            final String crlf = "\n\r";
            String response = "HTTP/1.1 200 OK" + crlf +
                    "Content-Length: " + html.getBytes().length + crlf +
                    crlf +
                    html +
                    crlf + crlf;
            outputStream.write(response.getBytes());

            LOGGER.info("Connection processing finished");
        } catch (IOException e) {
            LOGGER.error("Problem with communication",e);
        }finally {
           if (inputStream!=null){
               try {
                   inputStream.close();
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
           }
           if(outputStream!=null){
               try {
                   outputStream.close();
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
           }
           if (socket!=null){
               try {
                   socket.close();
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
           }

        }
    }
}
