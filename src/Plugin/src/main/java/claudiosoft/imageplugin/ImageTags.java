package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.indexer.Indexer;
import claudiosoft.ollama.OAPI;
import claudiosoft.threads.ImageTagsThread;
import io.github.ollama4j.models.response.Model;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageTags extends BaseImagePlugin {
    
    private String prompt;
    
    public ImageTags(int step) {
        super(step);
    }
    
    @Override
    public void init(Config config) throws CTException {
        super.init(config);
        OAPI.init();
        
        Model curModel = null;
        try {
            String modelName = config.get(this.getClass().getSimpleName(), "modelName", "llava");
            logger.info("looking for model " + modelName);
            
            logger.info("list ollama installed models");
            for (Model model : OAPI.getModelList()) {
                logger.info("- " + model.getModelName());
                if (model.getModelName().equalsIgnoreCase(modelName)) {
                    curModel = model;
                }
            }
            OAPI.setModel(curModel);
        } catch (Exception ex) {
            logger.error("unable to select current model", ex);
        }
        
        int timeout = Integer.parseInt(config.get(this.getClass().getSimpleName(), "timeout", "120"));
        OAPI.setTimeout(timeout);
        
        prompt = config.get(this.getClass().getSimpleName(), "prompt", "create %d tags for this image, in a single line with tags separated by comma");
        if (!prompt.contains("%d")) {
            throw new CTException("invalid prompt");
        }
        int nTags = Integer.parseInt(config.get(this.getClass().getSimpleName(), "tagNumber", "5"));
        prompt = String.format(prompt, nTags);
        logger.info(String.format("prompt: %s", prompt));
        
        multithread = Boolean.parseBoolean(config.get(this.getClass().getSimpleName(), "multithread", "true"));
        logger.info(String.format("multithread enabled: %s", multithread));
    }
    
    @Override
    public void apply(Indexer indexer) throws CTException {
        super.apply(indexer);
        
        int nPluginThread = Integer.parseInt(config.get("threads", "plugin_threads", "1"));
        int nThread = Integer.min(Runtime.getRuntime().availableProcessors() - 1, nPluginThread);
        
        ExecutorService exec = Executors.newFixedThreadPool(nThread);
        try {
            File curImage = indexer.startVisit();
            while (curImage != null) {
                ImageTagsThread thread = new ImageTagsThread(curImage, prompt);
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
