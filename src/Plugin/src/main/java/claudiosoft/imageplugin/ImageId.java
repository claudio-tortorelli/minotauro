package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.transientimage.TransientImage;
import claudiosoft.utils.BasicUtils;
import java.io.File;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageId extends BaseImagePlugin {

    private String algo;
    private String hashId;

    public ImageId(int step) {
        super(step);
    }

    @Override
    public void init(Config config, String pluginName) throws CTException {
        super.init(config, pluginName);
        algo = config.get(this.getClass().getSimpleName(), "algorithm", "sha-1");
    }

    @Override
    public void apply(File image, TransientImage transientImage) throws CTException {
        super.apply(image, transientImage);

        try {
            if (algo.equalsIgnoreCase("sha-1")) {
                hashId = BasicUtils.bytesToHex(BasicUtils.getSHA1(image));
            } else {
                hashId = BasicUtils.bytesToHex(BasicUtils.getSHA256(image));
            }
            store();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new CTException(ex.getMessage(), ex);
        }
    }

    @Override
    public void store() throws CTException {
        transientImage.set(this.getClass().getSimpleName(), "id", hashId);
        transientImage.store();
    }

}
