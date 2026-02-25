package claudiosoft.plugin;

import claudiosoft.baseplugin.BasePlugin;
import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.indexer.IndexMechanism;
import claudiosoft.pluginbean.BeanUpdateDB;
import claudiosoft.pluginconfig.UpdateDBConfig;
import claudiosoft.threads.UpdateDBThread;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author claudio.tortorelli
 */
public class UpdateDB extends BasePlugin {

    private UpdateDBConfig plugConf;

    public UpdateDB(int step) {
        super(step);
    }

    @Override
    public void init(Config config) throws CTException {
        super.init(config);
        plugConf = new UpdateDBConfig(config, this.getClass().getSimpleName());
    }

    @Override
    public void apply(IndexMechanism indexer) throws CTException {
        super.apply(indexer);

        ExecutorService exec = Executors.newFixedThreadPool(nThread);
        try {
            List<CompletableFuture<?>> futures = new ArrayList<>();
            File curImage = indexer.startVisit(pluginName);
            while (curImage != null) {
                UpdateDBThread thread = new UpdateDBThread(UUID.randomUUID(), curImage, plugConf, new BeanUpdateDB(this.getClass().getSimpleName()));
                futures.add(CompletableFuture.runAsync(thread, exec));
            }
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex, CTError.PLUGIN_GENERIC);
        } finally {
            exec.shutdown();
        }
    }
}
