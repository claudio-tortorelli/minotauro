package claudiosoft.minotauro;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Constants;
import claudiosoft.indexer.Indexer;
import java.io.File;
import java.io.IOException;

public class Minotauro {

    private static String configFilePath;
    private static Config config;
    private static BasicLogger logger;

//    private static final Logger logger = LoggerFactory.getLogger(Minotauro.class);
    public static void main(String[] args) throws IOException, CTException {
        // Print the tool version on standard output
        System.out.println("Minotauro v1.0");

        configFilePath = "../../config/config.ini";
        parseArgs(args);

        config = new Config(new File(configFilePath));

        //////
        logger = null;
        if (config.get("logger", "enable").equalsIgnoreCase("true")) {
            //  setup logger by config
            BasicLogger.LogLevel logLevel = BasicLogger.LogLevel.NORMAL;
            if (config.get("logger", "level").equalsIgnoreCase("debug")) {
                logLevel = BasicLogger.LogLevel.DEBUG;
            }
            String logFile = config.get("logger", "filePath");
            if (logFile.isEmpty()) {
                logFile = "./minotauro.log";
            }
            logger = BasicLogger.get(logLevel, Constants.LOGGER_NAME, new File(logFile));
        } else {
            logger = BasicLogger.get(BasicLogger.LogLevel.NONE, Constants.LOGGER_NAME);
        }
        logger.info("Minotauro v1.0");
        logger.info("----------------------");

        if (config.get("index", "build").equalsIgnoreCase("true")) {
            String rootFolder = config.get("index", "rootPath");
            String index = config.get("index", "indexPath");
            if (rootFolder.isEmpty() || index.isEmpty()) {
                throw new CTException("root folder and index path are required");
            }
            String filter = config.get("index", "filter");
            Indexer indexer = new Indexer(new File(rootFolder), new File(index));
            if (!filter.isEmpty()) {
                indexer = new Indexer(new File(rootFolder), new File(index), filter);
            }
            indexer.buildIndex();
            logger.info("extensions found:");
            for (String ext : indexer.getExtensions()) {
                logger.info(ext);
            }
        }

        if (config.get("plugins", "").equalsIgnoreCase("true")) {

        }

        // If the tool ends without errors, return 0 to the system
        System.exit(0);
    }

    private static void parseArgs(String[] args) {
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
