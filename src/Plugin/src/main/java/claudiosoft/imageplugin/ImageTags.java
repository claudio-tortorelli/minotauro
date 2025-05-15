package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.indexer.Indexer;
import claudiosoft.ollama.OAPI;
import claudiosoft.pluginbean.BeanTags;
import claudiosoft.pluginconfig.ImageTagConfig;
import claudiosoft.threads.ImageTagsThread;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageTags extends BaseImagePlugin {

    private ImageTagConfig plugConf;

    public ImageTags(int step) {
        super(step);
    }

    @Override
    public void init(Config config) throws CTException {
        super.init(config);
        OAPI.init();
        plugConf = new ImageTagConfig(config);
    }

    @Override
    public void apply(Indexer indexer) throws CTException {
        super.apply(indexer);

        ExecutorService exec = Executors.newFixedThreadPool(nThread);
        try {
            File curImage = indexer.startVisit();
            while (curImage != null) {
                ImageTagsThread thread = new ImageTagsThread(curImage, plugConf, new BeanTags(this.getClass().getSimpleName()));
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
