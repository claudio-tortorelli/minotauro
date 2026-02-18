package claudiosoft.dbabel;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author claudio.tortorelli
 */
public class DBabel {

    private BasicLogger logger;
    private Connection dbConnection;
    private Statement statement;

    public DBabel(String sqliteDbFilePath) throws CTException, SQLException {

        logger = BasicLogger.get();

        try {
            logger.info(String.format("open db %s", sqliteDbFilePath));
            Class.forName("org.sqlite.JDBC");
            String connectStr = String.format("jdbc:sqlite:%s", new File(sqliteDbFilePath).getCanonicalPath());
            dbConnection = DriverManager.getConnection(connectStr);
            statement = dbConnection.createStatement();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            close();
            throw new CTException(ex.getMessage(), ex, CTError.DB_OPEN);
        } finally {
            logger.info("finished\n");
        }
    }

    public void close() throws SQLException {
        if (statement != null) {
            statement.close();
        }
        if (dbConnection != null) {
            dbConnection.close();
        }
    }
}
