package claudiosoft.pluginconfig;

import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.dbabel.DBabel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 *
 * @author claudio.tortorelli
 */
public class UpdateDBConfig extends PluginConfig {

    public DBabel db;

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
    }

}
