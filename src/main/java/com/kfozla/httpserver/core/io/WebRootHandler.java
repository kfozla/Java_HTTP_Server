package com.kfozla.httpserver.core.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        Path filePath = Paths.get(file.getAbsolutePath());
        String mimeType = null;
        try {
            mimeType = Files.probeContentType(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Get Mime Type Error",e);
        }
        //String mimeType = URLConnection.getFileNameMap().getContentTypeFor(file.getName());
        if (mimeType == null){
            LOGGER.info("MİME TYPE NULL");
            return "application/octet-stream";
        }
        LOGGER.info("MIME Type for " + relativePath + ": " + mimeType);  // Add this log to debug
        return mimeType;
    }
    public byte[] getFileByteArray(String relativePath) throws FileNotFoundException, ReadFileException{
        if (relativePath == null || relativePath.trim().isEmpty() || relativePath.equals("/")) {
            relativePath += "index.html";  // Default to "index.html"
        }
        if (!checkIfPathExist(relativePath)){
            throw new FileNotFoundException("File Not Found " + relativePath);
        }
        File file=new File(webRoot,relativePath);
        if (file.isDirectory()) {
            file = new File(file, "index.html"); // Serve index.html if directory is requested
        }
        if (!file.exists() || !file.canRead()) {
            throw new FileNotFoundException("File cannot be read " + relativePath);
        }

        FileInputStream inputStream = new FileInputStream(file);
        //Can Cause Trouble For Large Files
        //byte [] bytes = new byte[(int)file.length()];
        byte [] bytes ;
        try {
             bytes = Files.readAllBytes(file.toPath());
            int bytesRead=inputStream.read(bytes);
            if (bytesRead != file.length()) {
                LOGGER.warn("Expected to read {} bytes, but only read {} bytes.", file.length(), bytesRead);
            }
            //inputStream.close();

        }catch (IOException e){
            throw new ReadFileException(e);
        }
        LOGGER.info("File size read: " + file.length());
        return bytes;

    }

}
