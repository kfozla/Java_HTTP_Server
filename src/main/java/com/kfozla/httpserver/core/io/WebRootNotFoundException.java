package com.kfozla.httpserver.core.io;

public class WebRootNotFoundException extends Exception{
    public WebRootNotFoundException(String message){
        super(message);
    }
}
