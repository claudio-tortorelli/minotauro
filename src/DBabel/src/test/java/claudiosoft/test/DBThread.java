package claudiosoft.test;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTException;
import claudiosoft.dbabel.Condition;
import claudiosoft.dbabel.DBabel;
import claudiosoft.dbabel.SchemaUtils;
import claudiosoft.dbabel.Table;
import claudiosoft.dbabel.TableData;
import claudiosoft.dbabel.TableRow;
import claudiosoft.utils.BasicUtils;
import java.util.Random;

/**
 *
 * @author claudio.tortorelli
 */
public class DBThread implements Runnable {

    protected int id;
    protected BasicLogger logger;
    protected DBabel db;

    public DBThread(int id, DBabel db) throws CTException {
        this.id = id;
        this.db = db;
        this.logger = BasicLogger.get();
    }

    @Override
    public void run() {
        try {
            Random rnd = new Random();

            BasicUtils.sleepRandom(500);
            logger.info(logThreadMessage(String.format("processing %d", id)));
            db.insert(Table.CONFIG, "testProject", SchemaUtils.getSchemaVersion());

            BasicUtils.sleepRandom(500);
            Condition condition = new Condition("project", "=", "testProject");
            String[] values = {"testProject", String.format("%06d", rnd.nextInt())};
            db.update(Table.CONFIG, values, condition);

            BasicUtils.sleepRandom(500);
            TableData res = db.select(Table.CONFIG, condition);
            for (TableRow tr : res.getData()) {
                BasicLogger.get().info("dbVersion %s".formatted(tr.getString("dbVersion")));
            }

            BasicUtils.sleepRandom(500);
            db.delete(Table.CONFIG, condition);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        logger.info(logThreadMessage(String.format("end %d", id)));
    }

    protected String logThreadMessage(String msg) {
        return String.format("- %s - %s", id, msg);
    }

}
