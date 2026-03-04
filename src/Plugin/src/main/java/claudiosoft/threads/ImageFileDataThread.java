package claudiosoft.threads;

import claudiosoft.commons.CTException;
import claudiosoft.pluginbean.BeanImageFileData;
import claudiosoft.pluginconfig.ImageFileDataConfig;
import claudiosoft.transientdata.TransientFile;
import claudiosoft.transientdata.TransientProvider;
import claudiosoft.utils.BasicUtils;
import claudiosoft.utils.Failures;
import java.io.File;
import java.util.Date;
import java.util.UUID;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageFileDataThread extends PluginThread {

    private final ImageFileDataConfig plugConf;
    private final BeanImageFileData data;

    public ImageFileDataThread(UUID uuid, File curImage, ImageFileDataConfig plugConf, BeanImageFileData data) throws CTException {
        super(uuid, curImage);
        this.plugConf = plugConf;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            super.run();
            TransientFile transientImage = TransientProvider.getProvider().get(curFile);

            data.folder = curFile.getParentFile().getCanonicalPath().replace("\\", "/").toLowerCase();
            data.fileName = curFile.getName();
            data.ext = BasicUtils.getExtension(curFile);
            data.lastModifiedDate = BasicUtils.dateToString(new Date(curFile.lastModified()));
            data.fileSize = String.format("%d", curFile.length());

            if (plugConf.getImageSizePix) {
                Mat cvImage = Imgcodecs.imread(curFile.getCanonicalPath());
                if (cvImage == null || cvImage.empty() || cvImage.width() == 0 || cvImage.height() == 0) {
                    logger.error(logThreadMessage("unable to read the image. Look for unicode chars in the path"));
                    return;
                }
                data.imgWidthPix = String.format("%s", cvImage.width());
                data.imgHeightPix = String.format("%s", cvImage.height());
            }

            data.store(transientImage);
        } catch (Exception ex) {
            logger.error(logThreadMessage(ex.getMessage()), ex);
            Failures.addFailure();
        } finally {

        }
    }

}
