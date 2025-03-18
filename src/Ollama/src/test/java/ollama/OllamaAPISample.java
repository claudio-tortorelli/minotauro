package ollama;

import claudiosoft.ollama.OAPI;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.types.OllamaModelType;

/**
 *
 * @author claudio.tortorelli
 */
public class OllamaAPISample {

    public static void main(String[] args) throws Exception {
        OAPI.init();

        Model curModel = null;
        for (Model model : OAPI.getModelList()) {
            System.out.println(model.getModelName());
            if (model.getModelName().equalsIgnoreCase(OllamaModelType.CODELLAMA)) {
                curModel = model;
            }
        }
        OAPI.setModel(curModel);
        //String imageSample = "C:\\dev\\GitHub\\minotauro\\testImg\\image-1.jpg";
        //String answerPrefix = "Please describe this picture using up to 50 words: ";
        //String response = OAPI.generate(answerPrefix + imageSample);
        String response = OAPI.generate("Can you write a simple hello world in C?");
        System.out.println(response);
    }
}
