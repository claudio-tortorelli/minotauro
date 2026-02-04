package claudiosoft.minotauro;

import claudiosoft.baseplugin.BasePlugin;
import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.commons.Constants;
import claudiosoft.indexer.IncrementalMechanism;
import claudiosoft.indexer.IndexFactory;
import claudiosoft.indexer.IndexMechanism;
import claudiosoft.indexer.IndexMechanismType;
import claudiosoft.transientdata.TransientProvider;
import claudiosoft.utils.BasicUtils;
import claudiosoft.utils.Failures;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 *
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
        logger.info("  Minotauro " + ver);
        logger.info("----------------------");

        Runtime runtimeEnv = Runtime.getRuntime();
        String osArch = System.getProperty("os.arch");
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String homeDir = System.getProperty("user.home");
        String javaHome = System.getProperty("java.home");
        String javaVer = System.getProperty("java.version");
        String javaTemp = System.getProperty("java.io.tmpdir");
        int nProc = runtimeEnv.availableProcessors();
        long diskSize = new File("/").getTotalSpace();
        long freeSpace = new File("/").getFreeSpace();
        long ram = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalMemorySize();

        logger.info(String.format("- operating system name: %s", osName));
        logger.info(String.format("- operating system arch: %s", osArch));
        logger.info(String.format("- operation System version: %s", osVersion));
        logger.info(String.format("- user home: %s", homeDir));
        logger.info(String.format("- processors available: %d", nProc));
        logger.info(String.format("- disk size: %d mb", diskSize / (1024 * 1024)));
        logger.info(String.format("- free space: %d mb", freeSpace / (1024 * 1024)));
        logger.info(String.format("- total ram available: %d mb", ram / (1024 * 1024)));
        logger.info(String.format("- java home: %s", javaHome));
        logger.info(String.format("- java version: %s", javaVer));
        logger.info(String.format("- java temp folder: %s", javaTemp));

        String rootFolder = config.get("index", "rootPath");
        String index = config.get("index", "indexPath", "./index.txt");
        String folders = config.get("index", "folderPath", "./folders.txt");
        if (rootFolder.isEmpty() || index.isEmpty()) {
            throw new CTException("root folder and index path are required", CTError.FILESYSTEM_GENERIC_ERROR);
        }

        IncrementalMechanism indexMechanism = new IncrementalMechanism();

        String filter = config.get("index", "filter");
        IndexMechanism indexer = IndexFactory.get(IndexMechanismType.BASIC);
        indexer.init(new File(rootFolder), new File(index), new File(folders), filter);
        indexer.setForcedBuild(rebuildIndexOnly);
        indexer.buildIndex();
        if (rebuildIndexOnly) {
            logger.info("index built");
            System.exit(0);
        }

        LinkedList<BasePlugin> pluginList = new LinkedList<>();
        int nPlugin = 100;
        final Class<?>[] defaultConstructor = {int.class};
        for (int iPlug = 0; iPlug < nPlugin; iPlug++) {
            // instance classes by names
            String pluginId = String.format("plugin_%02d", iPlug + 1);
            String pluginName = config.get("plugins", pluginId);
            if (pluginName == null || pluginName.isEmpty()) {
                break;
            }
            String pluginClassName = String.format("claudiosoft.plugin.%s", pluginName);

            int step = iPlug + 1;
            boolean enabled = Boolean.parseBoolean(config.get(pluginName, "enabled", "false"));
            if (!enabled) {
                logger.warn(String.format("plugin %s is not enabled", pluginName));
                continue;
            }

            Class<?> clazz = Class.forName(pluginClassName);
            Constructor<?> constructor = clazz.getConstructor(defaultConstructor);
            pluginList.add((BasePlugin) constructor.newInstance(step));
        }

        // if no enabled plugin are present, terminate
        if (pluginList.isEmpty()) {
            logger.info("no enabled plugin found");
            System.exit(0);
        }

        logger.info(String.format("%d plugins loaded", pluginList.size()));

        // sort plugin by ascending by step
        Collections.sort(pluginList, new Comparator<BasePlugin>() {
            @Override
            public int compare(BasePlugin a, BasePlugin b) {
                if (a.getStep() < b.getStep()) {
                    return -1;
                } else if (a.getStep() == b.getStep()) {
                    return 0;
                }
                return 1;
            }
        });
        for (BasePlugin plugin : pluginList) {
            logger.info(String.format("- %s", plugin.getClass().getName()));
        }

        TransientProvider.init(new File(rootFolder), new File(config.get("transient", "transientDataPath", "./tsImages")));

        int nGeneralErrors = 0;
        BasicUtils.startElapsedTime();
        logger.info("= start plugin process =");
        for (BasePlugin plugin : pluginList) {
            try {
                plugin.init(config);
                plugin.apply(indexer);
            } catch (CTException ex) {
                nGeneralErrors++; // when an exception arrives here then the entire plugin is crashed
            } finally {
                indexer.resetIndex();
                plugin.close();
            }
        }
        int nFailures = Failures.getFailures(); // this is the single plugin thread failure count

        logger.info(String.format("process terminated with %d general errors and %d failures in %d seconds", nGeneralErrors, nFailures, BasicUtils.getElapsedTime()));
        System.exit(nGeneralErrors == 0 ? 0 : 1); // If the tool ends without errors, return 0 to the system
    }

    private static void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i].trim().toLowerCase();

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
