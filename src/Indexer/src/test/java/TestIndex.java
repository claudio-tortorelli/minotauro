
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
        Indexer indexer = new Indexer(new File("../../"), new File("./target/index.txt"));
        indexer.buildIndex();
        for (String ext : indexer.getExtensions()) {
            System.out.println(ext);
        }

        new File("./target/index2.txt").delete();
        Indexer indexer2 = new Indexer(new File("../../"), new File("./target/index2.txt"), "*.{jpg,jar}");
        indexer2.buildIndex();

        new File("./target/index3.txt").delete();
        Indexer indexer3 = new Indexer(new File("../../"), new File("./target/index3.txt"));
        indexer3.buildIndex(false);

        try {
            indexer2.visitNext();
        } catch (CTException ex) {
            System.err.println(ex.getMessage());
        }
        File nextFile = indexer2.startVisit();
        while (nextFile != null) {
            System.out.println(indexer2.getVisitIndex() + " - " + nextFile.getAbsolutePath());
            nextFile = indexer2.visitNext();
        }
    }
}
