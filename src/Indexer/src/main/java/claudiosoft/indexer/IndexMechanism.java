package claudiosoft.indexer;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTError;
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
 * Abstract class to implements common index mechanism handler methods
 *
 * @author claudio.tortorelli
 */
public abstract class IndexMechanism implements IndexInterface {

    protected String currentPluginName;

    protected File root;
    protected File index;
    protected File folderIndex;
    protected File tempIndex;
    protected PathMatcher matcher;
    protected List<String> indexData;
    protected File visitIndex;
    protected HashMap<String, Integer> extensions;
    protected BasicLogger logger;
    protected List<String> folders;
    protected String globFilter;
    protected boolean forcedBuild;
    protected boolean recursive;

    protected boolean initialized = false;

    @Override
    public void init(File root, File index, File folderIndex) throws CTException {
        init(root, index, folderIndex, "*.*");
    }

    @Override
    public void init(File root, File index, File folderIndex, String globFilter) throws CTException {
        this.root = root;
        this.index = index;
        this.folderIndex = folderIndex;
        if (!root.exists()) {
            throw new CTException("root folder not exists", CTError.FILESYSTEM_GENERIC_ERROR);
        }
        if (!root.canRead()) {
            throw new CTException("cannot access to root folder", CTError.FILESYSTEM_GENERIC_ERROR);
        }
        String tmpPath = String.format("%s.tmp", index.getAbsolutePath());
        tempIndex = new File(tmpPath);
        if (tempIndex.exists()) {
            this.index.delete(); // remove incomplete index
            this.folderIndex.delete(); // remove incomplete index
        }
        String visitPath = String.format("%s.next", index.getAbsolutePath());
        visitIndex = new File(visitPath);

        this.globFilter = globFilter.toLowerCase();
        matcher = FileSystems.getDefault().getPathMatcher("glob:" + this.globFilter);
        indexData = new LinkedList<>();
        extensions = new HashMap<>();
        folders = new LinkedList<>();

        logger = BasicLogger.get();
        forcedBuild = false;
        recursive = true;
        initialized = true;
    }

    @Override
    public List<String> getExtensions() {
        List<String> exts = new LinkedList<>();
        for (String ext : extensions.keySet()) {
            exts.add(ext);
        }
        return exts;
    }

    @Override
    public List<String> getFolders() throws IOException {
        if (folders.isEmpty()) {
            folders = Files.readAllLines(folderIndex.toPath());
        }
        return folders;
    }

    @Override
    public synchronized void buildIndex() throws IOException, CTException {
        if (!initialized) {
            throw new CTException("indexer not initialized", CTError.INDEXER_GENERIC);
        }

        if (forcedBuild) {
            logger.info("index rebuilding is forced");
            index.delete();
            folderIndex.delete();
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
            folderIndex.createNewFile();

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

    public boolean isForcedBuild() {
        return forcedBuild;
    }

    public void setForcedBuild(boolean forcedBuild) {
        this.forcedBuild = forcedBuild;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    @Override
    public synchronized File startVisit(String pluginName) throws CTException, IOException {
        if (!index.exists()) {
            throw new CTException("index must be built", CTError.INDEXER_GENERIC);
        }
        if (!index.canRead()) {
            throw new CTException("cannot read index", CTError.IO_GENERIC_ERROR);
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
                throw new CTException("skip this plugin because the previous stopped was " + prevPlugin, CTError.INDEXER_GENERIC);
            }
            nextFile = getLastIndex(visitLine);
        }
        return new File(indexData.get(nextFile));
    }

    @Override
    public synchronized File visitNext() throws CTException, IOException {
        if (!index.exists()) {
            throw new CTException("index must be built", CTError.INDEXER_GENERIC);
        }
        if (!index.canRead()) {
            throw new CTException("cannot read index", CTError.IO_GENERIC_ERROR);
        }
        if (indexData.isEmpty() || !visitIndex.exists()) {
            throw new CTException("visit must be started", CTError.INDEXER_GENERIC);
        }
        int nextFile = getLastIndex(Files.readString(visitIndex.toPath())) + 1;
        if (nextFile == indexData.size()) {
            resetIndex();
            return null;
        }
        Files.writeString(visitIndex.toPath(), String.format("%s;%d", currentPluginName, nextFile), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        return new File(indexData.get(nextFile));
    }

    @Override
    public synchronized String getVisitIndex() throws IOException {
        if (!visitIndex.exists()) {
            return "";
        }
        String last = Files.readString(visitIndex.toPath());
        String[] split = last.split(";");
        return String.format("%d/%d", Integer.parseInt(split[1]) + 1, indexData.size());
    }

    @Override
    public synchronized void resetIndex() throws CTException {
        if (!index.exists()) {
            throw new CTException("index must be built", CTError.INDEXER_GENERIC);
        }
        currentPluginName = "";
        visitIndex.delete();
    }

    protected synchronized int countIndexEntries() throws IOException {
        if (!index.exists()) {
            return 0;
        }
        return Files.readAllLines(index.toPath()).size();
    }

    protected synchronized int getLastIndex(String visitLine) {
        String[] splitted = visitLine.split(";");
        if (splitted.length < 2) {
            return 0;
        }
        return Integer.parseInt(splitted[1]);
    }

    protected synchronized String getLastPlugin(String visitLine) {
        String[] splitted = visitLine.split(";");
        if (splitted.length < 2) {
            return "";
        }
        return splitted[0];
    }

    protected void addFolder(File folder, boolean recursive) throws IOException {
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
                Files.write(folderIndex.toPath(), String.format("%s\n", folder.getCanonicalPath()).getBytes(), StandardOpenOption.APPEND);
            }
        }
    }
}
