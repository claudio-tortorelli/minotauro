package claudiosoft.threads;

import claudiosoft.commons.CTException;
import claudiosoft.ollama.OAPI;
import claudiosoft.pluginbean.BeanDescription;
import claudiosoft.pluginconfig.ImageDescriptionConfig;
import claudiosoft.transientdata.TransientFile;
import claudiosoft.transientdata.TransientProvider;
import claudiosoft.utils.Failures;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageDescriptionThread extends PluginThread {

    private final ImageDescriptionConfig plugConf;
    private final BeanDescription data;

    public ImageDescriptionThread(File curImage, ImageDescriptionConfig plugConf, BeanDescription data) throws CTException {
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

            data.description = OAPI.generateWithImage(plugConf.prompt, images);
            if (logger.isDebug()) {
                logger.debug(data.description);
            }
            data.store(transientImage);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            Failures.addFailure();
        } finally {

        }
    }

}
