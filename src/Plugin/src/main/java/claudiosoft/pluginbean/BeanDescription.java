package claudiosoft.pluginbean;

import claudiosoft.commons.CTException;
import claudiosoft.transientimage.TransientImage;

/**
 *
 * @author claudio.tortorelli
 */
public class BeanDescription extends BasePluginBean {

    public String description;

    public BeanDescription(String pluginName) {
        super(pluginName);
        description = "";
    }

    @Override
    public void store(TransientImage transientImage) throws CTException {
        transientImage.set(pluginName, "desc", description);
        transientImage.store();
    }

    @Override
    public void read(TransientImage transientImage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
