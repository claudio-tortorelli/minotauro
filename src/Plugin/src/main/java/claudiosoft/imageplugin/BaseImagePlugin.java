package claudiosoft.imageplugin;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.plugin.Plugin;
import claudiosoft.transientimage.TransientImage;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author claudio.tortorelli
 */
public abstract class BaseImagePlugin implements Plugin {

    protected String pluginName;

    protected int step;
    protected File imageFile;
    protected Config config;
    protected BasicLogger logger;
    protected TransientImage transientImage;
    protected boolean failed;

    private long nanoTimer;

    public BaseImagePlugin(int step) {
        this.step = step;
    }

    public int getStep() {
        return step;
    }

    @Override
    public void init(Config config, String pluginName) throws CTException {
        if (this.config != null) {
            return; // already initialized
        }
        try {
            this.pluginName = pluginName;
            this.config = config;
            this.logger = BasicLogger.get();
            this.nanoTimer = System.nanoTime();
        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex);
        }
    }

    @Override
    public void apply(File image, TransientImage transientImage) throws CTException {
        this.failed = false;
        this.imageFile = image;
        this.transientImage = transientImage;
    }

    @Override
    public void close() throws CTException {
        long msec = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nanoTimer);
        logger.debug(String.format("%s done in %d msec", pluginName, msec));
    }

    public void traceErrorToTransientImage(String error) throws CTException {
        try {
            logger.error(String.format("error in %s", transientImage.get(ImageId.class.getSimpleName(), "id", "")));
            transientImage.set(pluginName, "error", error);
        } catch (Exception ex) {
            throw new CTException(ex);
        }
    }

    public boolean isFailed() {
        return failed;
    }
}
