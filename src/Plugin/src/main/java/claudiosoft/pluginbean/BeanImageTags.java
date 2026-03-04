package claudiosoft.pluginbean;

import claudiosoft.commons.CTException;
import claudiosoft.transientdata.TransientFile;

/**
 *
 * @author claudio.tortorelli
 */
public class BeanImageTags extends BasePluginBean {

    public String tagList;

    public BeanImageTags(String pluginName) {
        super(pluginName);
        tagList = "";
    }

    @Override
    public void store(TransientFile transientImage) throws CTException {
        transientImage.set(pluginName, "tagList", tagList);
        transientImage.store();
    }

    @Override
    public void read(TransientFile transientImage) {
        //TODO
    }

}
