package ollama;

import claudiosoft.ollama.OAPI;
import claudiosoft.utils.BasicUtils;
import io.github.ollama4j.models.response.Model;
import io.github.ollama4j.utils.Options;
import io.github.ollama4j.utils.OptionsBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author claudio.tortorelli
 */
public class OllamaAPISampleImageOptions {

    public static void main(String[] args) throws Exception {
        OAPI.init();

        Model curModel = null;
        for (Model model : OAPI.getModelList()) {
            System.out.println(model.getModelName());
            if (model.getModelName().equalsIgnoreCase("qwen2.5vl")) {
                curModel = model;
            }
        }
        OAPI.setModel(curModel);
        OAPI.setTimeout(120);

        //String prompt = "Please describe as better as possible this picture using up to 255 characters and not less than 128 character. Location is Villa Lante Viterbo";
        //String prompt = "create 5 tag word for this image, in a single line with tags separated by comma";
        //String prompt = "Please describe as better as possible this picture using up to 255 characters and not less than 128 character";
        //String prompt = "Descrivi al meglio l'immagine usando al più 256 caratteri e almeno 128 caratteri";
        //String prompt = "Estrai 5 tag in italiano correlati all'immagine";
        String prompt = "Report 5 words that describe this image";
        ArrayList<File> images = new ArrayList<>();
        BasicUtils.listFilesForFolder(new File("..\\..\\testImg\\"), images);

        // https://ollama4j.github.io/ollama4j/apis-extras/options-builder
        Options opts = new OptionsBuilder()
                .setTemperature(1.2f)
                .setTopK(100)
                .setTopP(0.9f)
                .setNumThread(Runtime.getRuntime().availableProcessors() - 1)
                .setNumPredict(40)
                .setRepeatPenalty(1.0f)
                .setRepeatLastN(0)
                .setSeed(0)
                .build();

        for (File image : images) {
            BasicUtils.startElapsedTime();
            System.out.println("\n" + opts.toString());
            System.out.println(image.getCanonicalPath());
            LinkedList<File> curFileList = new LinkedList<>();
            curFileList.add(image);
            String response = OAPI.generateWithImage(prompt, curFileList);
            System.out.println(response + "\n" + response.length() + " chars\n" + BasicUtils.getElapsedTime() + " sec\n------------");
        }

    }
}
