package claudiosoft.threads;

import claudiosoft.commons.CTException;
import claudiosoft.pluginbean.BeanFileData;
import claudiosoft.pluginconfig.ImageFileDataConfig;
import claudiosoft.transientimage.TransientImage;
import claudiosoft.transientimage.TransientImageProvider;
import claudiosoft.utils.BasicUtils;
import java.io.File;
import java.util.Date;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageFileDataThread extends PluginThread {

    private final ImageFileDataConfig plugConf;
    private final BeanFileData data;

    public ImageFileDataThread(File curImage, ImageFileDataConfig plugConf, BeanFileData data) throws CTException {
        super(curImage);
        this.plugConf = plugConf;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            if (logger.isDebug()) {
                logger.debug(String.format("processing image %s", curImage.getCanonicalPath()));
            }
            TransientImage transientImage = TransientImageProvider.getProvider().get(curImage);

            data.originalPath = curImage.getCanonicalPath();
            data.fileName = curImage.getName();
            data.ext = BasicUtils.getExtension(curImage);
            data.lastModifiedDate = BasicUtils.dateToString(new Date(curImage.lastModified()));

            data.store(transientImage);
            done = true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {

        }
    }

}
