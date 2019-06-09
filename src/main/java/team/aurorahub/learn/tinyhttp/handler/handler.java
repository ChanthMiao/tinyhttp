package team.aurorahub.learn.tinyhttp.handler;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This interfer define the action entry of specific handler class.
 * 
 * @author Chanth Miao
 * @version 1.0
 */
public interface handler {
    public void handle(InputStream in, OutputStream out);
}