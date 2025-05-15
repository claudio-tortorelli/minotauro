package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.indexer.Indexer;
import claudiosoft.pluginbean.BeanFileData;
import claudiosoft.utils.BasicUtils;
import java.util.Date;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageFileData extends BaseImagePlugin {

    public ImageFileData(int step) {
        super(step);
    }

    @Override
    public void init(Config config) throws CTException {
        super.init(config);
    }

    @Override
    public void apply(Indexer indexer) throws CTException {
        super.apply(indexer);
        try {
            BeanFileData data = new BeanFileData(this.getClass().getSimpleName());

            data.originalPath = imageFile.getCanonicalPath();
            data.fileName = imageFile.getName();
            data.ext = BasicUtils.getExtension(imageFile);
            data.lastModifiedDate = BasicUtils.dateToString(new Date(imageFile.lastModified()));
            data.store(transientImage);
        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex);
        }

    }

}
