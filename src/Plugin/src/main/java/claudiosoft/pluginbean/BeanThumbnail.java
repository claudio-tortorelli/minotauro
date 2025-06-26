package claudiosoft.pluginbean;

import claudiosoft.commons.CTException;
import claudiosoft.transientdata.TransientFile;

/**
 *
 * @author claudio.tortorelli
 */
public class BeanThumbnail extends BasePluginBean {

    public String base64Image;

    public BeanThumbnail(String pluginName) {
        super(pluginName);
        base64Image = "";
    }

    @Override
    public void store(TransientFile transientImage) throws CTException {
        transientImage.set(pluginName, "thumb", base64Image);
        transientImage.store();
    }

    @Override
    public void read(TransientFile transientImage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
