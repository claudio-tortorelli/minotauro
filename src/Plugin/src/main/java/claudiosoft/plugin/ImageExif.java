package claudiosoft.plugin;

import claudiosoft.baseplugin.BasePlugin;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.indexer.Indexer;
import claudiosoft.pluginbean.BeanExif;
import claudiosoft.pluginconfig.ImageExifConfig;
import claudiosoft.threads.ImageExifThread;
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
public class ImageExif extends BasePlugin {

    private ImageExifConfig plugConf;

    public ImageExif(int step) throws CTException {
        super(step);
    }

    @Override
    public void init(Config config) throws CTException {
        super.init(config);
        plugConf = new ImageExifConfig(config, this.getClass().getSimpleName());
    }

    @Override
    public void apply(Indexer indexer) throws CTException {
        super.apply(indexer);

        ExecutorService exec = Executors.newFixedThreadPool(nThread);
        try {
            List<CompletableFuture<?>> futures = new ArrayList<>();
            File curImage = indexer.startVisit(pluginName);
            while (curImage != null) {
                ImageExifThread thread = new ImageExifThread(curImage, plugConf, new BeanExif(this.getClass().getSimpleName()));
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
