package claudiosoft.dbabel;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;
import claudiosoft.dbabel.entity.Condition;
import claudiosoft.dbabel.entity.Entity;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author claudio.tortorelli
 */
public class DBabel {

    private BasicLogger logger;
    private Connection dbConnection;

    public DBabel(File sqliteDbFile) throws CTException {
        this(sqliteDbFile.getAbsolutePath());
    }

    public DBabel(String sqliteDbFilePath) throws CTException {

        logger = BasicLogger.get();

        try {
            logger.debug(String.format("open db %s", sqliteDbFilePath));
            Class.forName("org.sqlite.JDBC");
            String connectStr = String.format("jdbc:sqlite:%s", new File(sqliteDbFilePath).getCanonicalPath());
            dbConnection = DriverManager.getConnection(connectStr);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new CTException(ex.getMessage(), ex, CTError.DB_OPEN);
        } finally {
            logger.debug("db is ready");
        }
    }

    public boolean isOpen() throws CTException {
        try {
            return dbConnection != null && dbConnection.isValid(0);
        } catch (SQLException ex) {
            throw new CTException(ex, CTError.DB_STATUS);
        }
    }

    public void close() throws SQLException {
        if (dbConnection != null) {
            dbConnection.close();
            dbConnection = null;
            logger.debug("db closed");
        }
    }

    public synchronized void insert(Table table, String... values) throws CTException {
        new Entity(table).insert(dbConnection, values);
    }

    public synchronized ResultSet select(Table table, Condition condition) throws CTException {
        return new Entity(table).select(dbConnection, condition);
    }

}
