package claudiosoft.threads;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTException;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author claudio.tortorelli
 */
public abstract class PluginThread implements Runnable {

    protected File curImage;
    protected BasicLogger logger;

    public PluginThread(File curImage) throws CTException {
        try {
            this.curImage = curImage;
            this.logger = BasicLogger.get();
        } catch (Exception ex) {
            throw new CTException(ex);
        }
    }

    public void run() {
        if (logger.isDebug()) {
            try {
                logger.debug(String.format("processing image %s", curImage.getCanonicalPath()));
            } catch (IOException ex) {
            }
        }
    }

}
