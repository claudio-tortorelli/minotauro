package claudiosoft.imageplugin;

import claudiosoft.imageplugin.BaseImagePlugin;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageId extends BaseImagePlugin {

    public ImageId(int step) {
        super(step);
    }

    @Override
    public void init(Config config) throws CTException {
        super.init(config);
    }

    @Override
    public void apply() throws CTException {
        // store();
    }

}
