package claudiosoft.transientimage;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTException;
import claudiosoft.utils.BasicUtils;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Give access to the transient image file and its properties
 *
 * @author claudio.tortorelli
 */
public class TransientImageProvider {
    
    private File imageRootPath;
    private File transientRootPath;
    private BasicLogger logger;
    
    public TransientImageProvider(File imageRootPath, File transientRootPath) throws IOException {
        logger = BasicLogger.get();
        this.imageRootPath = imageRootPath;
        this.transientRootPath = transientRootPath;
    }
    
    public TransientImage get(File imageFile) throws NoSuchAlgorithmException, IOException, CTException {
        
        String imgFilePath = imageFile.getCanonicalPath().toLowerCase();
        String rootPath = imageRootPath.getCanonicalPath().toLowerCase();
        if (!imgFilePath.toLowerCase().contains(rootPath)) {
            throw new CTException(String.format("%s is not included into index", imageFile.getCanonicalPath()));
        }
        String relativePath = imgFilePath.substring(rootPath.length(), imgFilePath.length());
        String sha1 = Arrays.toString(BasicUtils.getSHA1(relativePath));
        
        String transientImagePath = String.format("%s/%s_%s.transient", transientRootPath.getCanonicalPath(), sha1, imageFile.getName());
        File transientImageFile = new File(transientImagePath);
        
        return new TransientImage(transientImageFile);
    }
}
