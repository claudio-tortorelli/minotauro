package claudiosoft.pluginconfig;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.ollama.OAPI;
import io.github.ollama4j.models.response.Model;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageDescriptionConfig extends PluginConfig {

    public String prompt;

    public ImageDescriptionConfig(Config config, String pluginName) throws CTException {
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

        prompt = config.get(pluginName, "prompt", "write a synthetic description of this image. The maximum length is %d characters");
        if (!prompt.contains("%d")) {
            throw new CTException("invalid prompt");
        }
        int maxLength = Integer.parseInt(config.get(pluginName, "maxLengthChar", "255"));
        prompt = String.format(prompt, maxLength);

        logger.info(String.format("prompt: %s", prompt));
    }

}
