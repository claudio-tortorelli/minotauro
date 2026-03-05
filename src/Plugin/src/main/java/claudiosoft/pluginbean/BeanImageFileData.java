package claudiosoft.pluginbean;

import claudiosoft.commons.CTException;
import claudiosoft.transientdata.TransientFile;

/**
 *
 * @author claudio.tortorelli
 */
public class BeanImageFileData extends BasePluginBean {

    public String folder;
    public String fileName;
    public String lastModifiedDate;
    public String ext;
    public String fileSize;
    public String imgWidthPix;
    public String imgHeightPix;

    public BeanImageFileData(String pluginName) {
        super(pluginName);
        folder = "";
        fileName = "";
        ext = "";
        lastModifiedDate = "";
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
    public void read(TransientFile transientImage) throws CTException {
        folder = transientImage.get(pluginName, "folder", "");
        fileName = transientImage.get(pluginName, "fileName", "");
        ext = transientImage.get(pluginName, "ext", "");
        lastModifiedDate = transientImage.get(pluginName, "lastModifiedDate", "");
        fileSize = transientImage.get(pluginName, "sizeByte", "");
        imgWidthPix = transientImage.get(pluginName, "widthPix", "");
        imgHeightPix = transientImage.get(pluginName, "heightPix", "");
    }

}
