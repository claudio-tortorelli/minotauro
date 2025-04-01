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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class Minotauro {

    private static String configFilePath;
    private static Config config;
    private static BasicLogger logger;

//    private static final Logger logger = LoggerFactory.getLogger(Minotauro.class);
    public static void main(String[] args) throws IOException, CTException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // Print the tool version on standard output
        System.out.println("Minotauro v1.0");

        configFilePath = "../../config/config.ini";
        parseArgs(args);

        config = new Config(new File(configFilePath));

        BasicLogger.LogLevel logLevel = BasicLogger.LogLevel.NORMAL;
        if (config.get("logger", "level").equalsIgnoreCase("debug")) {
            logLevel = BasicLogger.LogLevel.DEBUG;
        }
        if (config.get("logger", "toFile", "true").equalsIgnoreCase("true")) {
            String logFile = config.get("logger", "filePath", "./minotauro.log");
            logger = BasicLogger.get(logLevel, Constants.LOGGER_NAME, new File(logFile));
        } else {
            // console logger
            logger = BasicLogger.get(logLevel, Constants.LOGGER_NAME);
        }
        logger.info("----------------------");
        logger.info("Minotauro v1.0");
        logger.info("----------------------");

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

        LinkedList<BaseImagePlugin> pluginList = new LinkedList<>();
        String nPlug = config.get("plugins", "plugins_num", "0");
        int nPlugin = Integer.parseInt(nPlug);
        final Class<?>[] defaultConstructor = {int.class};
        for (int iPlug = 0; iPlug < nPlugin; iPlug++) {
            // instance classes by names
            String pluginId = String.format("plugin_%02d", iPlug + 1);
            String pluginName = config.get("plugins", pluginId);
            String pluginClassName = String.format("claudiosoft.imageplugin.%s", pluginName);

            int step = Integer.parseInt(config.get(pluginName, "step", "0"));
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

        String transientRootPath = config.get("transient", "ransientRootPath", "./tsImages");
        TransientImageProvider transientImageProxy = new TransientImageProvider(new File(rootFolder), new File(transientRootPath));

        // for each image in the index execute enabled plugin using multithread
        int nPluginThread = Integer.parseInt(config.get("threads", "plugin_threads", "1"));
        logger.info(String.format("%d thread used", nPluginThread));

        int nErrors = 0;
        BasicUtils.startElapsedTime();
        logger.info("start plugin process");
        File curImage = indexer.startVisit();
        while (curImage != null) {
            logger.debug(String.format("%s - processing image %s", indexer.getVisitIndex(), curImage.getCanonicalPath()));

            // apply plugins
            for (BaseImagePlugin plugin : pluginList) {
                try {
                    plugin.init(config, transientImageProxy);
                    plugin.apply(curImage);
                } catch (CTException ex) {
                    logger.error(String.format("%s: %s", plugin.getClass().getName(), ex.getMessage()), ex);
                    nErrors++;
                }
            }
            curImage = indexer.visitNext();
        }
        logger.info(String.format("process terminated with %d errors in %d seconds", nErrors, BasicUtils.getElapsedTime()));
        System.exit(nErrors == 0 ? 0 : 1); // If the tool ends without errors, return 0 to the system
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
