package claudiosoft.threads;

import claudiosoft.commons.CTException;
import claudiosoft.dbabel.DBabel;
import claudiosoft.plugin.utils.PluginUtils;
import claudiosoft.pluginbean.BasePluginBean;
import claudiosoft.pluginbean.BeanImageId;
import claudiosoft.pluginbean.BeanUpdateDB;
import claudiosoft.pluginconfig.UpdateDBConfig;
import claudiosoft.transientdata.TransientFile;
import claudiosoft.transientdata.TransientProvider;
import claudiosoft.utils.Failures;
import java.io.File;
import java.util.LinkedList;
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
            LinkedList<BasePluginBean> pluginBeanList = PluginUtils.loadPluginBeans(plugConf.getGlobalConfig());
            boolean imageInDb = false;
            for (BasePluginBean pluginBean : pluginBeanList) {
                pluginBean.read(transientImage);
                if (pluginBean instanceof BeanImageId) {

                }
            }

        } catch (Exception ex) {
            logger.error(logThreadMessage(ex.getMessage()), ex);
            Failures.addFailure();
        } finally {

        }
    }

}
