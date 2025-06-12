package claudiosoft.minotauro;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.commons.Constants;
import claudiosoft.imageplugin.BaseImagePlugin;
import claudiosoft.indexer.Indexer;
import claudiosoft.transientimage.TransientImageProvider;
import claudiosoft.utils.BasicUtils;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * //TODO analyze the extension to take note of presence of some files (video,
 * documents etc)
 *
 * //TODO add a statistic analyzer that can produce an ipothesis about the
 * remaining time of a plugin work
 *
 * //TODO how to handle thread not critic failures
 *
 * //TODO check the plugin name right assignements in classes
 *
 * //TODO, timer should have a map of times
 *
 * //TODO implement the wiki search
 *
 * //TODO implement a translation plugin
 *
 * //TODO evaluate a tag extraction from description
 *
 * //TODO transient image must become transient data project with a base object
 * and a new transientfolder
 *
 * //TODO check force use ollama gpu
 *
 * //TODO define the plugin order and sequence
 *
 * //TODO a plugin to get all extensions in input folders
 *
 * //TODO simplify the plugin classes and framework
 *
 * //TODO review plugin failure
 *
 * //TODO exception must have a code ID to handle the error
 *
 * //TODO File writes should be minimized or added to a queue
 *
 * //TODO each plugin must check if the transiend with its data is already
 * present. If not do it or skip instead
 *
 * @author claudio.tortorelli
 */
public class Minotauro {

    private static String configFilePath;
    private static boolean rebuildIndexOnly;
    private static BasicLogger logger;

    public static void main(String[] args) throws IOException, CTException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // Print the tool version on standard output
        String ver = Version.getVersion();
        System.out.println("Minotauro " + ver);

        rebuildIndexOnly = false;
        configFilePath = "../../config/config.ini";
        parseArgs(args);

        Config config = new Config(new File(configFilePath));

        BasicLogger.LogLevel logLevel = BasicLogger.LogLevel.NORMAL;
        if (config.get("logger", "level").equalsIgnoreCase("debug")) {
            logLevel = BasicLogger.LogLevel.DEBUG;
        }
        if (config.get("logger", "toFile", "true").equalsIgnoreCase("true")) {
            File loggerFile = new File(config.get("logger", "filePath", "./minotauro.log"));
            if (config.get("logger", "append", "true").equalsIgnoreCase("false")) {
                loggerFile.delete();
            }
            logger = BasicLogger.get(logLevel, Constants.LOGGER_NAME, loggerFile);
        } else {
            // console logger
            logger = BasicLogger.get(logLevel, Constants.LOGGER_NAME);
        }

        logger.info("----------------------");
        logger.info("Minotauro " + ver);
        logger.info("----------------------");

        Runtime runtimeEnv = Runtime.getRuntime();
        String osArch = System.getProperty("os.arch");
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String homeDir = System.getProperty("user.home");
        String javaHome = System.getProperty("java.home");
        String javaVer = System.getProperty("java.version");
        int nProc = runtimeEnv.availableProcessors();
        long diskSize = new File("/").getTotalSpace();
        long ram = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalMemorySize();

        logger.info(String.format("- operating system name: %s", osName));
        logger.info(String.format("- operating system arch: %s", osArch));
        logger.info(String.format("- operation System version: %s", osVersion));
        logger.info(String.format("- user home: %s", homeDir));
        logger.info(String.format("- processors available: %d", nProc));
        logger.info(String.format("- disk size: %d mb", diskSize / (1024 * 1024)));
        logger.info(String.format("- total ram available: %d mb", ram / (1024 * 1024)));
        logger.info(String.format("- java home: %s", javaHome));
        logger.info(String.format("- java version: %s", javaVer));

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
        indexer.buildIndex(rebuildIndexOnly);
        if (rebuildIndexOnly) {
            logger.info("index built");
            System.exit(0);
        }

        LinkedList<BaseImagePlugin> pluginList = new LinkedList<>();
        int nPlugin = 100;
        final Class<?>[] defaultConstructor = {int.class};
        for (int iPlug = 0; iPlug < nPlugin; iPlug++) {
            // instance classes by names
            String pluginId = String.format("plugin_%02d", iPlug + 1);
            String pluginName = config.get("plugins", pluginId);
            if (pluginName == null || pluginName.isEmpty()) {
                break;
            }
            String pluginClassName = String.format("claudiosoft.imageplugin.%s", pluginName);

            int step = iPlug + 1;
            boolean enabled = Boolean.parseBoolean(config.get(pluginName, "enabled", "false"));
            if (!enabled) {
                logger.warn(String.format("plugin %s is not enabled", pluginName));
                continue;
            }

            Class<?> clazz = Class.forName(pluginClassName);
            Constructor<?> constructor = clazz.getConstructor(defaultConstructor);
            pluginList.add((BaseImagePlugin) constructor.newInstance(step));
        }

        // if no enabled plugin are present, terminate
        if (pluginList.isEmpty()) {
            logger.info("no enabled plugin found");
            System.exit(0);
        }

        logger.info(String.format("%d plugins loaded", pluginList.size()));

        // sort plugin by ascending by step
        Collections.sort(pluginList, new Comparator<BaseImagePlugin>() {
            @Override
            public int compare(BaseImagePlugin a, BaseImagePlugin b) {
                if (a.getStep() < b.getStep()) {
                    return -1;
                } else if (a.getStep() == b.getStep()) {
                    return 0;
                }
                return 1;
            }
        });
        for (BaseImagePlugin plugin : pluginList) {
            logger.info(String.format("- %s", plugin.getClass().getName()));
        }

        String transientRootPath = config.get("transient", "transientRootPath", "./tsImages");
        TransientImageProvider.init(new File(rootFolder), new File(transientRootPath));

        int nErrors = 0;
        int nWarns = 0;
        BasicUtils.startElapsedTime();
        logger.info("= start plugin process =");
        for (BaseImagePlugin plugin : pluginList) {
            try {
                plugin.init(config);
                plugin.apply(indexer);
            } catch (CTException ex) {
                nErrors++;
            } finally {
                indexer.reset();
                plugin.close();
            }
        }

        logger.info(String.format("process terminated with %d errors and %d warnings in %d seconds", nErrors, nWarns, BasicUtils.getElapsedTime()));
        System.exit(nErrors == 0 ? 0 : 1); // If the tool ends without errors, return 0 to the system
    }

    private static void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("-c") || arg.startsWith("--config")) {
                // Set the custom configuration file path
                configFilePath = args[++i];
            } else if (arg.startsWith("-i") || arg.startsWith("--index")) {
                // Set the custom configuration file path
                rebuildIndexOnly = true;
            } else {
                System.err.println("Unknown switch: " + arg);
                System.exit(1);
            }
        }
    }
}
