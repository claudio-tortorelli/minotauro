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

    public TransientFile get(File imageFile) throws CTException {

        try {
            String imgFilePath = imageFile.getCanonicalPath().toLowerCase();
            String rootPath = imageRootPath.getCanonicalPath().toLowerCase();
            if (!imgFilePath.toLowerCase().contains(rootPath)) {
                throw new CTException(String.format("%s is not included into index", imageFile.getCanonicalPath()));
            }
            String relativePath = imgFilePath.substring(rootPath.length(), imgFilePath.length());
            String sha1 = BasicUtils.bytesToHex(BasicUtils.getSHA1(relativePath));

            String transientImagePath = String.format("%s/%s_%s.transient", transientRootPath.getCanonicalPath(), sha1, imageFile.getName());
            File transientImageFile = new File(transientImagePath);

            return new TransientFile(transientImageFile);
        } catch (Exception ex) {
            throw new CTException(ex);
        }
    }
}
