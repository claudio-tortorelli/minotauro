package claudiosoft.threads;

import claudiosoft.commons.CTException;
import claudiosoft.ollama.OAPI;
import claudiosoft.pluginbean.BeanImageDescription;
import claudiosoft.pluginconfig.ImageDescriptionConfig;
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
public class ImageDescriptionThread extends PluginThread {

    private final ImageDescriptionConfig plugConf;
    private final BeanImageDescription data;

    public ImageDescriptionThread(UUID uuid, File curImage, ImageDescriptionConfig plugConf, BeanImageDescription data) throws CTException {
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

            data.description = OAPI.generateWithImage(plugConf.prompt, images);
            if (logger.isDebug()) {
                logger.debug(logThreadMessage(data.description));
            }
            data.store(transientImage);
        } catch (Exception ex) {
            logger.error(logThreadMessage(ex.getMessage()), ex);
            Failures.addFailure();
        } finally {

        }
    }

}
