package claudiosoft.imageplugin;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.indexer.Indexer;
import claudiosoft.plugin.Plugin;
import claudiosoft.transientimage.TransientImage;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author claudio.tortorelli
 */
public abstract class BaseImagePlugin implements Plugin {

    protected String pluginName;

    protected int step;
    protected Config config;
    protected BasicLogger logger;
    protected Indexer indexer;
    protected boolean failed;
    protected boolean multithread;

    private long nanoTimer;

    public BaseImagePlugin(int step) {
        this.step = step;
    }

    public int getStep() {
        return step;
    }

    @Override
    public void init(Config config) throws CTException {
        if (this.config != null) {
            return; // already initialized
        }
        try {
            this.config = config;
            this.pluginName = getClass().getSimpleName();
            this.logger = BasicLogger.get();
            this.nanoTimer = System.nanoTime();
            this.multithread = false;

            logger.info(String.format("-- start plugin %s --", pluginName));
        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex);
        }
    }

    @Override
    public void apply(Indexer indexer) throws CTException {
        this.failed = false;
        this.indexer = indexer;
    }

    @Override
    public void close() throws CTException {
        long msec = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nanoTimer);
        logger.debug(String.format("%s terminated in %d msec", pluginName, msec));
    }

    public void traceErrorToTransientImage(String error, TransientImage transientImage) throws CTException {
        try {
            logger.error(String.format("error in %s", transientImage.get(ImageId.class.getSimpleName(), "id", "")));
            logger.error(error);
            transientImage.set(pluginName, "error", error);
        } catch (Exception ex) {
            throw new CTException(ex);
        }
    }

    public boolean isFailed() {
        return failed;
    }
}
