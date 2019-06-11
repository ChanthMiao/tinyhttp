package team.aurorahub.learn.tinyhttp.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import team.aurorahub.learn.tinyhttp.config.config;
import team.aurorahub.learn.tinyhttp.handler.*;
import team.aurorahub.learn.tinyhttp.tinyUtils.tinyLogger;

/**
 * This class manages a fix thread-Pool, and invokes the
 * {@link team.aurorahub.learn.tinyhttp.handler.handlerImp handlerImp}
 * automatically when new connection established.
 * 
 * @author Chanth Miao
 * @version 1.0
 */
public class threadPoolManager {
    private int maxCon;
    private ServerSocket sSocket;
    private ExecutorService fixThreadPool;

    /**
     * Generate a {@code threadPoolManager} with specific max concurrence threads
     * number.
     * 
     * @param max The number of threads you want.
     */
    public threadPoolManager(int max) {
        maxCon = max;
        sSocket = null;
        fixThreadPool = Executors.newFixedThreadPool(maxCon);
    }

    /**
     * Run with specific configure.
     */
    public void run(config myConf) {
        Logger myLogger = tinyLogger.getTinyLogger();
        try {
            sSocket = new ServerSocket(myConf.getPort());
            while (true) {
                Socket newSocket = sSocket.accept();
                myLogger.info(newSocket.getRemoteSocketAddress().toString() + " new cennection established.");
                fixThreadPool.execute(new handler(myConf, newSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}