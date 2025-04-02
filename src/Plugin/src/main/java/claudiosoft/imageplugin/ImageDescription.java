package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.transientimage.TransientImageProvider;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageDescription extends BaseImagePlugin {

    public ImageDescription(int step) {
        super(step);
    }

    @Override
    public void init(Config config, TransientImageProvider transientImageProvider) throws CTException {
        super.init(config, transientImageProvider);

    }

    @Override
    public void apply(File image) throws CTException {
        super.apply(image);

    }

    protected void store(String result) throws NoSuchAlgorithmException, IOException, CTException {
//        TransientImage transientImage = transientImageProvider.get(imageFile);
//        transientImage.set(this.getClass().getSimpleName(), "id", result);
//        transientImage.store();
    }

}
