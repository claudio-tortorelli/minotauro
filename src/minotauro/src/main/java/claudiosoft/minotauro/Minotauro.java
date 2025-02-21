package claudiosoft.minotauro;

import java.lang.System.Logger;

public class Minotauro {

    private static final Logger logger = LoggerFactory.getLogger(Minotauro.class);

    public static void main(String[] args) {
        // Print the tool version on standard output
        System.out.println("Minotauro v1.0");

        // Parse command line arguments and check for unknown switches
        parseArgs(args);

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
                String configFilePath = args[++i];
            } else {
                System.err.println("Unknown switch: " + arg);
                System.exit(1);
            }
        }
    }
}
