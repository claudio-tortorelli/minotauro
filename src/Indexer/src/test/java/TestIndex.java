
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
        Indexer indexer = new Indexer(new File("../../"), new File("./target/index.txt"));
        indexer.buildIndex();

        Indexer indexer2 = new Indexer(new File("../../"), new File("./target/index2.txt"), "*.{jpg,jar}");
        indexer2.buildIndex();

        Indexer indexer3 = new Indexer(new File("../../"), new File("./target/index3.txt"));
        indexer3.buildIndex(false);

        File nextFile = indexer2.visitNext();
        while (nextFile != null) {
            System.out.println(nextFile.getAbsolutePath());
            nextFile = indexer2.visitNext();
        }
    }
}
