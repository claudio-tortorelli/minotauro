package claudiosoft.pluginbean;

import claudiosoft.commons.CTException;
import claudiosoft.transientdata.TransientFile;

/**
 *
 * @author claudio.tortorelli
 */
public class BeanImageDescription extends BasePluginBean {

    public String description;

    public BeanImageDescription(String pluginName) {
        super(pluginName);
        description = "";
    }

    @Override
    public void store(TransientFile transientImage) throws CTException {
        transientImage.set(pluginName, "desc", description);
        transientImage.store();
    }

    @Override
    public void read(TransientFile transientImage) {
        //TODO
    }

}
