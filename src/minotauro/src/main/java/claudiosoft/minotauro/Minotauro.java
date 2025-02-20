package claudiosoft.minotauro;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Minotauro {

    private static final Logger LOGGER = Logger.getLogger(Minotauro.class.getName());

    public static void main(String[] args) throws IOException {
        List<String> arguments = Arrays.asList(args);

        // Parse command line arguments
        String configFilePath;
        if (arguments.contains("--config") || arguments.contains("-c")) {
            configFilePath = arguments.get(arguments.indexOf("--config") + 1);
        } else if (arguments.size() == 0) {
            configFilePath = "config.properties"; // default configuration file path
        } else {
            LOGGER.log(Level.SEVERE, "Invalid arguments: only --config or -c followed by a file path is allowed");
            System.exit(1);
        }

        // Initialize logger
        Logger.getLogger("").addHandler(new ConsoleLoggingHandler());

        // Print tool version on standard output
        System.out.println("Tool version: 1.0");

        // Load configuration from file
        Properties props = new Properties();
        try {
            props.loadFromXML(Files.newInputStream(Paths.get(configFilePath)));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading configuration: " + e.getMessage());
            System.exit(1);
        }

        // Use configuration to do something useful
        // ...
        // Exit with 0 value if tool ends without errors
        System.exit(0);
    }
}
