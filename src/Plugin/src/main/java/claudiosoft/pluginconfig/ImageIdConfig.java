package claudiosoft.pluginconfig;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageIdConfig extends PluginConfig {

    public String algo;

    public ImageIdConfig(Config config, String pluginName) throws CTException {
        super(config, pluginName);
        algo = config.get(pluginName, "algorithm", "sha-1");
    }

}
