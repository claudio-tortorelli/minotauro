package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.indexer.Indexer;
import claudiosoft.pluginbean.BeanThumbnail;
import claudiosoft.pluginconfig.ImageThumbnailConfig;
import claudiosoft.threads.ImageThumbnailThread;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import nu.pattern.OpenCV;

/**
 * Uses opencv
 *
 * @author claudio.tortorelli
 */
public class ImageThumbnail extends BaseImagePlugin {

    private ImageThumbnailConfig plugConf;

    public ImageThumbnail(int step) throws CTException {
        super(step);
        plugConf = new ImageThumbnailConfig(config, this.getClass().getSimpleName());
    }

    @Override
    public void init(Config config) throws CTException {
        super.init(config);
        OpenCV.loadShared();
    }

    /**
     *
     *
     * @throws CTException
     */
    @Override
    public void apply(Indexer indexer) throws CTException {
        super.apply(indexer);

        ExecutorService exec = Executors.newFixedThreadPool(nThread);
        try {
            File curImage = indexer.startVisit();
            while (curImage != null) {
                ImageThumbnailThread thread = new ImageThumbnailThread(curImage, plugConf, new BeanThumbnail(this.getClass().getSimpleName()));
                exec.execute(thread);
                curImage = indexer.visitNext();
            }
        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex);
        } finally {
            exec.shutdown();
        }
    }
}
