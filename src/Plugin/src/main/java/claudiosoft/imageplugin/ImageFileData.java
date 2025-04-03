package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.transientimage.TransientImage;
import claudiosoft.transientimage.TransientImageProvider;
import claudiosoft.utils.BasicUtils;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
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
    public void init(Config config, TransientImageProvider transientImageProvider) throws CTException {
        super.init(config, transientImageProvider);
    }

    @Override
    public void apply(File image) throws CTException {
        super.apply(image);
        try {
            originalPath = imageFile.getCanonicalPath();
            fileName = imageFile.getName();
            lastModifiedDate = BasicUtils.dateToString(new Date(imageFile.lastModified()));
            store();
        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex);
        }

    }

    protected void store() throws CTException, IOException, NoSuchAlgorithmException {
        TransientImage transientImage = transientImageProvider.get(imageFile);
        transientImage.set(this.getClass().getSimpleName(), "originalPath", originalPath);
        transientImage.set(this.getClass().getSimpleName(), "fileName", fileName);
        transientImage.set(this.getClass().getSimpleName(), "lastModifiedDate", lastModifiedDate);
        transientImage.store();
    }

}
