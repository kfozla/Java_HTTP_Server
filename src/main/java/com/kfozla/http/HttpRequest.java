package com.kfozla.http;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class HttpRequest extends HttpMessage{
    private HttpMethod method;
    private String requestTarget;
    private String originalHttpVersion;//literal form
    private HttpVersion bestVersion;
    private HashMap<String,String> headers = new HashMap<>();
    private String body;
    HttpRequest(){
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestTarget() {
        return requestTarget;
    }
    public HttpVersion getBestVersion(){
        return bestVersion;
    }
    public String getOriginalHttpVersion(){
        return originalHttpVersion;
    }
    public String getBody(){return body;}

    void setMethod(String methodName) throws HttpParsingException {
        for (HttpMethod httpMethod : HttpMethod.values()){
            if (methodName.equals(httpMethod.name())){
                this.method=httpMethod;
                return;
            }
        }
        throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
    }

    void setRequestTarget(String requestTarget) throws HttpParsingException {
        if (requestTarget==null || requestTarget.length()==0){
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        this.requestTarget=requestTarget;
    }

     void setHttpVersion(String originalHttpVersion) throws HttpParsingException, BadHttpVersionException {
        this.originalHttpVersion = originalHttpVersion;
        this.bestVersion = HttpVersion.getBestCompatibleVersion(originalHttpVersion);
        if (this.bestVersion == null){
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
        }
    }
    void setBody(String body){
        this.body=body;
    }
     void addHeader(String headerName,String headerField){
        headers.put(headerName,headerField);
    }

    public Set<String> getHeaderNames() {
        return headers.keySet();
    }
    public String getHeader(String headerName){
        return headers.get(headerName);
    }
}
