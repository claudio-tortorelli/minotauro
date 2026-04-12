package claudiosoft.plugin;

import claudiosoft.baseplugin.BasePlugin;
import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.indexer.IndexMechanism;
import claudiosoft.pluginbean.BeanImageFileData;
import claudiosoft.pluginconfig.ImageFileDataConfig;
import claudiosoft.threads.ImageFileDataThread;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import nu.pattern.OpenCV;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageFileData extends BasePlugin {

    private ImageFileDataConfig plugConf;

    public ImageFileData(int step) {
        super(step);
    }

    @Override
    public void init(Config config) throws CTException {
        super.init(config);
        plugConf = new ImageFileDataConfig(config, this.getClass().getSimpleName());
        OpenCV.loadLocally();
    }

    @Override
    public void apply(IndexMechanism indexer) throws CTException {
        super.apply(indexer);

        ExecutorService exec = Executors.newFixedThreadPool(nThread);
        try {
            List<CompletableFuture<?>> futures = new ArrayList<>();
            File curImage = indexer.startVisit(pluginName);
            while (curImage != null) {
                ImageFileDataThread thread = new ImageFileDataThread(UUID.randomUUID(), curImage, plugConf, new BeanImageFileData(this.getClass().getSimpleName()));
                futures.add(CompletableFuture.runAsync(thread, exec));
                curImage = indexer.visitNext();
            }
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex, CTError.PLUGIN_GENERIC);
        } finally {
            exec.shutdown();
        }
    }

}
