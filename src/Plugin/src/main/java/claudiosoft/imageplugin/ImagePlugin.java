package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.transientimage.TransientImageProvider;
import java.io.File;

/**
 *
 * @author claudio.tortorelli
 */
public interface ImagePlugin {

    public void init(Config config, TransientImageProvider transientImageProvider) throws CTException;

    public void apply(File image) throws CTException;

}
