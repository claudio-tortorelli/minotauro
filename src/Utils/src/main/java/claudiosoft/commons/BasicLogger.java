/**
 *
 *
 * MIT License - 2021
 */
package claudiosoft.commons;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A really simple logging wrapper. Aimed to print to console (stdout) only
 */
public class BasicLogger {

    static {
        // console logger formatting: 2020-04-04 19:42:45 INFO - msg
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS - %5$s%6$s%n");
    }

    /**
     * Three supported log level
     */
    public enum LogLevel {
        NONE,
        NORMAL,
        DEBUG
    }

    private static Logger internalLogger = null;
    private LogLevel level = LogLevel.NORMAL;

    private static BasicLogger logger;

    public static BasicLogger get() throws SecurityException, IOException {
        return get(LogLevel.NORMAL);
    }

    public static BasicLogger get(LogLevel level) throws SecurityException, IOException {
        return get(level, "Logger");
    }

    public static BasicLogger get(LogLevel level, String logName) throws SecurityException, IOException {
        return get(level, "Logger", null);
    }

    public static BasicLogger get(LogLevel level, String logName, File logFile) throws SecurityException, IOException {
        if (logger == null) {
            logger = new BasicLogger(level, logName, logFile);
        }
        return logger;
    }

    private BasicLogger(LogLevel level, String logName, File logFile) throws SecurityException, IOException {
        internalLogger = Logger.getLogger(logName);
        Handler handlerObj = new ConsoleHandler();

        switch (level) {
            case DEBUG:
                handlerObj.setLevel(Level.ALL);
                internalLogger.setLevel(Level.ALL);
                break;
            case NORMAL:
                handlerObj.setLevel(Level.INFO);
                internalLogger.setLevel(Level.INFO);
                break;
            default:
                handlerObj.setLevel(Level.OFF);
                internalLogger.setLevel(Level.OFF);
                break;
        }
        internalLogger.addHandler(handlerObj);
        if (logFile != null) {
            FileHandler fileHandler = new FileHandler(logFile.getAbsolutePath(), true);
            fileHandler.setFormatter(new SimpleFormatter());
            internalLogger.addHandler(fileHandler);
        }
        internalLogger.setUseParentHandlers(false);
        this.level = level;
    }

    public synchronized void info(String message) {
        internalLogger.log(Level.INFO, "[INFO] {0}", message);
    }

    public synchronized void warn(String message) {
        internalLogger.log(Level.WARNING, "[WARN] {0}", message);
    }

    public synchronized void error(String message) {
        error(message, null);
    }

    public synchronized void error(String message, Exception ex) {
        if (ex != null) {
            internalLogger.log(Level.SEVERE, "[ERROR] " + message, ex);
        } else {
            internalLogger.log(Level.SEVERE, "[ERROR] {0}", message);
        }
    }

    public synchronized void debug(String message) {
        if (level == LogLevel.DEBUG) {
            internalLogger.log(Level.INFO, "[DEBUG] {0}", message);
        }
    }

    public boolean isDebug() {
        return (level == LogLevel.DEBUG);
    }
}
