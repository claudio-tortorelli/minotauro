package claudiosoft.pluginbean;

import claudiosoft.commons.CTException;
import claudiosoft.transientdata.TransientFile;

/**
 *
 * @author claudio.tortorelli
 */
public class BeanFileData extends BasePluginBean {

    public String originalPath;
    public String fileName;
    public String lastModifiedDate;
    public String ext;

    public BeanFileData(String pluginName) {
        super(pluginName);
        originalPath = "";
        fileName = "";
        ext = "";
        lastModifiedDate = null;
    }

    @Override
    public void store(TransientFile transientImage) throws CTException {
        transientImage.set(pluginName, "originalPath", originalPath);
        transientImage.set(pluginName, "fileName", fileName);
        transientImage.set(pluginName, "ext", ext);
        transientImage.set(pluginName, "lastModifiedDate", lastModifiedDate);
        transientImage.store();
    }

    @Override
    public void read(TransientFile transientImage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
