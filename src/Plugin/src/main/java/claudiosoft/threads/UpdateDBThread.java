package claudiosoft.threads;

import claudiosoft.commons.CTException;
import claudiosoft.pluginbean.BeanUpdateDB;
import claudiosoft.pluginconfig.UpdateDBConfig;
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

    }

}
