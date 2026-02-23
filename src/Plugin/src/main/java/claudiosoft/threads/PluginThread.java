package claudiosoft.threads;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 *
 * @author claudio.tortorelli
 */
public abstract class PluginThread implements Runnable {

    protected File curFile;
    protected BasicLogger logger;
    protected UUID uuid;

    protected PluginThread(UUID uuid, File curImage) throws CTException {
        try {
            this.uuid = uuid;
            this.curFile = curImage;
            this.logger = BasicLogger.get();
        } catch (Exception ex) {
            throw new CTException(ex, CTError.PLUGIN_THREAD);
        }
    }

    public void run() {
        if (logger.isDebug()) {
            try {
                logger.debug(logThreadMessage(String.format("processing %s", curFile.getCanonicalPath())));
            } catch (IOException ex) {
            }
        }
    }

    protected String logThreadMessage(String msg) {
        return String.format("- %s - %s", uuid, msg);
    }

}
