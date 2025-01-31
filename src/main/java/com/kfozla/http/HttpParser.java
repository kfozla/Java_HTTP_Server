package com.kfozla.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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
        parseHeaders(inputStreamReader,httpRequest);
        parseBody(inputStreamReader,httpRequest);

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
                } else if (!requestTargetParsed) {
                    LOGGER.debug("Request Line Target to Process:{}", processingDataBuffer.toString());
                    httpRequest.setRequestTarget(processingDataBuffer.toString());
                    requestTargetParsed=true;
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
    private void parseHeaders(InputStreamReader inputStreamReader, HttpRequest httpRequest) {
    }

    private void parseBody(InputStreamReader inputStreamReader, HttpRequest httpRequest) {
    }
}
