package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.transientimage.TransientImage;
import java.io.File;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageTags extends BaseImagePlugin {

    public ImageTags(int step) {
        super(step);
    }

    @Override
    public void init(Config config, String pluginName) throws CTException {
        super.init(config, pluginName);
    }

    @Override
    public void apply(File image, TransientImage transientImage) throws CTException {
        super.apply(image, transientImage);
        try {
            store();
        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex);
        }
    }

    @Override
    public void store() throws CTException {

    }
}
