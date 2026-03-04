package claudiosoft.pluginbean;

import claudiosoft.commons.CTException;
import claudiosoft.transientdata.TransientFile;

/**
 *
 * @author claudio.tortorelli
 */
public class BeanImageId extends BasePluginBean {

    public String hashId;

    public BeanImageId(String pluginName) {
        super(pluginName);
        hashId = "";
    }

    @Override
    public void store(TransientFile transientImage) throws CTException {
        transientImage.set(pluginName, "id", hashId);
        transientImage.store();
    }

    @Override
    public void read(TransientFile transientImage) {
        //TODO
    }

}
