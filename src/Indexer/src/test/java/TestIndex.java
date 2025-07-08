
import claudiosoft.commons.CTException;
import claudiosoft.indexer.Indexer;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author claudio.tortorelli
 */
public class TestIndex {

    public static void main(String[] args) throws CTException, IOException {
        new File("./target/index.txt").delete();
        Indexer indexer = new Indexer(new File("../../"), new File("./target/index.txt"), new File("./target/folders.txt"));
        indexer.buildIndex();

        new File("./target/index2.txt").delete();
        Indexer indexer2 = new Indexer(new File("../../"), new File("./target/index2.txt"), new File("./target/folders.txt"), "*.{jpg,jar}");
        indexer2.buildIndex();

        for (String folder : indexer2.getFolders()) {
            System.out.println(folder);
        }

        new File("./target/index3.txt").delete();
        Indexer indexer3 = new Indexer(new File("../../"), new File("./target/index3.txt"), new File("./target/folders.txt"));
        indexer3.buildIndex(false);

        try {
            indexer2.visitNext();
        } catch (CTException ex) {
            System.err.println(ex.getMessage());
        }
        File nextFile = indexer2.startVisit("plugin");
        while (nextFile != null) {
            System.out.println(indexer2.getVisitIndex() + " - " + nextFile.getAbsolutePath());
            nextFile = indexer2.visitNext();
        }
    }
}
