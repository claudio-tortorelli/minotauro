package claudiosoft.transientfolder;

import claudiosoft.commons.CTException;
import claudiosoft.utils.BasicUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Give access to the transient folder and its properties
 *
 * @author claudio.tortorelli
 */
public class TransientFolderProvider {

    private File folderRootPath;
    private File transientRootPath;

    public static TransientFolderProvider folderProvider = null;

    public static synchronized void init(File folderRootPath, File transientRootPath) throws IOException {
        if (folderProvider != null) {
            return;
        }
        folderProvider = new TransientFolderProvider(folderRootPath, transientRootPath);
    }

    public static synchronized TransientFolderProvider getProvider() throws CTException {
        if (folderProvider == null) {
            throw new CTException("Transient folder provider not initialized");
        }
        return folderProvider;
    }

    private TransientFolderProvider(File folderRootPath, File transientRootPath) throws IOException {
        this.folderRootPath = folderRootPath;
        this.transientRootPath = transientRootPath;
        Files.createDirectories(this.transientRootPath.toPath());
    }

    public TransientFolder get(File folder) throws CTException {

        try {
            String folderPath = folder.getCanonicalPath().toLowerCase();
            String rootPath = folderRootPath.getCanonicalPath().toLowerCase();
            if (!folderPath.toLowerCase().contains(rootPath)) {
                throw new CTException(String.format("%s is not included into index", folder.getCanonicalPath()));
            }
            String relativePath = folderPath.substring(rootPath.length(), folderPath.length());
            String sha1 = BasicUtils.bytesToHex(BasicUtils.getSHA1(relativePath));

            String transientFolderPath = String.format("%s/%s_%s.transient", transientRootPath.getCanonicalPath(), sha1, folder.getName());
            File transientFolderFile = new File(transientFolderPath);

            return new TransientFolder(transientFolderFile);
        } catch (Exception ex) {
            throw new CTException(ex);
        }
    }
}
