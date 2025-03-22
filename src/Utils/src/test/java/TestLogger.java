
import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.Constants;
import java.io.File;

/**
 *
 * @author claudio.tortorelli
 */
public class TestLogger {

    public static void main(String[] args) throws Exception {
        BasicLogger logger = BasicLogger.get(BasicLogger.LogLevel.DEBUG, Constants.LOGGER_NAME);
        File logFile = new File("./target/test.log");
        logger.addFileHandler(logFile);

        logger.info("logger started");
        logger.debug("debug active");
        logger.warn("warning");
        logger.error("error!", new Exception("error"));

    }
}
