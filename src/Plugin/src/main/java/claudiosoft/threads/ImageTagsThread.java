package claudiosoft.threads;

import claudiosoft.commons.CTException;
import claudiosoft.ollama.OAPI;
import claudiosoft.pluginbean.BeanImageTags;
import claudiosoft.pluginconfig.ImageTagConfig;
import claudiosoft.transientdata.TransientFile;
import claudiosoft.transientdata.TransientProvider;
import claudiosoft.utils.Failures;
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageTagsThread extends PluginThread {

    private final ImageTagConfig plugConf;
    private final BeanImageTags data;

    public ImageTagsThread(UUID uuid, File curImage, ImageTagConfig plugConf, BeanImageTags data) throws CTException {
        super(uuid, curImage);
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
                logger.debug(logThreadMessage(data.tagList));
            }
            data.store(transientImage);
        } catch (Exception ex) {
            logger.error(logThreadMessage(ex.getMessage()), ex);
            Failures.addFailure();
        } finally {

        }
    }

}
