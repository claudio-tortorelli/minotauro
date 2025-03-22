package claudiosoft.minotauro;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.Constants;
import java.io.File;
import java.io.IOException;

public class Minotauro {

    private static String configFilePath;
    private static Config config;
    private static BasicLogger logger;

//    private static final Logger logger = LoggerFactory.getLogger(Minotauro.class);
    public static void main(String[] args) throws IOException {
        // Print the tool version on standard output
        System.out.println("Minotauro v1.0");
        logger = BasicLogger.get(BasicLogger.LogLevel.DEBUG, Constants.LOGGER_NAME);
        File logFile = new File("./minotauro.log");
        logger.addFileHandler(logFile);
        logger.info("Minotauro v1.0");
        logger.info("----------------------");

        // Parse command line arguments and check for unknown switches
        configFilePath = "../../config/config.ini";
        parseArgs(args);

        config = new Config(new File(configFilePath));

        // Initialize SLF4J logger based on configuration
        // ...
        // Do some work...
        // If the tool ends without errors, return 0 to the system
        System.exit(0);
    }

    private static void parseArgs(String[] args) {
        if (args.length == 0) {
            System.err.println("No arguments provided");
            System.exit(1);
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("--c") || arg.startsWith("--config")) {
                // Set the custom configuration file path
                configFilePath = args[++i];
            } else {
                System.err.println("Unknown switch: " + arg);
                System.exit(1);
            }
        }
    }
}
