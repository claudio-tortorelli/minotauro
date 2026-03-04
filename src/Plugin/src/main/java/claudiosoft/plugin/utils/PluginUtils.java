package claudiosoft.plugin.utils;

import claudiosoft.baseplugin.BasePlugin;
import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.pluginbean.BasePluginBean;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 *
 * @author claudio.tortorelli
 */
public class PluginUtils {

    public static LinkedList<BasePlugin> loadPlugins(Config config) throws CTException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalArgumentException, InvocationTargetException, IllegalAccessException {

        BasicLogger logger = BasicLogger.get();

        LinkedList<BasePlugin> pluginList = new LinkedList<>();
        final int maxPlugin = 100;
        int step = 1;
        final Class<?>[] defaultConstructor = {int.class};
        for (int iPlug = 1; iPlug < maxPlugin; iPlug++) {
            // instance classes by names            
            String pluginId = String.format("plugin_%02d", iPlug);
            String pluginName = config.get("plugins", pluginId);
            if (pluginName == null || pluginName.isEmpty()) {
                continue;
            }
            boolean enabled = Boolean.parseBoolean(config.get(pluginName, "enabled", "false"));
            if (!enabled) {
                logger.warn(String.format("plugin %s is not enabled", pluginName));
                continue;
            }

            String pluginClassName = String.format("claudiosoft.plugin.%s", pluginName);
            Class<?> clazz = Class.forName(pluginClassName);
            Constructor<?> constructor = clazz.getConstructor(defaultConstructor);
            pluginList.add((BasePlugin) constructor.newInstance(step++));
        }

        // if no enabled plugin are present, terminate
        if (pluginList.isEmpty()) {
            throw new CTException("no enabled plugin found", CTError.PLUGIN_GENERIC);
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
        return pluginList;
    }

    public static LinkedList<BasePluginBean> loadPluginBeans(Config config) throws CTException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        BasicLogger logger = BasicLogger.get();

        LinkedList<BasePluginBean> pluginBeanList = new LinkedList<>();
        final int maxPlugin = 100;
        final Class<?>[] defaultConstructor = {String.class};
        for (int iPlug = 1; iPlug < maxPlugin; iPlug++) {
            // instance classes by names            
            String pluginId = String.format("plugin_%02d", iPlug);
            String pluginName = config.get("plugins", pluginId);
            if (pluginName == null || pluginName.isEmpty()) {
                continue;
            }

            String pluginBeanClassName = String.format("claudiosoft.pluginbean.Bean%s", pluginName);
            Class<?> clazz = Class.forName(pluginBeanClassName);
            Constructor<?> constructor = clazz.getConstructor(defaultConstructor);
            pluginBeanList.add((BasePluginBean) constructor.newInstance(pluginName));
        }

        // if no enabled plugin are present, terminate
        if (pluginBeanList.isEmpty()) {
            throw new CTException("no bean plugin found", CTError.PLUGIN_GENERIC);
        }

        logger.info(String.format("%d beans loaded", pluginBeanList.size()));

        return pluginBeanList;
    }

    public static BasePlugin getPlugin(LinkedList<BasePlugin> pluginList, String pluginName) {
        String pluginClassName = String.format("claudiosoft.plugin.%s", pluginName);
        for (BasePlugin plugin : pluginList) {
            if (plugin.getClass().getName().equals(pluginClassName)) {
                return plugin;
            }
        }
        return null;
    }
}
