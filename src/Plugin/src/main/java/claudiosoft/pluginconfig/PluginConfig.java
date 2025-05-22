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

    public PluginConfig(Config config, String pluginName) throws CTException {
        this.logger = BasicLogger.get();
        this.pluginName = pluginName;

    }
}
