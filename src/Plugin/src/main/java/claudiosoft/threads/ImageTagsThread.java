package claudiosoft.threads;

import claudiosoft.commons.CTException;
import claudiosoft.ollama.OAPI;
import claudiosoft.pluginbean.BeanTags;
import claudiosoft.pluginconfig.ImageTagConfig;
import claudiosoft.transientdata.TransientFile;
import claudiosoft.transientdata.TransientProvider;
import claudiosoft.utils.Failures;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageTagsThread extends PluginThread {

    private final ImageTagConfig plugConf;
    private final BeanTags data;

    public ImageTagsThread(File curImage, ImageTagConfig plugConf, BeanTags data) throws CTException {
        super(curImage);
        this.plugConf = plugConf;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            super.run();
            TransientFile transientImage = TransientProvider.getProvider().get(curFile);

            File imgToAnalyze = curFile;

            ArrayList<File> images = new ArrayList<>();
            images.add(imgToAnalyze);

            data.tagList = OAPI.generateWithImage(plugConf.prompt, images);
            if (logger.isDebug()) {
                logger.debug(data.tagList);
            }
            data.store(transientImage);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            Failures.addFailure();
        } finally {

        }
    }

}
