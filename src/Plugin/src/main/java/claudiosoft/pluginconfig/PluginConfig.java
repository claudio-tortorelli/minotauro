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

    public PluginConfig(Config config) throws CTException {
        logger = BasicLogger.get();

    }
}
