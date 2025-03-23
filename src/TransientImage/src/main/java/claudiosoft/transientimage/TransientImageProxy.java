package claudiosoft.transientimage;

import claudiosoft.commons.BasicLogger;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.ini4j.Ini;

/**
 * Give access to the transient image file and its properties
 *
 * @author claudio.tortorelli
 */
public class TransientImageProxy {

    private BasicLogger logger;
    private Ini transientFile;

    public TransientImageProxy(File imageFile) throws IOException {
        logger = BasicLogger.get();
        transientFile = new Ini();
        transientFile.load(new FileReader(imageFile.getCanonicalPath().concat(".transient")));
    }

    public String get(String sectionName, String property, String defaultValue) throws Exception {
        Ini.Section section = transientFile.get(sectionName);
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
