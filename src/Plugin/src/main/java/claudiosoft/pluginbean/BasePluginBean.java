package claudiosoft.pluginbean;

import claudiosoft.commons.CTException;
import claudiosoft.transientimage.TransientImage;

/**
 *
 * @author claudio.tortorelli
 */
public abstract class BasePluginBean {

    protected String pluginName;

    protected BasePluginBean(String pluginName) {
        this.pluginName = pluginName;
    }

    public abstract void store(TransientImage transientImage) throws CTException;

    public abstract void read(TransientImage transientImage);

}
