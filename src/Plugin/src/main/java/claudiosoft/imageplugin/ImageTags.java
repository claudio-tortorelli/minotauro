package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.ollama.OAPI;
import claudiosoft.pluginbean.BeanTags;
import claudiosoft.transientimage.TransientImage;
import claudiosoft.utils.BasicUtils;
import io.github.ollama4j.models.response.Model;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageTags extends BaseImagePlugin {

    private String prompt;
    private final boolean useThumb = false;

    public ImageTags(int step) {
        super(step);
    }

    @Override
    public void init(Config config, String pluginName) throws CTException {
        super.init(config, pluginName);
        OAPI.init();

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

        prompt = config.get(this.getClass().getSimpleName(), "prompt", "create %d tags for this image, in a single line with tags separated by comma");
        if (!prompt.contains("%d")) {
            throw new CTException("invalid prompt");
        }
        int nTags = Integer.parseInt(config.get(this.getClass().getSimpleName(), "tagNumber", "5"));
        prompt = String.format(prompt, nTags);

        logger.info(String.format("prompt: %s", prompt));
    }

    @Override
    public void apply(File image, TransientImage transientImage) throws CTException {
        super.apply(image, transientImage);

        File tmpImage = null;
        try {
            BeanTags data = new BeanTags(this.getClass().getSimpleName());

            File imgToAnalyze = image;

            if (useThumb) {
                String thumbB64 = transientImage.get("ImageThumbnail", "thumb", "");
                if (!thumbB64.isEmpty()) {
                    tmpImage = Files.createTempFile("tmpImage", "." + BasicUtils.getExtension(image.getCanonicalPath())).toFile();
                    Files.write(tmpImage.toPath(), Base64.getDecoder().decode(thumbB64));
                    imgToAnalyze = tmpImage;
                }
            }

            ArrayList<File> images = new ArrayList<>();
            images.add(imgToAnalyze);

            data.tagList = OAPI.generateWithImage(prompt, images);
            if (logger.isDebug()) {
                logger.debug(data.tagList);
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
