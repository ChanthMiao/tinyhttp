package team.aurorahub.learn.tinyhttp.core;

import team.aurorahub.learn.tinyhttp.config.Config;

/**
 * This class can be used as a demo of tinyhttp.
 * 
 * @author Chanth Miao
 * @version 1.0
 */
public class Server {
    private ThreadPoolManager manager;
    private Config myConf;

    /**
     * Generate an {@code Server} instance accroding to specific configure.
     * 
     * @param myConf The {@code Config} that used to generate {@code Server}.
     */
    public Server(Config newConf) {
        manager = new ThreadPoolManager(128);
        myConf = newConf;
    }

    /**
     * Run the {@code Server} imediately.
     */
    public void run() {
        manager.run(myConf);
    }

    /**
     * The simple demo.
     * 
     * @param args The initialize args.
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            throw new RuntimeException("YOU NEED TO PROVIDE CONFIGURE FILE BY ARGS.");
        } else if (args[0].equals("-c")) {
            System.out.println("Loading custemd configure...");
            Config myConf = new Config(args[1]);
            System.out.println("Done!");
            System.out.println("Create server instance with thread-Pool...");
            Server newServer = new Server(myConf);
            System.out.println("Done!");
            int port = myConf.getPort();
            System.out.println("Listening on port " + port);
            newServer.run();
        } else {
            throw new RuntimeException("INVALID OPTION DETECT.");
        }
    }
}