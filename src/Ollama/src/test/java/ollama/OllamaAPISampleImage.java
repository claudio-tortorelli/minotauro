package ollama;

import claudiosoft.ollama.OAPI;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.types.OllamaModelType;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author claudio.tortorelli
 */
public class OllamaAPISampleImage {

    public static void main(String[] args) throws Exception {
        OAPI.init();

        Model curModel = null;
        for (Model model : OAPI.getModelList()) {
            System.out.println(model.getModelName());
            if (model.getModelName().equalsIgnoreCase(OllamaModelType.LLAVA)) {
                curModel = model;
            }
        }
        OAPI.setModel(curModel);
        OAPI.setTimeout(120);

        //String prompt = "Please describe as better as possible this picture using up to 255 characters and not less than 128 character. Location is Villa Lante Viterbo";
        String prompt = "create 10 tag for this image, in a single line with tags separated by comma";
        //String prompt = "Please return the file format description and some image features like image sizes in pixel, and exif data";
        ArrayList<File> images = new ArrayList<>();
        images.add(new File("..\\..\\testImg\\lante.jpg"));

        String response = OAPI.generateWithImage(prompt, images);
        System.out.println(response);
    }
}
