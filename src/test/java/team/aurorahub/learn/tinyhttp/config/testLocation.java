package team.aurorahub.learn.tinyhttp.config;

import com.alibaba.fastjson.*;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Chanth Miao
 * @version 1.0
 */
public class testLocation {
    private static final String baseStr = "{\"type\":0,\"handler\":\"plain\",\"accessiable\": true}";

    private tinyLocation testedObj;

    public testLocation() {
        JSONObject sample = JSONObject.parseObject(baseStr);
        testedObj = new tinyLocation();
        testedObj.load(sample);
    }

    @Test
    public void testIsAccessiable() {
        assertTrue("test isAccessiable()", testedObj.isAccessiable());
    }

    @Test
    public void testSetAccessiable() {
        testedObj.setAccessible(false);
        assertTrue("test setAccessiable()", (testedObj.isAccessiable()) == false);
    }

    @Test
    public void testGetHandlerType() {
        assertEquals("test getHandlerType()", testedObj.getHandlerType(), 0);
    }

    @Test
    public void testSetHandlerType() {
        testedObj.setHandlerType(1);
        assertEquals("test getHandlerType()", testedObj.getHandlerType(), 1);
        testedObj.setHandlerType(0);
        assertEquals("test getHandlerType()", testedObj.getHandlerType(), 0);
    }

    @Test
    public void testGetHandlerPath() {
        assertEquals("test getHandlerPath()", testedObj.getHandlerPath(), "plain");
    }

    @Test
    public void testSetHandlerPath() {
        testedObj.setHandlerPath("socket");
        assertEquals("test setHandlerPath()", testedObj.getHandlerPath(), "socket");
        testedObj.setHandlerPath("plain");
        assertEquals("test setHandlerPath()", testedObj.getHandlerPath(), "plain");
    }
}