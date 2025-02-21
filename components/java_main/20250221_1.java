import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        // Print the tool version on standard output
        System.out.println("MyTool v1.0");

        // Parse command line arguments
        String configFile = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--c") || args[i].equals("--config")) {
                if (i + 1 >= args.length) {
                    System.err.println("Invalid argument: --c or --config must be followed by a file path");
                    System.exit(1);
                } else {
                    configFile = args[i + 1];
                }
            } else {
                System.err.println("Invalid argument: " + args[i]);
                System.exit(1);
            }
        }

        // If no configuration file was specified, use the default one in the same folder as the tool
        if (configFile == null) {
            configFile = Paths.get(".").resolve("mytool.conf").toString();
        }

        // Initialize the logger
        Logger logger = new Logger(configFile);

        // Do some work...

        // If there were no errors, return 0 to the system
        System.exit(0);
    }
}
