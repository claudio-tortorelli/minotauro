package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.indexer.Indexer;
import claudiosoft.ollama.OAPI;
import claudiosoft.pluginbean.BeanDescription;
import claudiosoft.pluginconfig.ImageDescriptionConfig;
import claudiosoft.threads.ImageDescriptionThread;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageDescription extends BaseImagePlugin {

    private ImageDescriptionConfig plugConf;

    public ImageDescription(int step) {
        super(step);
    }

    @Override
    public void init(Config config) throws CTException {
        super.init(config);
        OAPI.init();
        plugConf = new ImageDescriptionConfig(config, this.getClass().getSimpleName());
    }

    @Override
    public void apply(Indexer indexer) throws CTException {
        super.apply(indexer);

        ExecutorService exec = Executors.newFixedThreadPool(nThread);
        try {
            List<CompletableFuture<?>> futures = new ArrayList<>();
            File curImage = indexer.startVisit(pluginName);
            while (curImage != null) {
                ImageDescriptionThread thread = new ImageDescriptionThread(curImage, plugConf, new BeanDescription(this.getClass().getSimpleName()));
                futures.add(CompletableFuture.runAsync(thread, exec));
                curImage = indexer.visitNext();
            }
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex);
        } finally {
            exec.shutdown();
        }
    }

}
