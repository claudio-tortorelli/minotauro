package claudiosoft.threads;

import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;
import claudiosoft.dbabel.Condition;
import claudiosoft.dbabel.DBabel;
import claudiosoft.dbabel.Table;
import claudiosoft.dbabel.TableData;
import claudiosoft.plugin.utils.PluginUtils;
import claudiosoft.pluginbean.BeanUpdateDB;
import claudiosoft.pluginconfig.UpdateDBConfig;
import claudiosoft.transientdata.TransientFile;
import claudiosoft.transientdata.TransientProvider;
import claudiosoft.utils.Failures;
import java.io.File;
import java.util.UUID;

/**
 *
 * @author claudio.tortorelli
 */
public class UpdateDBThread extends PluginThread {

    private final UpdateDBConfig plugConf;
    private final BeanUpdateDB data;

    public UpdateDBThread(UUID uuid, File curImage, UpdateDBConfig plugConf, BeanUpdateDB data) throws CTException {
        super(uuid, curImage);
        this.plugConf = plugConf;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            super.run();

            TransientFile transientImage = TransientProvider.getProvider().get(curFile);

            DBabel db = plugConf.db;
            if (!db.isOpen()) {
                throw new CTException("db not available", CTError.DB_OPEN);
            }
            String imageId = PluginUtils.getBeansByTransientData(plugConf.pluginBeanList, transientImage);
            if (logger.isDebug()) {
                logger.debug("read beans of image %s".formatted(imageId));
            }
            Condition conditionSel = new Condition("id", "=", imageId);
            TableData td = db.select(Table.PICTURE, conditionSel);
            if (td.getRows() > 0) {
                if (logger.isDebug()) {
                    logger.debug("image %s is already into DB".formatted(imageId));
                }
            } else {

            }

        } catch (Exception ex) {
            logger.error(logThreadMessage(ex.getMessage()), ex);
            Failures.addFailure();
        } finally {

        }
    }

}
