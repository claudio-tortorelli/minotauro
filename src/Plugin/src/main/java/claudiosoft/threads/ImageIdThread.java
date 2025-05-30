package claudiosoft.threads;

import claudiosoft.commons.CTException;
import claudiosoft.pluginbean.BeanId;
import claudiosoft.pluginconfig.ImageIdConfig;
import claudiosoft.transientimage.TransientImage;
import claudiosoft.transientimage.TransientImageProvider;
import claudiosoft.utils.BasicUtils;
import java.io.File;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageIdThread extends PluginThread {

    private final ImageIdConfig plugConf;
    private final BeanId data;

    public ImageIdThread(File curImage, ImageIdConfig plugConf, BeanId data) throws CTException {
        super(curImage);
        this.plugConf = plugConf;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            super.run();
            TransientImage transientImage = TransientImageProvider.getProvider().get(curImage);

            if (plugConf.algo.equalsIgnoreCase("sha-1")) {
                data.hashId = BasicUtils.bytesToHex(BasicUtils.getSHA1(curImage));
            } else {
                data.hashId = BasicUtils.bytesToHex(BasicUtils.getSHA256(curImage));
            }
            data.store(transientImage);
            done = true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {

        }
    }

}
