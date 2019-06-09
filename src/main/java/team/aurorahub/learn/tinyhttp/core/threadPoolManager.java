package team.aurorahub.learn.tinyhttp.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import team.aurorahub.learn.tinyhttp.config.config;
import team.aurorahub.learn.tinyhttp.handler.*;

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
    private handlerImp tinyHandler;
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
        tinyHandler = null;
        fixThreadPool = Executors.newFixedThreadPool(maxCon);
    }

    /**
     * Register handler by loading customed configure.
     * 
     * @param newConf The user configure.
     */
    public void setUpHandlers(config newConf) {
        tinyHandler = new handlerImp(newConf);
        try {
            sSocket = new ServerSocket(newConf.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run it, begin to work.
     */
    public void run() {
        while (true) {
            try {
                Socket newSocket = sSocket.accept();
                InputStream in = newSocket.getInputStream();
                OutputStream out = newSocket.getOutputStream();
                fixThreadPool.execute(() -> {
                    while (newSocket.isClosed() == false && newSocket.isOutputShutdown() == false
                            && newSocket.isInputShutdown() == false) {
                        tinyHandler.handle(in, out);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}