package com.kfozla.httpserver.core;

import com.kfozla.http.HttpParser;
import com.kfozla.http.HttpParsingException;
import com.kfozla.http.HttpRequest;
import com.kfozla.httpserver.core.io.ReadFileException;
import com.kfozla.httpserver.core.io.WebRootHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.IIOException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpConnectionWorker extends Thread{
    private Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorker.class);
    private Socket socket;
    private WebRootHandler webRootHandler;
    private HttpParser httpParser;
    public  HttpConnectionWorker (Socket socket, WebRootHandler webRootHandler, HttpParser httpParser){
        this.socket=socket;
        this.webRootHandler=webRootHandler;
        this.httpParser=httpParser;
    }
    @Override
    public void run(){
        InputStream inputStream= null;
        OutputStream outputStream= null;
        try{
             inputStream = socket.getInputStream();
             outputStream = socket.getOutputStream();


            //String html = "<html><head><title>Simple Http Server</title></head><body><h1>Simple Java HTTP Server</h1></body></html>";
            //final String crlf = "\n\r";
            //String response = "HTTP/1.1 200 OK" + crlf +
            //        "Content-Length: " + html.getBytes().length + crlf +
            //        crlf +
            //        html +
            //        crlf + crlf;
            //outputStream.write(response.getBytes());

            //this line block is new

            try {
                 HttpRequest httpRequest = httpParser.parseHttpRequest(inputStream);
                String requestTarget = httpRequest.getRequestTarget();
                byte [] body= webRootHandler.getFileByteArray(requestTarget);
                String contentType = webRootHandler.getFileMimeType(requestTarget);
                sendResponse(outputStream,contentType,body);
            }
            catch (ReadFileException e){
               LOGGER.error("Read file Exception on webroot handler",e);
            } catch (HttpParsingException e) {
                throw new RuntimeException("parser exception",e);
            }
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
    public void sendResponse(OutputStream outputStream, String contentType, byte[] body) throws IOException {
        try {
            final String crlf = "\n\r";
            String headers = "HTTP/1.1 200 OK" + crlf +
                    "Content-Type: " + contentType + crlf +
                    "Content-Length: " + body.length + crlf +
                    "Connection: close" + crlf + crlf;
            outputStream.write(headers.getBytes(StandardCharsets.US_ASCII));
            outputStream.write(body);
            outputStream.flush();
        }
        catch (IOException e) {
            LOGGER.error("error while senging response", e);
        }
    }
    public void sendResponse(OutputStream outputStream, HttpRequest httpRequest, String contentType, byte[] body) throws IOException {
        try {
            final String crlf = "\n\r";

            // Get HTTP version dynamically
            String version = httpRequest.getBestVersion().toString(); // Assume getVersion() returns the version string
            String statusCode = "200"; // Example: Use HttpStatusCode enum for status
            String statusMessage = "OK"; // Get status message from enum

            // Construct headers dynamically
            String headers = version + " " + statusCode + " " + statusMessage + crlf +
                    "Content-Type: " + contentType + crlf +
                    "Content-Length: " + body.length + crlf +
                    "Connection: close" + crlf + crlf;

            outputStream.write(headers.getBytes(StandardCharsets.US_ASCII));
            outputStream.write(body);
            outputStream.flush();
        } catch (IOException e) {
            LOGGER.error("Error while sending response", e);
        }
    }

}
