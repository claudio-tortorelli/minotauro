package claudiosoft.pluginconfig;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageFileDataConfig extends PluginConfig {

    public final boolean getImageSizePix;

    public ImageFileDataConfig(Config config, String pluginName) throws CTException {
        super(config, pluginName);
        getImageSizePix = config.get(pluginName, "getImageSizePix", "true").equalsIgnoreCase("true");
    }

}
