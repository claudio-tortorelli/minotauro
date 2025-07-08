package claudiosoft.transientdata;

import claudiosoft.commons.CTException;
import claudiosoft.utils.BasicUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Give access to the transient image file and its properties
 *
 * @author claudio.tortorelli
 */
public class TransientProvider {

    private File imageRootPath;
    private File transientRootPath;

    public static TransientProvider imgProvider = null;

    public static synchronized void init(File imageRootPath, File transientRootPath) throws IOException {
        if (imgProvider != null) {
            return;
        }
        imgProvider = new TransientProvider(imageRootPath, transientRootPath);
    }

    public static synchronized TransientProvider getProvider() throws CTException {
        if (imgProvider == null) {
            throw new CTException("Transient image provider not initialized");
        }
        return imgProvider;
    }

    private TransientProvider(File imageRootPath, File transientRootPath) throws IOException {
        this.imageRootPath = imageRootPath;
        this.transientRootPath = transientRootPath;
        Files.createDirectories(this.transientRootPath.toPath());
    }

    public TransientFile get(File origFile) throws CTException {

        try {
            String origFilePath = origFile.getCanonicalPath().toLowerCase();
            String rootPath = imageRootPath.getCanonicalPath().toLowerCase();
            if (!origFilePath.toLowerCase().contains(rootPath)) {
                throw new CTException(String.format("%s is not included into index", origFile.getCanonicalPath()));
            }
            String relativePath = origFilePath.substring(rootPath.length(), origFilePath.length());
            String sha1 = BasicUtils.bytesToHex(BasicUtils.getSHA1(relativePath));

            String origFileName = origFile.getName();
            if (origFile.isDirectory()) {
                origFileName = String.format("FOLD_%s", origFile.getName());
            }
            origFileName = origFileName.replace(" ", "_");
            String transientImagePath = String.format("%s/%s_%s.transient", transientRootPath.getCanonicalPath(), origFileName, sha1);
            File transientImageFile = new File(transientImagePath);

            return new TransientFile(transientImageFile);
        } catch (Exception ex) {
            throw new CTException(ex);
        }
    }
}
