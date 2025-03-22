package claudiosoft.indexer;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTException;
import claudiosoft.utils.BasicUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.PathMatcher;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
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
    private HashMap<String, String> extensions;
    private BasicLogger logger;

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
        extensions = new HashMap<>();

        logger = BasicLogger.get();
    }

    public void buildIndex() throws IOException {
        buildIndex(true);
    }

    public synchronized void buildIndex(boolean recursive) throws IOException {
        if (index.exists()) {
            return;
        }
        if (!tempIndex.exists()) {
            tempIndex.createNewFile();
        }
        try {
            index.createNewFile();
            logger.info("start building index");
            addFolder(root, recursive);
            logger.info("end building index");
            tempIndex.delete();
        } finally {

        }
    }

    private void addFolder(File folder, boolean recursive) throws IOException {
        File[] children = folder.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isFile() && !Files.isSymbolicLink(child.toPath()) && matcher.matches(child.toPath().getFileName())) {
                    String path = child.getCanonicalPath();
                    extensions.put(BasicUtils.getExtension(path), "");
                    Files.write(index.toPath(), String.format("%s\n", path).getBytes(), StandardOpenOption.APPEND);
                } else if (child.isDirectory() && recursive) {
                    addFolder(child, recursive);
                }
            }
        }
    }

    public List<String> getExtensions() {
        List<String> exts = new LinkedList<>();
        for (String ext : extensions.keySet()) {
            exts.add(ext);
        }
        return exts;
    }

    public synchronized String getVisitIndex() throws IOException {
        if (!visitIndex.exists()) {
            return "";
        }
        return String.format("%d/%d", Integer.parseInt(Files.readString(visitIndex.toPath())) + 1, indexData.size());
    }

    public synchronized File startVisit() throws CTException, IOException {
        if (!index.exists()) {
            throw new CTException("index must be built");
        }
        if (!index.canRead()) {
            throw new CTException("cannot read index");
        }
        if (indexData.isEmpty()) {
            indexData = Files.readAllLines(index.toPath(), StandardCharsets.UTF_8);
        }
        int nextFile = 0;
        if (!visitIndex.exists()) {
            visitIndex.createNewFile();
            Files.writeString(visitIndex.toPath(), String.format("%d", nextFile), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } else {
            // previous visit was stopped so continue 
            String visLine = Files.readString(visitIndex.toPath());
            nextFile = Integer.parseInt(visLine);
        }
        return new File(indexData.get(nextFile));
    }

    public synchronized File visitNext() throws CTException, IOException {
        if (!index.exists()) {
            throw new CTException("index must be built");
        }
        if (!index.canRead()) {
            throw new CTException("cannot read index");
        }
        if (indexData.isEmpty() || !visitIndex.exists()) {
            throw new CTException("visit must be started");
        }
        String visLine = Files.readString(visitIndex.toPath());
        int nextFile = Integer.parseInt(visLine) + 1;
        if (nextFile == indexData.size()) {
            reset();
            return null;
        }
        Files.writeString(visitIndex.toPath(), String.format("%d", nextFile), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        return new File(indexData.get(nextFile));
    }

    private void reset() throws CTException {
        if (!index.exists()) {
            throw new CTException("index must be built");
        }
        visitIndex.delete();
    }

}
