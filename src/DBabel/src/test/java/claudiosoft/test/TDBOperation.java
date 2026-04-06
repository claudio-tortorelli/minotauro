
import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Constants;
import claudiosoft.dbabel.Condition;
import claudiosoft.dbabel.DBabel;
import claudiosoft.dbabel.SchemaUtils;
import claudiosoft.dbabel.Table;
import claudiosoft.dbabel.TableData;
import claudiosoft.dbabel.TableRow;
import claudiosoft.test.BaseJUnitTest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author Claudio
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TDBOperation extends BaseJUnitTest {

    protected static DBabel db;

    public TDBOperation() throws CTException {
        super(false, false);
        BasicLogger.get(BasicLogger.LogLevel.DEBUG, Constants.LOGGER_NAME, new File("./target/test-output/dbTest.log"));
    }

    @Test
    public void t01OpenDB() throws CTException, IOException {

        File dbFileOrig = new File("../../base_db/babel.db");
        File dbFile = new File("./target/test-output/dbTest.db");
        Files.copy(dbFileOrig.toPath(), dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        db = new DBabel(dbFile.getAbsolutePath());
        Assert.assertTrue(db.isOpen());
    }

    @Test
    public void t02InsertConfig() throws CTException, SQLException {
        db.insert(Table.CONFIG, "testProject", SchemaUtils.getSchemaVersion());
        BasicLogger.get().debug("inserted");
    }

    @Test
    public void t03SelectConfig() throws CTException, SQLException {
        Condition condition = new Condition("project", "=", "testProject");
        TableData res = db.select(Table.CONFIG, condition);
        Assert.assertTrue(res.getRows() > 0);

        for (TableRow tr : res.getData()) {
            BasicLogger.get().info("project found is " + tr.getString("project"));
            try {
                tr.getInt("dbVersion");
            } catch (CTException ex) {
                BasicLogger.get().error(ex.getMessage());
            }
        }

        Condition falseCondition = new Condition("project", "=", "pippo");
        res = db.select(Table.CONFIG, falseCondition);
        Assert.assertTrue(res.getRows() == 0);
    }

    @Test
    public void t09MultiThreadInsert() throws CTException {
    }

    @Test
    public void t99Close() throws CTException {
        if (db.isOpen()) {
            db.close();
        }
    }

}
