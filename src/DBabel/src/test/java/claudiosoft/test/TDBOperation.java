
import claudiosoft.test.BaseJUnitTest;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author Claudio
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TDBOperation extends BaseJUnitTest {

    public TDBOperation() {
        super(false, false);
    }

    @Test
    public void t01OpenDB() {
//        File dbFile = new File("./dbschema-%s".formatted(SchemaUtils.getSchemaVersion()));
//        Assert.assertTrue(dbFile.exists());
    }

}
