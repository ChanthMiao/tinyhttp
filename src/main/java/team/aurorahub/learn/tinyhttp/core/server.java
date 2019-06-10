package team.aurorahub.learn.tinyhttp.core;

import team.aurorahub.learn.tinyhttp.config.config;

/**
 * This class can be used as a demo of tinyhttp.
 * 
 * @author Chanth Miao
 * @version 1.0
 */
public class server {
    private threadPoolManager manager;
    private config myConf;

    /**
     * Generate an server instance accroding to specific configure.
     * 
     * @param myConf The config that used to generate server.
     */
    public server(config newConf) {
        manager = new threadPoolManager(128);
        myConf = newConf;
    }

    /**
     * Run the server imediately.
     */
    private void run() {
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
            config myConf = new config(args[1]);
            server newServer = new server(myConf);
            newServer.run();
        } else {
            throw new RuntimeException("INVALID OPTION DETECT.");
        }
    }
}