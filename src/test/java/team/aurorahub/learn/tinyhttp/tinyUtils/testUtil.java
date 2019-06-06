package team.aurorahub.learn.tinyhttp.tinyUtils;

import static org.junit.Assert.assertEquals;
import static team.aurorahub.learn.tinyhttp.tinyUtils.ioTools.*;

import org.junit.Test;

public class testUtil {
    @Test
    public void testReadToString() {
        String testTextPath = "resources/testText.txt";
        String orignText = "Hello! Try to read this file into a String variable without loop.";
        try {
            String testTextStr = readToString(testTextPath);
            assertEquals("test readToString()", testTextStr, orignText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTinyURLDecoding() {
        String encodedUrl = "http%3a%2f%2flocalhost%2fapi%2fsearch%3fname%3d%e5%b0%8f%e6%9d%8e%26age%3d17";
        String orignUrl = "http://localhost/api/search?name=小李&age=17";
        assertEquals("test tinyURLDecoding()", tinyURLDecoding(encodedUrl, "utf-8"), orignUrl);
    }

    @Test
    public void testFormatURI() {
        String originURI = "\\var\\www\\sample";
        String formatedURI = "/var/www/sample";
        assertEquals("test formatURI()", formatURI(originURI), formatedURI);
    }
}