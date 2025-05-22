package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.indexer.Indexer;
import claudiosoft.pluginbean.BeanFileData;
import claudiosoft.pluginconfig.ImageFileDataConfig;
import claudiosoft.threads.ImageFileDataThread;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageFileData extends BaseImagePlugin {

    private ImageFileDataConfig plugConf;

    public ImageFileData(int step) {
        super(step);
    }

    @Override
    public void init(Config config) throws CTException {
        super.init(config);
        plugConf = new ImageFileDataConfig(config, this.getClass().getSimpleName());
    }

    @Override
    public void apply(Indexer indexer) throws CTException {
        super.apply(indexer);

        ExecutorService exec = Executors.newFixedThreadPool(nThread);
        try {
            File curImage = indexer.startVisit();
            while (curImage != null) {
                ImageFileDataThread thread = new ImageFileDataThread(curImage, plugConf, new BeanFileData(this.getClass().getSimpleName()));
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
