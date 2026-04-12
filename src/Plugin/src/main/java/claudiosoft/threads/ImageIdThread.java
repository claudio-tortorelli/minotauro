package claudiosoft.threads;

import claudiosoft.commons.CTException;
import claudiosoft.pluginbean.BeanImageId;
import claudiosoft.pluginconfig.ImageIdConfig;
import claudiosoft.transientdata.TransientFile;
import claudiosoft.transientdata.TransientProvider;
import claudiosoft.utils.BasicUtils;
import claudiosoft.utils.Failures;
import java.io.File;
import java.util.UUID;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageIdThread extends PluginThread {

    private final ImageIdConfig plugConf;
    private final BeanImageId data;

    public ImageIdThread(UUID uuid, File curImage, ImageIdConfig plugConf, BeanImageId data) throws CTException {
        super(uuid, curImage);
        this.plugConf = plugConf;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            super.run();
            TransientFile transientImage = TransientProvider.getProvider().get(curFile);

            if (plugConf.algo.equalsIgnoreCase("sha-1")) {
                data.hashId = BasicUtils.bytesToHex(BasicUtils.getSHA1(curFile));
            } else {
                data.hashId = BasicUtils.bytesToHex(BasicUtils.getSHA256(curFile));
            }
            data.store(transientImage);
        } catch (Exception ex) {
            logger.error(logThreadMessage(ex.getMessage()), ex);
            Failures.addFailure();
        } finally {

        }
    }

}
