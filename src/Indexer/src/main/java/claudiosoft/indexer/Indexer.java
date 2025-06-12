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
    private HashMap<String, Integer> extensions;
    private BasicLogger logger;
    private String currentPluginName;
    private List<String> folders;
    private String globFilter;

    public Indexer(File root, File index) throws CTException, SecurityException, IOException {
        this(root, index, "*");
    }

    public Indexer(File root, File index, String globFilter) throws CTException, SecurityException, IOException {
        this.root = root;
        this.index = index;
        this.currentPluginName = "plugin";
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

        this.globFilter = globFilter.toLowerCase();
        matcher = FileSystems.getDefault().getPathMatcher("glob:" + this.globFilter);
        indexData = new LinkedList<>();
        extensions = new HashMap<>();
        folders = new LinkedList<>();

        logger = BasicLogger.get();
    }

    public void buildIndex() throws IOException {
        buildIndex(false);
    }

    public void buildIndex(boolean force) throws IOException {
        buildIndex(force, true);
    }

    public synchronized void buildIndex(boolean force, boolean recursive) throws IOException {
        if (force) {
            logger.info("index rebuilding is forced");
            index.delete();
        }
        if (index.exists()) {
            logger.info(String.format("index already present in %s with %d entries", index.getCanonicalPath(), countIndexEntries()));
            return;
        }
        if (!tempIndex.exists()) {
            tempIndex.createNewFile();
        }
        try {
            index.createNewFile();
            logger.info("building index");
            addFolder(root, recursive);
            tempIndex.delete();

            logger.info("index extensions found:");
            int total = 0;
            int totalFiltered = 0;
            for (String ext : getExtensions()) {
                int instances = extensions.get(ext);
                logger.info(String.format(" - %s (%d)", ext, instances));
                total += instances;
                if (globFilter.contains(ext)) {
                    totalFiltered += instances;
                }
            }
            logger.info("total: " + total);
            logger.info("total extension filtered: " + totalFiltered);
            logger.info("index folders found:");
            for (String folder : getFolders()) {
                logger.info(String.format(" - %s", folder));
            }
        } finally {

        }
    }

    private void addFolder(File folder, boolean recursive) throws IOException {
        File[] children = folder.listFiles();
        boolean isFolderToBeStored = false;
        if (children != null) {
            for (File child : children) {
                if (child.isFile() && !Files.isSymbolicLink(child.toPath())) {
                    String path = child.getCanonicalPath();
                    String ext = BasicUtils.getExtension(path);
                    if (ext.isEmpty()) {
                        ext = "<none>";
                    }
                    int count = 1;
                    if (extensions.containsKey(ext)) {
                        count = extensions.get(ext) + 1;
                    }
                    extensions.put(ext, count);
                    if (matcher.matches(child.toPath().getFileName())) {
                        Files.write(index.toPath(), String.format("%s\n", path).getBytes(), StandardOpenOption.APPEND);
                        isFolderToBeStored = true;
                    }
                } else if (child.isDirectory() && recursive) {
                    addFolder(child, recursive);
                }
            }
            if (isFolderToBeStored) {
                folders.add(folder.getCanonicalPath());
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

    public List<String> getFolders() {
        return folders;
    }

    public synchronized int countIndexEntries() throws IOException {
        if (!index.exists()) {
            return 0;
        }
        return Files.readAllLines(index.toPath()).size();
    }

    public synchronized String getVisitIndex() throws IOException {
        if (!visitIndex.exists()) {
            return "";
        }
        String last = Files.readString(visitIndex.toPath());
        String[] split = last.split(";");
        return String.format("%d/%d", Integer.parseInt(split[1]) + 1, indexData.size());
    }

    public int countExtension(String ext) {
        if (extensions.containsKey(ext)) {
            return extensions.get(ext);
        }
        return 0;
    }

    public synchronized File startVisit(String pluginName) throws CTException, IOException {
        if (!index.exists()) {
            throw new CTException("index must be built");
        }
        if (!index.canRead()) {
            throw new CTException("cannot read index");
        }
        currentPluginName = pluginName;
        if (indexData.isEmpty()) {
            indexData = Files.readAllLines(index.toPath(), StandardCharsets.UTF_8);
        }
        int nextFile = 0;
        if (!visitIndex.exists()) {
            visitIndex.createNewFile();
            Files.writeString(visitIndex.toPath(), String.format("%s;%d", currentPluginName, nextFile), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } else {
            // previous visit was stopped so continue 
            String visitLine = Files.readString(visitIndex.toPath());
            String prevPlugin = getLastPlugin(visitLine);
            if (!prevPlugin.isEmpty() && !prevPlugin.equalsIgnoreCase(currentPluginName)) {
                throw new CTException("skip this plugin because the previous stopped was " + prevPlugin);
            }
            nextFile = getLastIndex(visitLine);
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
        int nextFile = getLastIndex(Files.readString(visitIndex.toPath())) + 1;
        if (nextFile == indexData.size()) {
            reset();
            return null;
        }
        Files.writeString(visitIndex.toPath(), String.format("%s;%d", currentPluginName, nextFile), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        return new File(indexData.get(nextFile));
    }

    public synchronized void reset() throws CTException {
        if (!index.exists()) {
            throw new CTException("index must be built");
        }
        currentPluginName = "";
        visitIndex.delete();
    }

    private synchronized int getLastIndex(String visitLine) {
        String[] splitted = visitLine.split(";");
        if (splitted.length < 2) {
            return 0;
        }
        return Integer.parseInt(splitted[1]);
    }

    private synchronized String getLastPlugin(String visitLine) {
        String[] splitted = visitLine.split(";");
        if (splitted.length < 2) {
            return "";
        }
        return splitted[0];
    }
}
