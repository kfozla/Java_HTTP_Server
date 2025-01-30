package com.kfozla.httpserver.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener extends Thread{
    private static Logger LOGGER= LoggerFactory.getLogger(ServerListener.class);
    private int port;
    private String webSocket;
    private ServerSocket serverSocket;
    public  ServerListener(int port, String webSocket) throws IOException {
        this.port=port;
        this.webSocket=webSocket;

        serverSocket = new ServerSocket(this.port);
    }
    @Override
    public void run(){
        try {
            while(serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                LOGGER.info(" Connection Accepted: " + socket.getInetAddress());
                HttpConnectionWorker worker = new HttpConnectionWorker(socket);
                worker.start();
            }
        } catch (IOException e) {
          LOGGER.error("Problem with setting socket",e);
        }finally {
            if(serverSocket!=null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
