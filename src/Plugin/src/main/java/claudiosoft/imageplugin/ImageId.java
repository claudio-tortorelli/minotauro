package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.indexer.Indexer;
import claudiosoft.pluginbean.BeanId;
import claudiosoft.utils.BasicUtils;

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
    public void init(Config config) throws CTException {
        super.init(config);
        algo = config.get(this.getClass().getSimpleName(), "algorithm", "sha-1");
    }

    @Override
    public void apply(Indexer indexer) throws CTException {
        super.apply(indexer);

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
