package claudiosoft.ollama;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.types.OllamaModelType;
import io.github.ollama4j.utils.OptionsBuilder;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Wrap Ollama4j api
 *
 * @author claudio.tortorelli
 */
public class OAPI {

    private static OllamaAPI ollamaSrv = null;
    private static Model selectedModel = null;

    private static final String host = "http://localhost:11434/";
    private static final int timeout = 30;

    public static synchronized void init() throws IOException {
        init(host);
    }

    public static synchronized void init(String hostPort) throws IOException {
        if (ollamaSrv == null) {
            ollamaSrv = new OllamaAPI(hostPort);
        }
        ollamaSrv.setVerbose(true);
        ollamaSrv.setRequestTimeoutSeconds(timeout);
        boolean isOllamaServerReachable = ollamaSrv.ping();
        if (!isOllamaServerReachable) {
            throw new IOException("Ollama server not reachable");
        }
    }

    public static void setTimeout(int seconds) throws Exception {
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

    public static final String generate(String answer) throws Exception {
        return generate(selectedModel, answer);
    }

    public static final String generate(Model model, String answer) throws Exception {
        if (ollamaSrv == null) {
            init();
        }
        if (model == null && selectedModel == null) {
            throw new Exception("no LLM model selected");
        }
        selectedModel = model;
        OllamaResult result = ollamaSrv.generate(selectedModel.getModel(), answer, false, new OptionsBuilder().build());
        return result.getResponse();
    }

    public static final String generateWithImage(String answer, List<File> images) throws Exception {
        return generateWithImage(selectedModel, answer, images);
    }

    public static final String generateWithImage(Model model, String answer, List<File> images) throws Exception {
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
        OllamaResult result = ollamaSrv.generateWithImageFiles(selectedModel.getModel(), answer, images, new OptionsBuilder().build());
        return result.getResponse();
    }

    public static void setModel(Model model) {
        selectedModel = model;
    }

    public static void resetModel() {
        selectedModel = null;
    }

}
