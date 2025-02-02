package com.kfozla.httpserver.core.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;

public class WebRootHandler {
    private File webRoot;
    private Logger LOGGER = LoggerFactory.getLogger(WebRootHandler.class);
    public WebRootHandler(String webRootPath) throws WebRootNotFoundException{
        webRoot=new File(webRootPath);
        if (!webRoot.exists() || !webRoot.isDirectory()){
            throw new WebRootNotFoundException("WebRoot Not Found");
        }
    }
    private boolean checkIfEndsWithSlash(String relativePath){
        return relativePath.endsWith("/");
    }
    private boolean checkIfPathExist(String relativePath){
        File file = new File(webRoot,relativePath);
        if (!file.exists()){
            return false;
        }
        try {
            if (file.getCanonicalPath().startsWith(webRoot.getCanonicalPath())){
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
    public String getFileMimeType(String relativePath) throws FileNotFoundException {
        if (checkIfEndsWithSlash(relativePath)){
            relativePath="index.html";// serve index.html by default
        }
        if (!checkIfPathExist(relativePath)){
            throw new FileNotFoundException("File Not Found"+relativePath);
        }
        File file = new File(webRoot,relativePath);
        String mimeType = URLConnection.getFileNameMap().getContentTypeFor(file.getName());
        if (mimeType == null){
            return "application/octet-stream";
        }
        LOGGER.info("MIME Type for " + relativePath + ": " + mimeType);  // Add this log to debug
        return mimeType;
    }
    public byte[] getFileByteArray(String relativePath) throws FileNotFoundException, ReadFileException{
        if (relativePath == null || relativePath.trim().isEmpty() || relativePath.equals("/")) {
            relativePath += "index.html";  // Default to "index.html"
        }
        if (!checkIfEndsWithSlash(relativePath)){
            relativePath = "index.html";
        }
        if (!checkIfPathExist(relativePath)){
            throw new FileNotFoundException("File Not Found " + relativePath);
        }
        File file=new File(webRoot,relativePath);
        FileInputStream inputStream = new FileInputStream(file);
        //Can Cause Trouble For Large Files
        byte [] bytes = new byte[(int)file.length()];
        try {
            inputStream.read(bytes);
            inputStream.close();
        }catch (IOException e){
            throw new ReadFileException(e);
        }
        return bytes;
    }

}
