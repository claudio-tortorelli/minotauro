package claudiosoft.threads;

import claudiosoft.commons.CTException;
import claudiosoft.ollama.OAPI;
import claudiosoft.pluginbean.BeanTags;
import claudiosoft.transientimage.TransientImage;
import claudiosoft.transientimage.TransientImageProvider;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageTagsThread extends PluginThread {

    private String prompt;

    public ImageTagsThread(File curImage, String prompt) throws CTException {
        super(curImage);
        this.prompt = prompt;
    }

    @Override
    public void run() {
        try {
            if (logger.isDebug()) {
                logger.debug(String.format("processing image %s", curImage.getCanonicalPath()));
            }
            TransientImage transientImage = TransientImageProvider.getProvider().get(curImage);

            BeanTags data = new BeanTags(this.getClass().getSimpleName());

            File imgToAnalyze = curImage;

            ArrayList<File> images = new ArrayList<>();
            images.add(imgToAnalyze);

            data.tagList = OAPI.generateWithImage(prompt, images);
            if (logger.isDebug()) {
                logger.debug(data.tagList);
            }
            data.store(transientImage);
            done = true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {

        }
    }

}
