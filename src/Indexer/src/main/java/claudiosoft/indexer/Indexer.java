package claudiosoft.indexer;

import claudiosoft.commons.CTException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.PathMatcher;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author claudio.tortorelli
 */
public class Indexer {

    private File root;
    private File index;
    private File tempIndex;
    private PathMatcher matcher;
    private List<String> indexData;
    private File visitIndex;

    public Indexer(File root, File index) throws CTException {
        this(root, index, "*");
    }

    public Indexer(File root, File index, String globFilter) throws CTException {
        this.root = root;
        this.index = index;
        if (!root.exists()) {
            throw new CTException("root folder not exists");
        }
        if (!root.canRead()) {
            throw new CTException("cannot access to root folder");
        }
        String tmpPath = String.format("%s.tmp", index.getAbsolutePath());
        tempIndex = new File(tmpPath);
        if (tempIndex.exists()) {
            this.index.delete(); // remove incomplete index
        }
        String visitPath = String.format("%s.next", index.getAbsolutePath());
        visitIndex = new File(visitPath);

        matcher = FileSystems.getDefault().getPathMatcher("glob:" + globFilter);
        indexData = new LinkedList<>();
    }

    public void buildIndex() throws IOException {
        buildIndex(true);
    }

    public void buildIndex(boolean recursive) throws IOException {
        if (index.exists()) {
            return;
        }
        if (!tempIndex.exists()) {
            tempIndex.createNewFile();
        }
        try {
            index.createNewFile();
            addFolder(root, recursive);
            tempIndex.delete();
        } finally {

        }
    }

    private void addFolder(File folder, boolean recursive) throws IOException {

        File[] children = folder.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isFile() && !Files.isSymbolicLink(child.toPath()) && matcher.matches(child.toPath().getFileName())) {
                    String line = String.format("%s\n", child.getCanonicalPath());
                    Files.write(index.toPath(), line.getBytes(), StandardOpenOption.APPEND);
                } else if (child.isDirectory() && recursive) {
                    addFolder(child, recursive);
                }
            }
        }
    }

    public synchronized File visitNext() throws CTException, IOException {
        if (!index.exists()) {
            throw new CTException("index must be built");
        }
        if (!index.canRead()) {
            throw new CTException("cannot read index");
        }
        if (indexData.isEmpty()) {
            indexData = Files.readAllLines(index.toPath(), StandardCharsets.UTF_8);
        }
        int maxFile = indexData.size();
        int nextFile = 0;
        if (!visitIndex.exists()) {
            visitIndex.createNewFile();
        } else {
            String visLine = Files.readString(visitIndex.toPath());
            nextFile = Integer.parseInt(visLine) + 1;
        }
        if (nextFile == maxFile) {
            reset();
            return null;
        }
        Files.writeString(visitIndex.toPath(), String.format("%d", nextFile), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        return new File(indexData.get(nextFile));
    }

    public void reset() throws CTException {
        if (!index.exists()) {
            throw new CTException("index must be built");
        }
        visitIndex.delete();
    }

}
