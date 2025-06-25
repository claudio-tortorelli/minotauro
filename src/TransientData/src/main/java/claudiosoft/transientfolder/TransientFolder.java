package claudiosoft.transientfolder;

import claudiosoft.commons.CTException;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.ini4j.Wini;

/**
 *
 * @author claudio.tortorelli
 */
public class TransientFolder {

    protected Wini transientFolder;

    public TransientFolder(File transientImageFile) throws IOException {
        if (!transientImageFile.exists()) {
            transientImageFile.createNewFile();
        }
        transientFolder = new Wini(transientImageFile);
    }

    public String get(String sectionName, String property, String defaultValue) throws CTException {
        Ini.Section section = transientFolder.get(sectionName);
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
        Ini.Section section = transientFolder.get(sectionName);
        if (section == null) {
            section = transientFolder.add(sectionName);
        }
        section.put(property, value);
    }

    public void set(String sectionName, String property, int value) throws CTException {
        Ini.Section section = transientFolder.get(sectionName);
        if (section == null) {
            section = transientFolder.add(sectionName);
        }
        section.put(property, value);
    }

    public void set(String sectionName, String property, double value) throws CTException {
        Ini.Section section = transientFolder.get(sectionName);
        if (section == null) {
            section = transientFolder.add(sectionName);
        }
        section.put(property, value);
    }

    public void set(String sectionName, String property, boolean value) throws CTException {
        Ini.Section section = transientFolder.get(sectionName);
        if (section == null) {
            section = transientFolder.add(sectionName);
        }
        section.put(property, value);
    }

    public void set(String sectionName, String property, byte[] value) throws CTException {
        Ini.Section section = transientFolder.get(sectionName);
        if (section == null) {
            throw new CTException("section undefined");
        }
        section.put(property, value);
    }

    public void set(String sectionName, String property, Date value) throws CTException {
        Ini.Section section = transientFolder.get(sectionName);
        if (section == null) {
            throw new CTException("section undefined");
        }
        section.put(property, value);
    }

    public synchronized void store() throws CTException {
        try {
            transientFolder.store();
        } catch (IOException ex) {
            throw new CTException(ex);
        }
    }

    public HashMap<String, String> getErrors() throws CTException {
        HashMap<String, String> errorMap = new HashMap<>();
        List<Section> sections = transientFolder.getAll(this);
        if (sections == null) {
            return null;
        }
        for (Section section : sections) {
            String error = get(section.getName(), "error", "");
            if (!error.isEmpty()) {
                errorMap.put(section.getName(), error);
            }
        }
        if (errorMap.isEmpty()) {
            return null;
        }
        return errorMap;
    }
}
