package claudiosoft.test;


import claudiosoft.commons.CTException;
import claudiosoft.indexer.IndexFactory;
import claudiosoft.indexer.IndexMechanism;
import claudiosoft.indexer.IndexMechanismType;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author claudio.tortorelli
 */
public class TestIndex {

    public static void main(String[] args) throws CTException, IOException {

        IndexMechanism indexer = IndexFactory.get(IndexMechanismType.BASIC);
        indexer.init(new File("../../"), new File("./target/index.txt"), new File("./target/folders.txt"));

        new File("./target/index.txt").delete();
        indexer.buildIndex();

        new File("./target/index2.txt").delete();
        indexer.init(new File("../../"), new File("./target/index2.txt"), new File("./target/folders.txt"), "*.{jpg,jar}");
        indexer.buildIndex();

        for (String folder : indexer.getFolders()) {
            System.out.println(folder);
        }

        new File("./target/index3.txt").delete();
        indexer.init(new File("../../"), new File("./target/index3.txt"), new File("./target/folders.txt"));
        indexer.setForcedBuild(false);
        indexer.buildIndex();
        try {
            indexer.visitNext();
        } catch (CTException ex) {
            System.err.println(ex.getMessage());
        }
        File nextFile = indexer.startVisit("plugin");
        while (nextFile != null) {
            System.out.println(indexer.getVisitIndex() + " - " + nextFile.getAbsolutePath());
            nextFile = indexer.visitNext();
        }
    }
}
