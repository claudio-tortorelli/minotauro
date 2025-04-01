package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.transientimage.TransientImage;
import claudiosoft.transientimage.TransientImageProvider;
import claudiosoft.utils.BasicUtils;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageId extends BaseImagePlugin {

    private String algo;

    public ImageId(int step) {
        super(step);
    }

    @Override
    public void init(Config config, TransientImageProvider transientImageProvider) throws CTException {
        super.init(config, transientImageProvider);
        algo = config.get(this.getClass().getSimpleName(), "algorithm", "sha-1");
    }

    @Override
    public void apply(File image) throws CTException {
        super.apply(image);

        String hashId = "";
        try {
            if (algo.equalsIgnoreCase("sha-1")) {
                hashId = BasicUtils.bytesToHex(BasicUtils.getSHA1(image));
            } else {
                hashId = BasicUtils.bytesToHex(BasicUtils.getSHA256(image));
            }
            store(hashId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new CTException(ex.getMessage(), ex);
        }
    }

    protected void store(String result) throws NoSuchAlgorithmException, IOException, CTException {
        TransientImage transientImage = transientImageProvider.get(imageFile);
        transientImage.set(this.getClass().getSimpleName(), "id", result);
        transientImage.store();
    }

}
