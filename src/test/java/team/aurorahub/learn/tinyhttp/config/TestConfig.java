package team.aurorahub.learn.tinyhttp.config;

import static team.aurorahub.learn.tinyhttp.tinyUtils.IoTools.formatURI;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

import org.junit.Test;

/**
 * @author Chanth Miao
 * @version 1.0
 */
public class TestConfig {
    private static final String testConfPath = "resources\\test\\test.json";

    private Config sampleConfig;

    public TestConfig() {
        sampleConfig = new Config(testConfPath);
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