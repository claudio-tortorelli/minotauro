package claudiosoft.threads;

import claudiosoft.commons.CTException;
import claudiosoft.pluginbean.BeanId;
import claudiosoft.pluginconfig.ImageIdConfig;
import claudiosoft.transientdata.TransientFile;
import claudiosoft.transientdata.TransientProvider;
import claudiosoft.utils.BasicUtils;
import claudiosoft.utils.Failures;
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
            TransientFile transientImage = TransientProvider.getProvider().get(curFile);

            if (plugConf.algo.equalsIgnoreCase("sha-1")) {
                data.hashId = BasicUtils.bytesToHex(BasicUtils.getSHA1(curFile));
            } else {
                data.hashId = BasicUtils.bytesToHex(BasicUtils.getSHA256(curFile));
            }
            data.store(transientImage);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            Failures.addFailure();
        } finally {

        }
    }

}
