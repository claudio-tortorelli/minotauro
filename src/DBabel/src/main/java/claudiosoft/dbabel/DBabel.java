package claudiosoft.dbabel;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author claudio.tortorelli
 */
public class DBabel {

    private BasicLogger logger;
    private Connection dbConnection;

    public DBabel(String sqliteDbFilePath) throws CTException {

        logger = BasicLogger.get();

        try {
            logger.info(String.format("open db %s", sqliteDbFilePath));
            Class.forName("org.sqlite.JDBC");
            String connectStr = String.format("jdbc:sqlite:%s", new File(sqliteDbFilePath).getCanonicalPath());
            dbConnection = DriverManager.getConnection(connectStr);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new CTException(ex.getMessage(), ex, CTError.DB_OPEN);
        } finally {
            logger.info("finished\n");
        }
    }

    public void close() throws SQLException {
        if (dbConnection != null) {
            dbConnection.close();
        }
    }

    private static final String INSERT_SQL = "INSERT INTO Remuneraciones(Nombre, Apellido, Rut, Edad, Tiempo, Sueldo) VALUES(?, ?, ?, ?, ?, ?)";

//    public synchronized void insert(String firstName, String lastName, String id, int age, int timeInHours, int salary) throws CTException {
//
//        PreparedStatement ps = null;
//        try {
//            ps = dbConnection.prepareStatement(INSERT_SQL);
//            ps.setString(1, firstName);
//            ps.setString(2, lastName);
//            ps.setString(3, id);
//            ps.setInt(4, timeInHours);
//            ps.setInt(5, age);  // You'll have to update this each and every year. BirthDate would be better.
//            ps.setInt(6, salary);
//            ps.executeUpdate();
//
//        } catch (SQLException ex) {
//            logger.error(ex.getMessage(), ex);
//            throw new CTException(ex, CTError.DB_OPEN);
//        } finally {
//            closeQuietly(ps);
//        }
//    }
    public synchronized void insertOrUpdate() throws CTException {

        PreparedStatement ps = null;
        try {
            ps = dbConnection.prepareStatement(INSERT_SQL);
            // todo
            /**
             * prima verifica le immagini presenti tramite imageId poi inserisce
             * quelle non presenti update di quelle presenti
             *
             * vedi anche upsert
             */
            ps.executeUpdate();

        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            throw new CTException(ex, CTError.DB_INSERT);
        } finally {
            closeQuietly(ps);
        }
    }

    private void closeQuietly(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {

        }
    }

}
