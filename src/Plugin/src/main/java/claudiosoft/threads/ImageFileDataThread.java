package claudiosoft.threads;

import claudiosoft.commons.CTException;
import claudiosoft.pluginbean.BeanFileData;
import claudiosoft.pluginconfig.ImageFileDataConfig;
import claudiosoft.transientdata.TransientFile;
import claudiosoft.transientdata.TransientProvider;
import claudiosoft.utils.BasicUtils;
import claudiosoft.utils.Failures;
import java.io.File;
import java.util.Date;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageFileDataThread extends PluginThread {

    private final ImageFileDataConfig plugConf;
    private final BeanFileData data;

    public ImageFileDataThread(File curImage, ImageFileDataConfig plugConf, BeanFileData data) throws CTException {
        super(curImage);
        this.plugConf = plugConf;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            super.run();
            TransientFile transientImage = TransientProvider.getProvider().get(curFile);

            data.originalPath = curFile.getCanonicalPath();
            data.fileName = curFile.getName();
            data.ext = BasicUtils.getExtension(curFile);
            data.lastModifiedDate = BasicUtils.dateToString(new Date(curFile.lastModified()));

            data.store(transientImage);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            Failures.addFailure();
        } finally {

        }
    }

}
