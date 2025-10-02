package claudiosoft.pluginconfig;

import claudiosoft.commons.CTError;
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

    public ImageTagConfig(Config config, String pluginName) throws CTException {
        super(config, pluginName);

        Model curModel = null;
        try {
            String modelName = config.get(pluginName, "modelName", "llava");
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

        int timeout = Integer.parseInt(config.get(pluginName, "timeout", "120"));
        OAPI.setTimeout(timeout);

        prompt = config.get(pluginName, "prompt", "create %d tags for this image, in a single line with tags separated by comma");
        if (!prompt.contains("%d")) {
            throw new CTException("invalid prompt", CTError.INVALID_ARGUMENT);
        }
        int nTags = Integer.parseInt(config.get(pluginName, "tagNumber", "5"));
        prompt = String.format(prompt, nTags);
        logger.info(String.format("prompt: %s", prompt));
    }

}
