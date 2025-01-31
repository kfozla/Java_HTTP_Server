package com.kfozla.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HttpVersionTest {
    @Test
    public void getBestVersionTestExactMatch(){
        HttpVersion version= null;
        try {
            version = HttpVersion.getBestCompatibleVersion("HTTP/1.1");
        } catch (BadHttpVersionException e) {
            //TODO add exception texts for version
           e.printStackTrace();
           fail();
        }
        assertNotNull(version);
        assertEquals(version,HttpVersion.HTTP_1_1);
    }
    @Test
    public void getBestVersionTestBadFormat(){
        HttpVersion version= null;
        try {
            version = HttpVersion.getBestCompatibleVersion("httP/1.1");
            fail();
        } catch (BadHttpVersionException e) {

        }
    }
    @Test
    public void getBestVersionTestHigherVersion(){
        HttpVersion version= null;
        try {
            version = HttpVersion.getBestCompatibleVersion("HTTP/1.2");
            assertNotNull(version);
            assertEquals(version,HttpVersion.HTTP_1_1);
        } catch (BadHttpVersionException e) {
            fail();
        }
    }

}
