package claudiosoft.pluginbean;

import claudiosoft.commons.CTException;
import claudiosoft.transientdata.TransientFile;

/**
 *
 * @author claudio.tortorelli
 */
public class BeanImageThumbnail extends BasePluginBean {

    public String base64Image;

    public BeanImageThumbnail(String pluginName) {
        super(pluginName);
        base64Image = "";
    }

    @Override
    public void store(TransientFile transientImage) throws CTException {
        transientImage.set(pluginName, "thumb", base64Image);
        transientImage.store();
    }

    @Override
    public void read(TransientFile transientImage) throws CTException {
        base64Image = transientImage.get(pluginName, "thumb", base64Image);
    }

}
