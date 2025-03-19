package claudiosoft.minotauro;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.ini4j.Ini;

/**
 *
 * @author claudio.tortorelli
 */
public class Config {

    private Ini ini;

    public Config(File configFile) throws FileNotFoundException, IOException {
        ini = new Ini();
        ini.load(new FileReader(configFile));
    }

    public String get(String sectionName, String property) throws Exception {
        return get(sectionName, property, "");
    }

    public String get(String sectionName, String property, String defaultValue) throws Exception {
        Ini.Section section = ini.get(sectionName);
        if (section == null) {
            throw new Exception("section undefined");
        }
        String prop = section.get(property);
        if (prop == null) {
            prop = defaultValue;
        }
        return prop;
    }

}
