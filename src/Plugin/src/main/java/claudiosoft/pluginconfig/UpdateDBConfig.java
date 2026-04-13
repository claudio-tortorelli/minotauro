package claudiosoft.pluginconfig;

import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.dbabel.DBabel;
import claudiosoft.plugin.utils.PluginUtils;
import claudiosoft.pluginbean.BasePluginBean;
import claudiosoft.pluginbean.BeanUpdateDB;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 *
 * @author claudio.tortorelli
 */
public class UpdateDBConfig extends PluginConfig {

    public DBabel db;
    public List<BasePluginBean> pluginBeanList;

    public UpdateDBConfig(Config config, String pluginName) throws CTException {
        super(config, pluginName);

        String dbPath = config.get("database", "dbPath");
        if (dbPath.isEmpty()) {
            throw new CTException("missing db path in config", CTError.DB_CONFIG);
        }
        File targetDb = new File(dbPath);
        if (!targetDb.exists()) {
            // restore from scratch
            File baseDb = new File("../../base_db/babel.db");
            if (!baseDb.exists()) {
                throw new CTException("missing original db file", CTError.DB_CONFIG);
            }
            try {
                Files.copy(baseDb.toPath(), targetDb.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                throw new CTException("cannot copy original db file", CTError.DB_CONFIG);
            }
        }
        db = new DBabel(dbPath);
        try {
            pluginBeanList = PluginUtils.loadPluginBeans(config);
            for (int i = 0; i < pluginBeanList.size(); i++) {
                if (pluginBeanList.get(i) instanceof BeanUpdateDB) {
                    pluginBeanList.remove(i);
                    break;
                }
            }
        } catch (Exception ex) {
            throw new CTException(ex, CTError.UNDEFINED_CLASS);
        }
    }

}
