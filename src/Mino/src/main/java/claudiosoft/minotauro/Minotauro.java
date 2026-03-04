package claudiosoft.minotauro;

import claudiosoft.baseplugin.BasePlugin;
import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.commons.Constants;
import claudiosoft.indexer.IndexFactory;
import claudiosoft.indexer.IndexMechanism;
import claudiosoft.indexer.IndexMechanismType;
import claudiosoft.plugin.UpdateDB;
import claudiosoft.plugin.utils.PluginUtils;
import claudiosoft.transientdata.TransientProvider;
import claudiosoft.utils.BasicUtils;
import claudiosoft.utils.Failures;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

/**
 *
 *
 * @author claudio.tortorelli
 */
public class Minotauro {

    private static String configFilePath;
    private static Task curTask;
    private static BasicLogger logger;
    private static LinkedList<BasePlugin> pluginList;
    private static IndexMechanism indexer;

    public static void main(String[] args) throws IOException, CTException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        int nGeneralErrors = 0;
        try {
            // Print the tool version on standard output
            String ver = Version.getVersion();
            System.out.println("Minotauro " + ver);

            configFilePath = "../../config/config.ini";
            parseArgs(args);

            Config config = new Config(new File(configFilePath));

            setupLogger(config);

            printLogHeader(ver);

            setupIndex(config);

            initTransientProvider(config);

            pluginList = PluginUtils.loadPlugins(config);

            BasicUtils.startElapsedTime();
            if (curTask == Task.PROCESS_IMAGES) {
                logger.info("= start plugin process =");
                for (BasePlugin plugin : pluginList) {
                    try {
                        plugin.init(config);
                        plugin.apply(indexer);
                    } catch (CTException ex) {
                        logger.error(ex.getMessage(), ex);
                        nGeneralErrors++; // when an exception arrives here then the entire plugin is crashed
                    } finally {
                        indexer.resetIndex();
                        plugin.close();
                    }
                }
            } else if (curTask == Task.UPDATE_DB) {
                logger.info("= start update db =");
                UpdateDB plugin = null;
                try {
                    plugin = (UpdateDB) PluginUtils.getPlugin(pluginList, "UpdateDB");
                    if (plugin == null) {
                        throw new CTException("UpdateDB plugin not enabled", CTError.PLUGIN_NOT_ENABLED);
                    }
                    plugin.init(config);
                    plugin.apply(indexer);
                } catch (CTException ex) {
                    logger.error(ex.getMessage(), ex);
                    nGeneralErrors++; // when an exception arrives here then the entire plugin is crashed
                } finally {
                    indexer.resetIndex();
                    if (plugin != null) {
                        plugin.close();
                    }
                }
            }
        } catch (CTException ex) {
            logger.error(ex.getMessage(), ex);
            nGeneralErrors++;
        } finally {
            int nFailures = Failures.getFailures(); // this is the single plugin thread failure count
            logger.info(String.format("process terminated with %d general errors and %d failures in %d seconds", nGeneralErrors, nFailures, BasicUtils.getElapsedTime()));
            System.exit(nGeneralErrors == 0 ? 0 : 1); // If the tool ends without errors, return 0 to the system
        }
    }

    private static void setupLogger(Config config) throws CTException {
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
    }

    private static void printLogHeader(String version) {
        logger.info("----------------------------");
        logger.info("  Minotauro " + version);
        logger.info("----------------------------");

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
    }

    private static void setupIndex(Config config) throws CTException, IOException {
        String rootFolder = config.get("index", "rootPath");
        String index = config.get("index", "indexPath", "./index.txt");
        String folders = config.get("index", "folderPath", "./folders.txt");
        if (rootFolder.isEmpty() || index.isEmpty()) {
            throw new CTException("root folder and index path are required", CTError.FILESYSTEM_GENERIC_ERROR);
        }

        boolean onlyIndex = curTask == Task.BUILD_INDEX;
        indexer = IndexFactory.get(IndexMechanismType.BASIC);
        String filter = config.get("index", "filter");
        indexer.init(new File(rootFolder), new File(index), new File(folders), filter);
        indexer.setForcedBuild(onlyIndex);
        indexer.buildIndex();
        if (onlyIndex) {
            logger.info("index built");
            System.exit(0);
        }
    }

    private static void initTransientProvider(Config config) throws CTException, IOException {
        String rootFolder = config.get("index", "rootPath");
        TransientProvider.init(new File(rootFolder), new File(config.get("transient", "transientDataPath", "./tsImages")));
    }

    private static void parseArgs(String[] args) throws CTException {
        curTask = Task.NONE;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i].trim().toLowerCase();

            if (arg.startsWith("-c") || arg.startsWith("--config")) {
                // Set the custom configuration file path
                configFilePath = args[++i];
            } else if (arg.startsWith("-i") || arg.startsWith("--index")) {
                if (curTask != Task.NONE) {
                    throw new CTException("ambiguous switch selection", CTError.INVALID_CMDLINE_SWITCH);
                }
                curTask = Task.BUILD_INDEX;
            } else if (arg.startsWith("-d") || arg.startsWith("--db")) {
                if (curTask != Task.NONE) {
                    throw new CTException("ambiguous switch selection", CTError.INVALID_CMDLINE_SWITCH);
                }
                curTask = Task.UPDATE_DB;
            } else {
                System.err.println("Unknown switch: " + arg);
                if (curTask != Task.NONE) {
                    throw new CTException("ambiguous switch selection", CTError.INVALID_CMDLINE_SWITCH);
                }
            }
        }
        if (curTask == Task.NONE) {
            curTask = Task.PROCESS_IMAGES; // default one
        }
    }
}
