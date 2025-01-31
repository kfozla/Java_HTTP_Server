package com.kfozla.http;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpHeaderParseTest {
    private HttpParser httpParser;
    private Method parseHeaderMethod;

    @BeforeAll
    public void beforeClass() throws NoSuchMethodException{
        httpParser=new HttpParser();
        Class<HttpParser> clas = HttpParser.class;
        parseHeaderMethod=clas.getDeclaredMethod("parseHeaders", InputStreamReader.class, HttpRequest.class);
        parseHeaderMethod.setAccessible(true);
    }
    @Test
    public void testSimpleSingleHeader() throws InvocationTargetException, IllegalAccessException {
        HttpRequest request = new HttpRequest();
        parseHeaderMethod.invoke(httpParser,generateSimpleSingleHeaderMessage(), request);
        assertEquals(1,request.getHeaderNames().size());
        assertEquals("localhost:8080",request.getHeader("Host"));
    }
    @Test
    public void testMultipleHeader() throws InvocationTargetException, IllegalAccessException {
        HttpRequest request = new HttpRequest();
        parseHeaderMethod.invoke(httpParser,generateMultipleHeaderMessage(), request);
        assertEquals(15,request.getHeaderNames().size());
        assertEquals("keep-alive",request.getHeader("Connection"));
    }
    @Test
    public void testSpaceBeforeColumn() throws InvocationTargetException, IllegalAccessException {
        HttpRequest request = new HttpRequest();
        try {
            parseHeaderMethod.invoke(httpParser, spaceBeforeColumnError(), request);
        }catch (InvocationTargetException e){
            if (e.getCause() instanceof HttpParsingException){
                assertEquals(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST,((HttpParsingException) e.getCause()).getErrorCode() );
            }
        }
    }
    private InputStreamReader generateSimpleSingleHeaderMessage(){
        String rawData="Host: localhost:8080\r\n";
                //"GET / HTTP/1.1\r\n" +
                //"Connection: keep-alive\r\n" +
                //"Cache-Control: max-age=0\r\n" +
                //"sec-ch-ua: \"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"\r\n" +
                //"sec-ch-ua-mobile: ?0\r\n" +
                //"sec-ch-ua-platform: \"macOS\"\r\n" +
                //"Upgrade-Insecure-Requests: 1\r\n" +
                //"User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36\r\n" +
                //"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                //"Sec-Fetch-Site: none\r\n" +
                //"Sec-Fetch-Mode: navigate\r\n" +
                //"Sec-Fetch-User: ?1\r\n" +
                //"Sec-Fetch-Dest: document\r\n" +
                //"Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                //"Accept-Language: tr-TR,tr;q=0.9,en-US;q=0.8,en;q=0.7\r\n"+
                //"\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        InputStreamReader reader = new InputStreamReader(inputStream,StandardCharsets.US_ASCII);
        return reader;
    }
    private InputStreamReader generateMultipleHeaderMessage() {
        String rawData =
                "Host: localhost:8080\r\n" +
                        "Connection: keep-alive\r\n" +
                        "Cache-Control: max-age=0\r\n" +
                        "sec-ch-ua: \"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"\r\n" +
                        "sec-ch-ua-mobile: ?0\r\n" +
                        "sec-ch-ua-platform: \"macOS\"\r\n" +
                        "Upgrade-Insecure-Requests: 1\r\n" +
                        "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36\r\n" +
                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                        "Sec-Fetch-Site: none\r\n" +
                        "Sec-Fetch-Mode: navigate\r\n" +
                        "Sec-Fetch-User: ?1\r\n" +
                        "Sec-Fetch-Dest: document\r\n" +
                        "Accept-Encoding: gzip, deflate, br, zstd\r\n" +
                        "Accept-Language: tr-TR,tr;q=0.9,en-US;q=0.8,en;q=0.7\r\n" +
                        "\r\n"; // End of headers (empty line)

        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
    }
    private InputStreamReader spaceBeforeColumnError() {
        String rawData =
                "Host : localhost:8080\r\n\r\n"; // End of headers (empty line)

        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return new InputStreamReader(inputStream, StandardCharsets.US_ASCII);
    }


}
