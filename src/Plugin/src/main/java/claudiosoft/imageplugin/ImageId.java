package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.pluginbean.BeanId;
import claudiosoft.transientimage.TransientImage;
import claudiosoft.utils.BasicUtils;
import java.io.File;

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
    public void init(Config config, String pluginName) throws CTException {
        super.init(config, pluginName);
        algo = config.get(this.getClass().getSimpleName(), "algorithm", "sha-1");
    }

    @Override
    public void apply(File image, TransientImage transientImage) throws CTException {
        super.apply(image, transientImage);

        try {
            BeanId data = new BeanId(this.getClass().getSimpleName());
            if (algo.equalsIgnoreCase("sha-1")) {
                data.hashId = BasicUtils.bytesToHex(BasicUtils.getSHA1(image));
            } else {
                data.hashId = BasicUtils.bytesToHex(BasicUtils.getSHA256(image));
            }
            data.store(transientImage);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new CTException(ex.getMessage(), ex);
        }
    }

}
