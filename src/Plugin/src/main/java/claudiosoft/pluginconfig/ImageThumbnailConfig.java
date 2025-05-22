package claudiosoft.pluginconfig;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageThumbnailConfig extends PluginConfig {

    public int maxPix;

    public ImageThumbnailConfig(Config config, String pluginName) throws CTException {
        super(config, pluginName);
        maxPix = Integer.parseInt(config.get(pluginName, "maxSizePix", "1024"));
    }

}
