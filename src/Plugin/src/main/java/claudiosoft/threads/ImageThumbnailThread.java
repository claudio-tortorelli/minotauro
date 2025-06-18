package claudiosoft.threads;

import claudiosoft.commons.CTException;
import claudiosoft.pluginbean.BeanThumbnail;
import claudiosoft.pluginconfig.ImageThumbnailConfig;
import claudiosoft.transientimage.TransientImage;
import claudiosoft.transientimage.TransientImageProvider;
import claudiosoft.utils.BasicUtils;
import claudiosoft.utils.Failures;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageThumbnailThread extends PluginThread {

    private final ImageThumbnailConfig plugConf;
    private final BeanThumbnail data;

    public ImageThumbnailThread(File curImage, ImageThumbnailConfig plugConf, BeanThumbnail data) throws CTException {
        super(curImage);
        this.plugConf = plugConf;
        this.data = data;
    }

    @Override
    public void run() {

        File tmpImage = null;
        try {
            super.run();
            TransientImage transientImage = TransientImageProvider.getProvider().get(curImage);

            if (!curImage.exists()) {
                logger.error("image not found");
                return;
            }

            // load the image
            Mat cvImage = Imgcodecs.imread(curImage.getCanonicalPath());
            if (cvImage == null || cvImage.empty() || cvImage.width() == 0 || cvImage.height() == 0) {
                logger.error("unable to read the image. Look for unicode chars in the path");
                return;
            }

            // evaluate the max size and get the destination sizes
            Size destSize;
            double ratio = cvImage.width() / (double) cvImage.height();
            if (ratio >= 1.0) {
                int dstWidth = Integer.min(cvImage.width(), plugConf.maxPix);
                int dstHeight = (int) (dstWidth * cvImage.height()) / cvImage.width();
                destSize = new Size(dstWidth, dstHeight);
            } else {
                int dstHeight = Integer.min(cvImage.height(), plugConf.maxPix);
                int dstWidth = (int) (dstHeight * cvImage.width()) / cvImage.height();
                destSize = new Size(dstWidth, dstHeight);
            }

            // resize the image and save to temporary file
            Imgproc.resize(cvImage, cvImage, destSize);
            tmpImage = Files.createTempFile("cvImage", "." + BasicUtils.getExtension(curImage.getCanonicalPath())).toFile();
            Imgcodecs.imwrite(tmpImage.getCanonicalPath(), cvImage);

            // read the image byte array and convert to base64
            byte[] imgByte = Files.readAllBytes(tmpImage.toPath());
            data.base64Image = Base64.getEncoder().encodeToString(imgByte);

            data.store(transientImage);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            Failures.addFailure();
        } finally {
            if (tmpImage != null) {
                tmpImage.delete();
            }
        }
    }

}
