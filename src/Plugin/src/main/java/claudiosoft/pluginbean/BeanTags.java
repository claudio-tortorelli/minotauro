package claudiosoft.pluginbean;

import claudiosoft.commons.CTException;
import claudiosoft.transientimage.TransientImage;

/**
 *
 * @author claudio.tortorelli
 */
public class BeanTags extends BasePluginBean {

    public String tagList;

    public BeanTags(String pluginName) {
        super(pluginName);
        tagList = "";
    }

    @Override
    public void store(TransientImage transientImage) throws CTException {
        transientImage.set(pluginName, "tagList", tagList);
        transientImage.store();
    }

    @Override
    public void read(TransientImage transientImage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
