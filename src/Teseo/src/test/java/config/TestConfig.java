package config;

import claudiosoft.minotauro.Config;
import java.io.File;

/**
 *
 * @author claudio.tortorelli
 */
public class TestConfig {

    public static void main(String[] args) throws Exception {
        Config conf = new Config(new File("../../config/config.ini"));
        System.out.println(conf.get("ollama", "image_desc_model"));
        System.out.println(conf.get("ollama", "undef"));
        System.out.println(conf.get("undef", "image_desc_model"));
    }
}
