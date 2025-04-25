package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.ollama.OAPI;
import claudiosoft.pluginbean.BeanDescription;
import claudiosoft.transientimage.TransientImage;
import io.github.ollama4j.models.response.Model;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageDescription extends BaseImagePlugin {

    private String prompt;

    public ImageDescription(int step) {
        super(step);
    }

    @Override
    public void init(Config config, String pluginName) throws CTException {
        super.init(config, pluginName);
        Model curModel = null;
        try {
            String modelName = config.get(this.getClass().getSimpleName(), "modelName", "llava");
            logger.info("looking for model " + modelName);

            logger.info("list ollama installed models");
            for (Model model : OAPI.getModelList()) {
                logger.info("- " + model.getModelName());
                if (model.getModelName().equalsIgnoreCase(modelName)) {
                    curModel = model;
                }
            }
            OAPI.setModel(curModel);
        } catch (Exception ex) {
            logger.error("unable to select current model", ex);
        }

        int timeout = Integer.parseInt(config.get(this.getClass().getSimpleName(), "timeout", "120"));
        OAPI.setTimeout(timeout);

        prompt = config.get(this.getClass().getSimpleName(), "prompt", "write a synthetic description of this image. The maximum length is %d characters");
        if (!prompt.contains("%d")) {
            throw new CTException("invalid prompt");
        }
        int maxLength = Integer.parseInt(config.get(this.getClass().getSimpleName(), "maxLengthChar", "255"));
        prompt = String.format(prompt, maxLength);

        logger.info(String.format("prompt: %s", prompt));
    }

    @Override
    public void apply(File image, TransientImage transientImage) throws CTException {
        super.apply(image, transientImage);
        File tmpImage = null;
        try {
            BeanDescription data = new BeanDescription(this.getClass().getSimpleName());

            ArrayList<File> images = new ArrayList<>();
            images.add(image);

            data.description = OAPI.generateWithImage(prompt, images);
            if (logger.isDebug()) {
                logger.debug(data.description);
            }
            data.store(transientImage);
        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex);
        } finally {
            if (tmpImage != null) {
                tmpImage.delete();
            }
        }
    }

}
