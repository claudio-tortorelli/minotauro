package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.pluginbean.BeanThumbnail;
import claudiosoft.transientimage.TransientImage;
import claudiosoft.utils.BasicUtils;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * Uses opencv
 *
 * @author claudio.tortorelli
 */
public class ImageThumbnail extends BaseImagePlugin {

    public ImageThumbnail(int step) {
        super(step);
    }

    @Override
    public void init(Config config, String pluginName) throws CTException {
        super.init(config, pluginName);
        OpenCV.loadShared();
    }

    /**
     *
     *
     * @param image
     * @param transientImage
     * @throws CTException
     */
    @Override
    public void apply(File image, TransientImage transientImage) throws CTException {
        super.apply(image, transientImage);

        File tmpImage = null;
        try {
            if (!image.exists()) {
                logger.error("image not found");
                failed = true;
                return;
            }

            BeanThumbnail data = new BeanThumbnail(this.getClass().getSimpleName());

            // load the image
            Mat cvImage = Imgcodecs.imread(image.getCanonicalPath());
            if (cvImage == null || cvImage.empty() || cvImage.width() == 0 || cvImage.height() == 0) {
                logger.error("unable to read the image. Look for unicode chars in the path");
                failed = true;
                return;
            }

            // evaluate the max size and get the destination sizes
            Size destSize;
            double ratio = cvImage.width() / (double) cvImage.height();
            if (ratio >= 1.0) {
                int dstWidth = Integer.min(cvImage.width(), Integer.parseInt(config.get(this.getClass().getSimpleName(), "maxSizePix", "1024")));
                int dstHeight = (int) (dstWidth * cvImage.height()) / cvImage.width();
                destSize = new Size(dstWidth, dstHeight);
            } else {
                int dstHeight = Integer.min(cvImage.height(), Integer.parseInt(config.get(this.getClass().getSimpleName(), "maxSizePix", "1024")));
                int dstWidth = (int) (dstHeight * cvImage.width()) / cvImage.height();
                destSize = new Size(dstWidth, dstHeight);
            }

            // resize the image and save to temporary file
            Imgproc.resize(cvImage, cvImage, destSize);
            tmpImage = Files.createTempFile("cvImage", "." + BasicUtils.getExtension(image.getCanonicalPath())).toFile();
            Imgcodecs.imwrite(tmpImage.getCanonicalPath(), cvImage);

            // read the image byte array and convert to base64
            byte[] imgByte = Files.readAllBytes(tmpImage.toPath());
            data.base64Image = Base64.getEncoder().encodeToString(imgByte);

            // save to transient image
            data.store(transientImage);

        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex);
        } finally {
            if (tmpImage != null) {
                tmpImage.delete();
            }
        }
    }
}
