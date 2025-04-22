package claudiosoft.pluginbean;

import claudiosoft.commons.CTException;
import claudiosoft.transientimage.TransientImage;

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
    public void store(TransientImage transientImage) throws CTException {
        transientImage.set(pluginName, "thumb", base64Image);
        transientImage.store();
    }

    @Override
    public void read(TransientImage transientImage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
