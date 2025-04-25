package claudiosoft.ollama;

import claudiosoft.commons.CTException;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.types.OllamaModelType;
import io.github.ollama4j.utils.OptionsBuilder;
import java.io.File;
import java.util.List;

/**
 * Wrap Ollama4j api
 *
 * @author claudio.tortorelli
 */
public class OAPI {

    private static OllamaAPI ollamaSrv = null;
    private static Model selectedModel = null;

    private static final String DEFAULT_OLLAMA_HOST = "http://localhost:11434/";
    private static final int TIMEOUT = 30;

    public static synchronized void init() throws CTException {
        init(DEFAULT_OLLAMA_HOST);
    }

    public static synchronized void init(String hostPort) throws CTException {
        if (ollamaSrv != null) {
            return; // already initialized
        }
        ollamaSrv = new OllamaAPI(hostPort);
        ollamaSrv.setVerbose(true);
        ollamaSrv.setRequestTimeoutSeconds(TIMEOUT);
        boolean isOllamaServerReachable = ollamaSrv.ping();
        if (!isOllamaServerReachable) {
            throw new CTException("Ollama server not reachable");
        }
    }

    public static void setTimeout(int seconds) throws CTException {
        if (ollamaSrv == null) {
            init();
        }
        ollamaSrv.setRequestTimeoutSeconds(seconds);
    }

    public static final List<Model> getModelList() throws Exception {
        if (ollamaSrv == null) {
            init();
        }
        return ollamaSrv.listModels();
    }

    public static final boolean isModelInList(String modelName) throws Exception {
        if (ollamaSrv == null) {
            init();
        }
        return ollamaSrv.listModels().contains(modelName);
    }

    public static final List<Model> addModelToList(OllamaModelType model) throws Exception {
        if (ollamaSrv == null) {
            init();
        }
        ollamaSrv.pullModel(model.toString());
        return getModelList();
    }

    public static final List<Model> deleteModelFromList(String modelName) throws Exception {
        if (ollamaSrv == null) {
            init();
        }
        ollamaSrv.deleteModel(modelName, true);
        return getModelList();
    }

    public static final String generate(String prompt) throws Exception {
        return generate(selectedModel, prompt);
    }

    public static final String generate(Model model, String prompt) throws Exception {
        if (ollamaSrv == null) {
            init();
        }
        if (model == null && selectedModel == null) {
            throw new Exception("no LLM model selected");
        }
        selectedModel = model;
        OllamaResult result = ollamaSrv.generate(selectedModel.getModel(), prompt, false, new OptionsBuilder().build());
        return result.getResponse();
    }

    public static final String generateWithImage(String prompt, List<File> images) throws Exception {
        return generateWithImage(selectedModel, prompt, images);
    }

    public static final String generateWithImage(Model model, String prompt, List<File> images) throws Exception {
        if (ollamaSrv == null) {
            init();
        }
        if (model == null && selectedModel == null) {
            throw new Exception("no LLM model selected");
        }
        if (images.isEmpty()) {
            throw new Exception("no image to process");
        }
        selectedModel = model;
        OllamaResult result = ollamaSrv.generateWithImageFiles(selectedModel.getModel(), prompt, images, new OptionsBuilder().build());
        return result.getResponse();
    }

    public static void setModel(Model model) {
        selectedModel = model;
    }

    public static void resetModel() {
        selectedModel = null;
    }

}
