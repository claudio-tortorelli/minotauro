package claudiosoft.test;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTException;
import claudiosoft.dbabel.Condition;
import claudiosoft.dbabel.DBabel;
import claudiosoft.dbabel.Table;
import claudiosoft.dbabel.TableData;
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
            logger.info(logThreadMessage(String.format("processing %d", id)));

            for (int iOP = 0; iOP < 5; iOP++) {
                BasicUtils.sleepRandom(500);

                int type = rnd.nextInt(2);
                switch (type) {
                    case 0:
                        db.insert(Table.TEST, "%d".formatted(rnd.nextInt(10000)), "test", "");
                        logger.info(logThreadMessage("(inserted)"));
                        break;
                    case 1:
                        TableData res = db.select(Table.TEST);
                        if (res.getRows() > 1) {
                            Condition condition = new Condition("id", "=", "%d".formatted(rnd.nextInt(res.getRows())));
                            String[] values = {"%d".formatted(rnd.nextInt(10000)), "test", ""};
                            db.update(Table.TEST, values, condition);
                            logger.info(logThreadMessage("(updated)"));
                        }
                        break;
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        logger.info(logThreadMessage(String.format("end %d", id)));
    }

    protected String logThreadMessage(String msg) {
        return String.format("- %s - %s", id, msg);
    }

}
