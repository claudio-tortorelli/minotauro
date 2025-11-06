package claudiosoft.indexer;

import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 *
 * @author claudio.tortorelli
 */
public class IndexUtils {

    public static void indexDiff(File index1, File index2, File indexDiff) throws CTException, IOException {
        if (index1 == null || !index1.exists()) {
            throw new CTException("missing first index", CTError.INVALID_ARGUMENT);
        }
        if (index2 == null || !index2.exists()) {
            throw new CTException("missing second index", CTError.INVALID_ARGUMENT);
        }
        if (indexDiff == null) {
            throw new CTException("missing diff index", CTError.INVALID_ARGUMENT);
        }

        //TODO: not so safe for memory usage
        List<String> indexData1 = Files.readAllLines(index1.toPath(), StandardCharsets.UTF_8);
        List<String> indexData2 = Files.readAllLines(index2.toPath(), StandardCharsets.UTF_8);
        if (indexDiff.exists()) {
            indexDiff.delete();
        }
        indexDiff.createNewFile();
        for (String data1 : indexData1) {
            if (!indexData2.contains(data1)) {
                Files.writeString(indexDiff.toPath(), data1, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            }
        }
        for (String data2 : indexData2) {
            if (!indexData1.contains(data2)) {
                Files.writeString(indexDiff.toPath(), data2, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            }
        }
    }

    public static void indexMerge(File index1, File index2, File indexMerge) throws CTException, IOException {
        if (index1 == null || !index1.exists()) {
            throw new CTException("missing first index", CTError.INVALID_ARGUMENT);
        }
        if (index2 == null || !index2.exists()) {
            throw new CTException("missing second index", CTError.INVALID_ARGUMENT);
        }
        if (indexMerge == null) {
            throw new CTException("missing diff index", CTError.INVALID_ARGUMENT);
        }
        List<String> indexData1 = Files.readAllLines(index1.toPath(), StandardCharsets.UTF_8);
        List<String> indexData2 = Files.readAllLines(index2.toPath(), StandardCharsets.UTF_8);
        if (indexMerge.exists()) {
            indexMerge.delete();
        }
        Files.copy(index1.toPath(), indexMerge.toPath());

        for (String data2 : indexData2) {
            if (!indexData1.contains(data2)) {
                Files.writeString(indexMerge.toPath(), data2, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            }
        }
    }
}
