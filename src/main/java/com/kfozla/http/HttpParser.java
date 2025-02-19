package com.kfozla.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpParser {
    private final static Logger LOGGER= LoggerFactory.getLogger(HttpParser.class);
    private static final int SP =0x20;//32
    private static final int CR =0x0D;//13
    private static final int LF =0x0A;//10
    public HttpRequest parseHttpRequest(InputStream inputStream) throws HttpParsingException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
        HttpRequest httpRequest = new HttpRequest();
        try{
            parseRequestLine(inputStreamReader,httpRequest);
        }catch (IOException e){
           throw new RuntimeException(e);
        }
        try {
            parseHeaders(inputStreamReader,httpRequest);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        if (httpRequest.getMethod().toString().equals("POST") ||httpRequest.getMethod().toString().equals("PUT")){
            try {
                parseBody(inputStreamReader,httpRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return httpRequest;
    }
    private void parseRequestLine(InputStreamReader inputStreamReader, HttpRequest httpRequest) throws IOException, HttpParsingException {
        boolean methodParsed=false;
        boolean requestTargetParsed=false;

        StringBuilder processingDataBuffer = new StringBuilder();
        int bit;
        while ((bit = inputStreamReader.read()) >=0){
            if (bit == CR){
                bit= inputStreamReader.read();
                if (bit == LF){
                    LOGGER.debug("Request Line Version to Process:{}", processingDataBuffer.toString());
                    if (!methodParsed || !requestTargetParsed){
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }
                    try {
                        httpRequest.setHttpVersion(processingDataBuffer.toString());
                    } catch (BadHttpVersionException e) {
                        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                    }
                    LOGGER.info("Request Line Version to Process Complete");

                    return;
                }else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
            }
            if (bit == SP) {
                if (!methodParsed){
                    LOGGER.debug("Request Line Method to Process:{}", processingDataBuffer.toString());
                    httpRequest.setMethod(processingDataBuffer.toString());
                    methodParsed=true;
                    LOGGER.info("Request Line Method to Process Complete");
                } else if (!requestTargetParsed) {
                    LOGGER.debug("Request Line Target to Process:{}", processingDataBuffer.toString());
                    httpRequest.setRequestTarget(processingDataBuffer.toString());
                    requestTargetParsed=true;
                    LOGGER.info("Request Line Target to Process Complete");
                } else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
                }
                processingDataBuffer.delete(0,processingDataBuffer.length());
                //TODO Process previous data
            }else {
                processingDataBuffer.append((char) bit);
                if (!methodParsed){
                    if (processingDataBuffer.length() > HttpMethod.MAX_LENGTH){
                        throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
                    }
                }
            }
        }
    }

    //private void parseHeaders(InputStreamReader inputStreamReader, HttpRequest httpRequest) throws IOException, HttpParsingException {
    //    LOGGER.info("Starting Parsing Header");
    //    int bit;
    //    StringBuilder processingDataBuffer = new StringBuilder();
    //    boolean crlfFound = false;
    //    while ((bit = inputStreamReader.read()) >=0){
    //        LOGGER.info("while iteration");
    //        if (bit == CR){
    //            bit= inputStreamReader.read();
    //            if (bit == LF){
    //                if (!crlfFound){
    //                    crlfFound=true;
    //
    //                    processSingleHeaderField(processingDataBuffer, httpRequest);
    // Two CRLF received, end of headers.//                    processingDataBuffer.delete(0,processingDataBuffer.length());
    // Exit the loop after processing the last header//                }else{
    //                    //Two crlf received, end of header
    //                }
    //            else {
    //                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
    //            }
    //        }
    // Process header data//        else{
    //            crlfFound=false;
    //            processingDataBuffer.append((char) bit);
    //            //TODO APPEND BUFFER
    //        }
    //    }
    //    LOGGER.info("Parsing header finished");
    //}
    private void parseHeaders(InputStreamReader inputStreamReader, HttpRequest httpRequest) throws IOException, HttpParsingException {
        LOGGER.info("Starting Parsing Header");
        int bit;
        StringBuilder processingDataBuffer = new StringBuilder();
        boolean crlfFound = false;
        while ((bit = inputStreamReader.read()) >= 0) {

            if (bit == CR) {
                bit = inputStreamReader.read();
                if (bit == LF) {
                    if (!crlfFound) {
                        crlfFound = true;
                        processSingleHeaderField(processingDataBuffer, httpRequest);
                        processingDataBuffer.delete(0, processingDataBuffer.length());
                    } else {

                        break;
                    }
                } else {
                    throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);//            }
                }
            } else {
                crlfFound = false;
                processingDataBuffer.append((char) bit);

            }
        }
        LOGGER.info("Parsing header finished");
    }

    private void processSingleHeaderField(StringBuilder processingDataBuffer, HttpRequest httpRequest) throws HttpParsingException {
        String rawHeader = processingDataBuffer.toString();
        Pattern pattern =Pattern.compile("^(?<fieldName>[^:]+):\\s?(?<fieldValue>.*)$");
        Matcher matcher=pattern.matcher(rawHeader);
        if (matcher.matches()){
            String fieldName = matcher.group("fieldName").trim();
            String fieldValue = matcher.group("fieldValue").trim();

            httpRequest.addHeader(fieldName, fieldValue);
        }else {
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    private void parseBody(InputStreamReader inputStreamReader, HttpRequest httpRequest) throws HttpParsingException,IOException {
        LOGGER.info("Starting Parsing Body");
        int bit;
        int bytesRead=0;
        int contentLength;
        if(httpRequest.getHeader("Content-Length").isBlank()){
            LOGGER.debug("Body with no content");
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
        contentLength = Integer.parseInt(httpRequest.getHeader("Content-Length"));
        StringBuilder processingDataBuffer = new StringBuilder();

        //int contentLength = Integer.parseInt(httpRequest.getHeader("Content-Length"));
        char[] buffer = new char[contentLength];
        while (bytesRead < contentLength) {
            int read = inputStreamReader.read(buffer, bytesRead, contentLength - bytesRead);
            if (read == -1) {
                break; // End of stream
            }
            bytesRead += read;
        }
        String body = new String(buffer, 0, bytesRead);
        httpRequest.setBody(body);
        LOGGER.info("RequestBody: "+httpRequest.getBody());

    }
}
