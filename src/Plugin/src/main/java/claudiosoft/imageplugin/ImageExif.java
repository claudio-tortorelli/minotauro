package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.indexer.Indexer;
import claudiosoft.pluginbean.BeanExif;
import claudiosoft.pluginconfig.ImageExifConfig;
import claudiosoft.threads.ImageExifThread;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageExif extends BaseImagePlugin {

    private ImageExifConfig plugConf;

    public ImageExif(int step) throws CTException {
        super(step);
        plugConf = new ImageExifConfig(config, this.getClass().getSimpleName());
    }

    @Override
    public void init(Config config) throws CTException {
        super.init(config);
    }

    @Override
    public void apply(Indexer indexer) throws CTException {
        super.apply(indexer);

        ExecutorService exec = Executors.newFixedThreadPool(nThread);
        try {
            File curImage = indexer.startVisit();
            while (curImage != null) {
                ImageExifThread thread = new ImageExifThread(curImage, plugConf, new BeanExif(this.getClass().getSimpleName()));
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
