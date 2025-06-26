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

    protected File curFile;
    protected BasicLogger logger;

    public PluginThread(File curImage) throws CTException {
        try {
            this.curFile = curImage;
            this.logger = BasicLogger.get();
        } catch (Exception ex) {
            throw new CTException(ex);
        }
    }

    public void run() {
        if (logger.isDebug()) {
            try {
                logger.debug(String.format("processing %s", curFile.getCanonicalPath()));
            } catch (IOException ex) {
            }
        }
    }

}
