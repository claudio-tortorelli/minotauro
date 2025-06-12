package claudiosoft.minotauro;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Version {

    private static String version = null;
    private static String build = null;

    public static String getVersion() {
        if (version != null) {
            return version;
        }

        // try to load from maven properties first
        try {
            Properties p = new Properties();
            InputStream is = Version.class.getClassLoader().getResourceAsStream("build.properties");
            if (is != null) {
                p.load(is);
                version = p.getProperty("version", "");
                build = p.getProperty("timestamp", "");
            }
        } catch (IOException ex) {

        }

        // fallback to using Java API
        if (version == null) {
            Package aPackage = Version.class.getPackage();
            if (aPackage != null) {
                version = aPackage.getImplementationVersion();
                if (version == null) {
                    version = aPackage.getSpecificationVersion();
                }
            }
        }

        if (version == null) {
            // we could not compute the version so use a blank
            version = "DEBUG VERSION";
            build = "no info";
        }

        return version;
    }

    public static String getBuild() {
        return build;
    }
}
