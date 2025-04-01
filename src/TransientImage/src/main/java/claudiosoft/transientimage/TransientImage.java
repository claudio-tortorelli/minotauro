package claudiosoft.transientimage;

import claudiosoft.commons.CTException;
import java.io.File;
import java.io.IOException;
import org.ini4j.Ini;
import org.ini4j.Wini;

/**
 *
 * @author claudio.tortorelli
 */
public class TransientImage {

    private Wini transientFile;

    public TransientImage(File transientImageFile) throws IOException {
        if (!transientImageFile.exists()) {
            transientImageFile.createNewFile();
        }
        transientFile = new Wini(transientImageFile);
    }

    public String get(String sectionName, String property, String defaultValue) throws CTException {
        Ini.Section section = transientFile.get(sectionName);
        if (section == null) {
            throw new CTException("section undefined");
        }
        String prop = section.get(property);
        if (prop == null) {
            prop = defaultValue;
        }
        return prop;
    }

    public void set(String sectionName, String property, String value) throws CTException {
        Ini.Section section = transientFile.get(sectionName);
        if (section == null) {
            section = transientFile.add(sectionName);
        }
        section.put(property, value);
    }

    public void set(String sectionName, String property, byte[] value) throws CTException {
        Ini.Section section = transientFile.get(sectionName);
        if (section == null) {
            throw new CTException("section undefined");
        }
        section.put(property, value);
    }

    public void store() throws IOException {
        transientFile.store();
    }
}
