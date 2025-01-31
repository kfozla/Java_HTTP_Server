package com.kfozla.http;

public class HttpRequest extends HttpMessage{
    private HttpMethod method;
    private String requestTarget;
    private String originalHttpVersion;//literal form
    private HttpVersion bestVersion;

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
}
