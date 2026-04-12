package claudiosoft.pluginconfig;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;

/**
 *
 * @author claudio.tortorelli
 */
public abstract class PluginConfig {

    protected BasicLogger logger;
    protected String pluginName;
    protected Config config;

    public PluginConfig(Config config, String pluginName) throws CTException {
        this.config = config;
        this.logger = BasicLogger.get();
        this.pluginName = pluginName;
    }

    public Config getGlobalConfig() {
        return config;
    }

    public String getPluginName() {
        return pluginName;
    }
}
