package claudiosoft.pluginconfig;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.ollama.OAPI;
import io.github.ollama4j.models.response.Model;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageTagConfig extends PluginConfig {

    public String prompt;

    public ImageTagConfig(Config config) throws CTException {
        super(config);

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

}
