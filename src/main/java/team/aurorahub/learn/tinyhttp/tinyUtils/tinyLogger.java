package team.aurorahub.learn.tinyhttp.tinyUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * This class define the specific logger of tinyhttp.
 */
public class tinyLogger {
    private static String logFile = null;
    private static Logger myLogger = null;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Initialize the shared logger for tinyhttp.
     */
    private tinyLogger() {
        myLogger = Logger.getLogger("tinyhttp");
        myLogger.setUseParentHandlers(false);
        myLogger.addHandler(new ConsoleHandler());
        Formatter tinyFormatter = new tinyLogFormat();
        if (logFile != null) {
            try {
                FileHandler tinyFileOut = new FileHandler(logFile);
                myLogger.addHandler(tinyFileOut);
            } catch (SecurityException | IOException e) {
                System.err.println("Failded get log file");
            }
        }
        for (Handler var : myLogger.getHandlers()) {
            var.setFormatter(tinyFormatter);
        }
    }

    /**
     * Set the path of log file.
     * 
     * @param path The log file's path.
     */
    public static void setLogPath(String path) {
        logFile = path;
    }

    /**
     * Get the shared logger of tinyhttp.
     * 
     * @return The shared logger.
     */
    public static Logger getTinyLogger() {
        if (myLogger == null) {
            new tinyLogger();
        }
        return myLogger;
    }

    private class tinyLogFormat extends Formatter {

        @Override
        public String format(LogRecord record) {
            Instant it = Instant.ofEpochMilli(record.getMillis());
            LocalDateTime currentDate = LocalDateTime.ofInstant(it, ZoneId.systemDefault());
            String curr = formatter.format(currentDate);
            StringBuffer rt = new StringBuffer(64);
            rt.append(record.getLevel());
            rt.append(": ");
            rt.append(curr);
            rt.append(" ");
            rt.append(record.getMessage());
            rt.append("\n");
            return rt.toString();
        }
    }
}