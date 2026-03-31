
import claudiosoft.commons.CTException;
import claudiosoft.dbabel.DBabel;
import claudiosoft.dbabel.SchemaUtils;
import claudiosoft.dbabel.Table;
import claudiosoft.dbabel.entity.Condition;
import claudiosoft.test.BaseJUnitTest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
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

    private File dbFile;

    public TDBOperation() {
        super(false, false);
        try {
            File dbFileOrig = new File("../../base_db/babel.db");
            dbFile = new File("./target/test-output/dbTest.db");
            Files.copy(dbFileOrig.toPath(), dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void t01OpenDB() throws CTException {
        DBabel db = new DBabel(dbFile.getAbsolutePath());
        Assert.assertTrue(db.isOpen());
    }

    @Test
    public void t02InsertConfig() throws CTException {
        DBabel db = new DBabel(dbFile);
        Assert.assertTrue(db.isOpen());
        db.insert(Table.CONFIG, "testProject", SchemaUtils.getSchemaVersion());
    }

    @Test
    public void t03SelectConfig() throws CTException, SQLException {
        DBabel db = new DBabel(dbFile);
        Assert.assertTrue(db.isOpen());

        Condition condition = new Condition("project", "=", "testProject");
        ResultSet res = db.select(Table.CONFIG, condition);
        Assert.assertTrue(res != null);
        while (res.next()) {
            System.out.println("--> " + res.getString("project"));
        }

        Condition falseCondition = new Condition("project", "=", "pippo");
        res = db.select(Table.CONFIG, falseCondition);
        Assert.assertTrue(res != null);
        while (res.next()) {
            System.out.println(res.getString("project"));
        }
    }

    @Test
    public void t09MultiThreadInsert() throws CTException {
        DBabel db = new DBabel(dbFile);
        Assert.assertTrue(db.isOpen());
    }

}
