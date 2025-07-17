package claudiosoft.plugin;

import claudiosoft.baseplugin.BasePlugin;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.indexer.Indexer;

/**
 * use opencv with https://www.baeldung.com/java-opencv
 *
 * @author claudio.tortorelli
 */
public class ImageFaceRecognize extends BasePlugin {

    public ImageFaceRecognize(int step) {
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

        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex);
        }
    }
}
