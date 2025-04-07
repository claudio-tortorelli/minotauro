package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.transientimage.TransientImage;
import claudiosoft.utils.BasicUtils;
import java.io.File;
import java.util.Date;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageFileData extends BaseImagePlugin {

    private String originalPath;
    private String fileName;
    private String lastModifiedDate;

    public ImageFileData(int step) {
        super(step);
    }

    @Override
    public void init(Config config, String pluginName) throws CTException {
        super.init(config, pluginName);
    }

    @Override
    public void apply(File image, TransientImage transientImage) throws CTException {
        super.apply(image, transientImage);
        try {
            originalPath = imageFile.getCanonicalPath();
            fileName = imageFile.getName();
            lastModifiedDate = BasicUtils.dateToString(new Date(imageFile.lastModified()));
            store();
        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex);
        }

    }

    @Override
    public void store() throws CTException {
        transientImage.set(this.getClass().getSimpleName(), "originalPath", originalPath);
        transientImage.set(this.getClass().getSimpleName(), "fileName", fileName);
        transientImage.set(this.getClass().getSimpleName(), "lastModifiedDate", lastModifiedDate);
        transientImage.store();
    }

}
