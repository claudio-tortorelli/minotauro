package claudiosoft.pluginbean;

import claudiosoft.commons.CTException;
import claudiosoft.transientdata.TransientFile;

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
    public void store(TransientFile transientImage) throws CTException {
        transientImage.set(pluginName, "id", hashId);
        transientImage.store();
    }

    @Override
    public void read(TransientFile transientImage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
