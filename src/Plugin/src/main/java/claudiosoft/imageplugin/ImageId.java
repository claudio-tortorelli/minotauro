package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.indexer.Indexer;
import claudiosoft.pluginbean.BeanId;
import claudiosoft.pluginconfig.ImageIdConfig;
import claudiosoft.threads.ImageIdThread;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageId extends BaseImagePlugin {

    private ImageIdConfig plugConf;

    public ImageId(int step) {
        super(step);
    }

    @Override
    public void init(Config config) throws CTException {
        super.init(config);
        plugConf = new ImageIdConfig(config, this.getClass().getSimpleName());
    }

    @Override
    public void apply(Indexer indexer) throws CTException {
        super.apply(indexer);

        ExecutorService exec = Executors.newFixedThreadPool(nThread);
        try {
            File curImage = indexer.startVisit();
            while (curImage != null) {
                ImageIdThread thread = new ImageIdThread(curImage, plugConf, new BeanId(this.getClass().getSimpleName()));
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
