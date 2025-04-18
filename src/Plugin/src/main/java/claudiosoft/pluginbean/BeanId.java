package claudiosoft.pluginbean;

import claudiosoft.commons.CTException;
import claudiosoft.transientimage.TransientImage;

/**
 *
 * @author claudio.tortorelli
 */
public class BeanId extends BasePluginBean {

    public String hashId;

    public BeanId(String pluginName) {
        super(pluginName);
        hashId = "";
    }

    @Override
    public void store(TransientImage transientImage) throws CTException {
        transientImage.set(pluginName, "id", hashId);
        transientImage.store();
    }

    @Override
    public void read(TransientImage transientImage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
