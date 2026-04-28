
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
import claudiosoft.test.DBThread;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    protected static BasicLogger logger;

    public TDBOperation() throws CTException {
        super(false, false);
        logger = BasicLogger.get(BasicLogger.LogLevel.DEBUG, Constants.LOGGER_NAME, new File("./target/test-output/dbTest.log"));
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
    public void t02InsertData() throws CTException, SQLException {
        db.insert(Table.TEST, SchemaUtils.getSchemaVersion(), "text", "YmxvYg==");
        logger.debug("inserted");
    }

    @Test
    public void t03SelectData() throws CTException, SQLException {
        Condition condition = new Condition("dataInt", "=", SchemaUtils.getSchemaVersion());
        TableData res = db.select(Table.TEST, condition);
        Assert.assertTrue(res.getRows() > 0);

        for (TableRow tr : res.getData()) {
            logger.info("dataText has %s".formatted(tr.getString("dataText")));
            try {
                tr.getInt("dataText");
            } catch (CTException ex) {
                logger.error(ex.getMessage());
            }
            logger.info("dataInt %d".formatted(tr.getInt("dataInt")));
            logger.info("dataBlob %s".formatted(new String(tr.getByte("dataBlob"))));
        }

        Condition falseCondition = new Condition("dataInt", "=", "YmxvYg==");
        res = db.select(Table.TEST, falseCondition);
        Assert.assertTrue(res.getRows() == 0);
    }

    @Test
    public void t04UpdateData() throws CTException, SQLException {
        Condition condition = new Condition("dataInt", "=", SchemaUtils.getSchemaVersion());

        String[] values = {SchemaUtils.getSchemaVersion(), "textUpd", "jorge"};
        int nUpd = db.update(Table.TEST, values, condition);
        Assert.assertTrue(nUpd == 1);

        TableData res = db.select(Table.TEST, condition);
        for (TableRow tr : res.getData()) {
            Assert.assertTrue(tr.getString("dataText").equals("textUpd"));
        }
    }

    @Test
    public void t05DeleteData() throws CTException, SQLException {
        Condition condition = new Condition("dataInt", "=", SchemaUtils.getSchemaVersion());

        TableData res = db.select(Table.TEST, condition);
        Assert.assertTrue(res.getRows() == 1);

        int nDel = db.delete(Table.TEST, condition);
        Assert.assertTrue(nDel == 1);

        res = db.select(Table.TEST, condition);
        Assert.assertTrue(res.getRows() == 0);
    }

    @Test
    public void t09MultiThreadExec() throws CTException {

        int nThread = Runtime.getRuntime().availableProcessors() - 1;
        ExecutorService exec = Executors.newFixedThreadPool(nThread);
        try {
            List<CompletableFuture<?>> futures = new ArrayList<>();
            for (int iT = 0; iT < nThread; iT++) {
                DBThread thread = new DBThread(iT + 1, db);
                futures.add(CompletableFuture.runAsync(thread, exec));
            }
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        } finally {
            exec.shutdown();
        }
    }

    @Test
    public void t99Close() throws CTException {
        if (db.isOpen()) {
            db.close();
        }
    }

}
