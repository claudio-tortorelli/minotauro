package claudiosoft.plugin;

import claudiosoft.baseplugin.BasePlugin;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.indexer.Indexer;
import claudiosoft.pluginbean.BeanFolderParseName;
import claudiosoft.pluginconfig.FolderParseNameConfig;
import claudiosoft.threads.FolderParseNameThread;
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
public class FolderParseName extends BasePlugin {

    private FolderParseNameConfig plugConf;

    public FolderParseName(int step) {
        super(step);
    }

    @Override
    public void init(Config config) throws CTException {
        super.init(config);

        /**
         * TODO adesso deve essere implementato il multi pattern per parsare i
         * casi diversi dallo standard
         */
        plugConf = new FolderParseNameConfig(config, this.getClass().getSimpleName());
    }

    @Override
    public void apply(Indexer indexer) throws CTException {
        super.apply(indexer);

        ExecutorService exec = Executors.newFixedThreadPool(nThread);
        try {
            List<CompletableFuture<?>> futures = new ArrayList<>();
            List<String> folders = indexer.getFolders();
            for (String folder : folders) {
                FolderParseNameThread thread = new FolderParseNameThread(new File(folder), plugConf, new BeanFolderParseName(this.getClass().getSimpleName()));
                futures.add(CompletableFuture.runAsync(thread, exec));
            }
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex);
        } finally {
            exec.shutdown();
        }
    }

}
