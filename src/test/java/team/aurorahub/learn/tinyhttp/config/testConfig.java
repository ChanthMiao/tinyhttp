package team.aurorahub.learn.tinyhttp.config;

import static team.aurorahub.learn.tinyhttp.tinyUtils.ioTools.formatURI;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

import org.junit.Test;

public class testConfig {
    private static final String testConfPath = "resources\\test.json";

    private config sampleConfig;

    public testConfig() {
        sampleConfig = new config(testConfPath);
    }

    @Test
    public void testGetDomain() {
        assertEquals("test getDomain", sampleConfig.getDomain(), "localhost");
    }

    @Test
    public void testSetDomain() {
        sampleConfig.setDomain("cc.scu.edu.cn");
        assertEquals("test getDomain()", sampleConfig.getDomain(), "cc.scu.edu.cn");
        sampleConfig.setDomain("localhost");
        assertEquals("test getDomain()", sampleConfig.getDomain(), "localhost");
    }

    @Test
    public void testGetPort() {
        assertEquals("test getPort()", sampleConfig.getPort(), 80);
    }

    @Test
    public void testSetPort() {
        sampleConfig.setPort(443);
        assertEquals("test getPort()", sampleConfig.getPort(), 443);
        sampleConfig.setPort(80);
        assertEquals("test getPort()", sampleConfig.getPort(), 80);
    }

    @Test
    public void testGetPaths() {
        LinkedList<String> expectedList = new LinkedList<String>();
        expectedList.add("/console");
        expectedList.add("/api");
        expectedList.add("/");
        assertEquals("test getPaths()", expectedList, sampleConfig.getPaths());
    }

    @Test
    public void testGetConfigFilePath() {
        assertEquals("test getConfigFilePath()", formatURI(testConfPath), sampleConfig.getConfigFilePath());
    }
    // TODO: More test samples are needed.
}