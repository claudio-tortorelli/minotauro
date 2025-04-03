package claudiosoft.imageplugin;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.transientimage.TransientImageProvider;
import java.io.File;

/**
 *
 * @author claudio.tortorelli
 */
public abstract class BaseImagePlugin implements ImagePlugin {

    protected int step;
    protected File imageFile;
    protected Config config;
    protected BasicLogger logger;
    protected TransientImageProvider transientImageProvider;

    public BaseImagePlugin(int step) {
        this.step = step;
    }

    public int getStep() {
        return step;
    }

    @Override
    public void init(Config config, TransientImageProvider transientImageProvider) throws CTException {
        if (this.config != null) {
            return; // already initialized
        }
        try {
            this.config = config;
            this.transientImageProvider = transientImageProvider;
            this.logger = BasicLogger.get();
        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex);
        }
    }

    @Override
    public void apply(File image) throws CTException {
        this.imageFile = image;
    }
}
