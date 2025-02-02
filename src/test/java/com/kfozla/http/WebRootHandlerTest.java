package com.kfozla.http;

import com.kfozla.httpserver.core.io.WebRootHandler;
import com.kfozla.httpserver.core.io.WebRootNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WebRootHandlerTest {
    private WebRootHandler webRootHandler;
    private Method method;
    private Method method2;
    @BeforeAll
    public void beforeClass() throws WebRootNotFoundException, NoSuchMethodException {
        webRootHandler=new WebRootHandler("webroot");
        Class<WebRootHandler> clas = WebRootHandler.class;
        method = clas.getDeclaredMethod("checkIfEndsWithSlash", String.class);
        method.setAccessible(true);
        method2=clas.getDeclaredMethod("checkIfPathExist", String.class);
        method2.setAccessible(true);
    }
    @Test
    void goodPath(){
        try {
            WebRootHandler webRootHandler = new WebRootHandler("/Users/yusuf/Desktop/BasicHttpServer/webroot");
        }catch (WebRootNotFoundException e){
            fail(e);
        }
    }
    @Test
    void badPath(){
        try {
            WebRootHandler webRootHandler = new WebRootHandler("/Users/yusuffffff/Desktop/BasicHttpServer/webroot");
            fail();
        }catch (WebRootNotFoundException e){

        }
    }
    @Test
    void relativePath(){
        try {
            WebRootHandler webRootHandler = new WebRootHandler("webroot");
        }catch (WebRootNotFoundException e){
            fail(e);
        }
    }
    @Test
    void endsWithSlashTest(){
        try {
           boolean result =(boolean) method.invoke(webRootHandler,"index.html");
           assertFalse(result);
        } catch (IllegalAccessException e) {
            fail(e);
        } catch (InvocationTargetException e) {
            fail(e);
        }
    }
    @Test
    void endsWithSlashTest2(){
        try {
            boolean result =(boolean) method.invoke(webRootHandler,"index.html/");
            assertTrue(result);
        } catch (IllegalAccessException e) {
            fail(e);
        } catch (InvocationTargetException e) {
            fail(e);
        }
    }
    @Test
    void checkPathExist(){
        try {
            boolean result = (boolean) method2.invoke(webRootHandler,"index.html");
            assertTrue(result);
        } catch (IllegalAccessException e) {
            fail(e);
        } catch (InvocationTargetException e) {
            fail(e);
        }
    }
    @Test
    void getFileMimeTest(){
        try {
            String mimeType= webRootHandler.getFileMimeType("/");
            assertEquals(mimeType,"text/html");
        }catch(FileNotFoundException e) {
            fail(e);
        }
    }
    @Test
    void getFileMimeTest2(){
        try {
            String mimeType= webRootHandler.getFileMimeType("/example.png");
            assertEquals(mimeType,"image/png");
        }catch(FileNotFoundException e) {
            fail(e);
        }
    }
}
