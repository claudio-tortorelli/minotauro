package claudiosoft.pluginconfig;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageExifConfig extends PluginConfig {

    public final boolean SHOW_EXIF_MODE = false;

    public ImageExifConfig(Config config, String pluginName) throws CTException {
        super(config, pluginName);
    }

}
