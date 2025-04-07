package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.transientimage.TransientImage;
import java.io.File;

/**
 *
 * @author claudio.tortorelli
 */
public interface ImagePlugin {

    public void init(Config config, String pluginName) throws CTException;

    public void apply(File image, TransientImage transientImage) throws CTException;

    public void close() throws CTException;

    public void store() throws CTException;

}
