
import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.Constants;
import java.io.File;

/**
 *
 * @author claudio.tortorelli
 */
public class TestLogger {

    public static void main(String[] args) throws Exception {

        File logFile = new File("./target/test.log");
        BasicLogger logger = BasicLogger.get(BasicLogger.LogLevel.DEBUG, Constants.LOGGER_NAME, logFile);

        logger.info("logger started");
        logger.debug("debug active");
        logger.warn("warning");
        logger.error("error!", new Exception("error"));

    }
}
