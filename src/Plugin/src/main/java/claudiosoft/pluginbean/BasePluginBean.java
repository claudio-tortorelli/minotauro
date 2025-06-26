package claudiosoft.pluginbean;

import claudiosoft.commons.CTException;
import claudiosoft.transientdata.TransientFile;

/**
 *
 * @author claudio.tortorelli
 */
public abstract class BasePluginBean {

    protected String pluginName;

    protected BasePluginBean(String pluginName) {
        this.pluginName = pluginName;
    }

    public abstract void store(TransientFile transientImage) throws CTException;

    public abstract void read(TransientFile transientImage);
}
