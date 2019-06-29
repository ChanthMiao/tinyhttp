package team.aurorahub.learn.tinyhttp.tinyUtils;

import static org.junit.Assert.assertEquals;
import static team.aurorahub.learn.tinyhttp.tinyUtils.IoTools.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.junit.Test;

/**
 * @author Chanth Miao
 * @version 1.0
 */
public class TestUtil {
    @Test
    public void testReadToString() {
        String testTextPath = "resources/test/testText.txt";
        String orignText = "Hello! Try to read this file into a String variable without loop.";
        try {
            String testTextStr = readToString(testTextPath);
            assertEquals("test readToString()", orignText, testTextStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTinyURLDecoding() {
        String encodedUrl = "name%3d%e5%b0%8f%e6%9d%8e%26age%3d17";
        String orignUrl = "name=小李&age=17";
        assertEquals("test tinyURLDecoding()", orignUrl, tinyURLDecoding(encodedUrl, "utf-8"));
    }

    @Test
    public void testTinyURLEncoding() {
        String encodedUrl = "name%3d%e5%b0%8f%e6%9d%8e%26age%3d17";
        String orignUrl = "name=小李&age=17";
        assertEquals("test tinyURLEncoding()", encodedUrl, tinyURLEncoding(orignUrl, "utf-8"));
    }

    @Test
    public void testFormatURI() {
        String originURI = "\\var\\www\\sample";
        String formatedURI = "/var/www/sample";
        assertEquals("test formatURI()", formatedURI, formatURI(originURI));
    }

    @Test
    public void testGetURLParams() {
        String encodedURI = "name%3d%e5%b0%8f%e6%9d%8e%26age%3d17";
        HashMap<String, String> kvs = new HashMap<String, String>();
        int nums = getURLParams(encodedURI, kvs);
        assertEquals("test getURLParams()", nums, 2);
        assertEquals("test getURLParams()", "小李", kvs.get("name"));
        assertEquals("test getURLParams()", "17", kvs.get("age"));
    }

    @Test
    public void testGetURIQuery() {
        HashMap<String, String> KVs = new HashMap<String, String>();
        String encodedURI = "name%3d%e5%b0%8f%e6%9d%8e%26age%3d17";
        KVs.put("name", "小李");
        KVs.put("age", "17");
        assertEquals("test testGetURIQuery()", encodedURI, getURIQuery(KVs));
    }

    @Test
    public void testSafeHeaderLineReader() {
        InputStreamReader testInputReader;
        try {
            testInputReader = new InputStreamReader(new FileInputStream("resources/test/readLineTest.txt"));
            assertEquals("test testInputReader()", "Hello, world", safeHeaderLineReader(testInputReader));
            assertEquals("test testInputReader()", "Good bye!", safeHeaderLineReader(testInputReader));
            testInputReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException f) {
            f.printStackTrace();
        }
    }

    @Test
    public void testGetContentType() {
        String expectedType1 = "text/plain";
        File target1 = new File("resources/test/readLineTest.txt");
        String realType1 = getContentType(target1);
        assertEquals("test getContentType()", expectedType1, realType1);
        File target2 = new File("resources/test/test.json");
        String expectedType2 = "application/json";
        String realType2 = getContentType(target2);
        assertEquals("test getContentType()", expectedType2, realType2);
    }
}