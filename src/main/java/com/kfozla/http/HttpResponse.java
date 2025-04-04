package com.kfozla.http;

import java.net.http.HttpRequest;

public class HttpResponse {
    private HttpRequest httpRequest ;
    private final String crlf = "\n\r";
    private String contentType;

    private byte [] body;
    private String headers = "HTTP/1.1 200 OK" + crlf +
            "Content-Type: " + contentType + crlf +
            "Content-Length: " + body.length + crlf +
            "Connection: close" + crlf + crlf;

    HttpResponse(HttpRequest httpRequest){
        this.httpRequest=httpRequest;

    }
}
