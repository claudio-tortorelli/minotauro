package claudiosoft.pluginbean;

import claudiosoft.commons.CTException;
import claudiosoft.transientdata.TransientFile;

/**
 *
 * @author claudio.tortorelli
 */
public class BeanFileData extends BasePluginBean {

    public String folder;
    public String fileName;
    public String lastModifiedDate;
    public String ext;
    public String fileSize;
    public String imgWidthPix;
    public String imgHeightPix;

    public BeanFileData(String pluginName) {
        super(pluginName);
        folder = "";
        fileName = "";
        ext = "";
        lastModifiedDate = null;
        fileSize = "";
        imgWidthPix = "";
        imgHeightPix = "";
    }

    @Override
    public void store(TransientFile transientImage) throws CTException {
        transientImage.set(pluginName, "folder", folder);
        transientImage.set(pluginName, "fileName", fileName);
        transientImage.set(pluginName, "ext", ext);
        transientImage.set(pluginName, "lastModifiedDate", lastModifiedDate);
        transientImage.set(pluginName, "sizeByte", fileSize);
        transientImage.set(pluginName, "widthPix", imgWidthPix);
        transientImage.set(pluginName, "heightPix", imgHeightPix);
        transientImage.store();
    }

    @Override
    public void read(TransientFile transientImage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
