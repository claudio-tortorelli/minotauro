package claudiosoft.indexer;

import claudiosoft.commons.CTException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author claudio.tortorelli
 */
public interface IndexInterface {

    public void buildIndex() throws IOException, CTException;

    public void init(File root, File index, File folderIndex) throws CTException;

    public void init(File root, File index, File folderIndex, String globFilter) throws CTException;

    public List<String> getExtensions();

    public List<String> getFolders() throws IOException;

    public File startVisit(String pluginName) throws CTException, IOException;

    public File visitNext() throws CTException, IOException;

    public String getVisitIndex() throws IOException;

    public void resetIndex() throws CTException;
}
