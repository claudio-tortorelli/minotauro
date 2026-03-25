package claudiosoft.dbabel.entity;

import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;
import claudiosoft.dbabel.Table;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author claudio.tortorelli
 */
public class Entity {

    protected static final String INSERT = "INSERT INTO ? (%s) VALUES (?, ?, ?, ?, ?, ?, ?)";
    protected static final String SELECT = "SELECT * FROM ? WHERE ? ? ?";
    protected static final String UPDATE = "UPDATE ? SET %s WHERE ? ? ?";
    protected static final String DELETE = "DELETE FROM ? WHERE ? ? ?;";

    protected String name;
    protected String insertFields;
    protected String updateFields;

    public Entity(Table table) {
        this(table.getDbName(), toInsertFields(table.getFields()), toUpdateFields(table.getFields()));
    }

    public Entity(String name, String insertFields, String updateFields) {
        this.name = name;
        this.insertFields = insertFields;
        this.updateFields = updateFields;
    }

    public void insert(Connection dbConnection, String[] values) throws CTException {

        if (values.length != insertFields.length()) {
            throw new CTException("incoherent number of values for table %s".formatted(name), CTError.DB_INSERT);
        }

        PreparedStatement ps = null;
        try {
            if (dbConnection.isClosed()) {
                throw new CTException("connection to DB is closed".formatted(name), CTError.DB_STATE);
            }
            if (dbConnection.isReadOnly()) {
                throw new CTException("DB is read only: unable to update".formatted(name), CTError.DB_STATE);
            }
            ps = dbConnection.prepareStatement(INSERT.formatted(insertFields));
            ps.setString(1, name);
            for (int iField = 1; iField < insertFields.length(); iField++) {
                ps.setString(iField, values[iField - 1]);
            }
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new CTException(ex, CTError.DB_INSERT);
        } finally {
            closeQuietly(ps);
        }
    }

    public ResultSet select(Connection dbConnection, Condition condition) throws CTException {
        PreparedStatement ps = null;
        try {
            if (dbConnection.isClosed()) {
                throw new CTException("connection to DB is closed".formatted(name), CTError.DB_STATE);
            }
            ps = dbConnection.prepareStatement(SELECT);
            ps.setString(1, name);
            ps.setString(2, condition.getField());
            ps.setString(3, condition.getOperator());
            ps.setString(4, condition.getValue());
            if (ps.executeUpdate() > 0) {
                return ps.getResultSet();
            }
            return null;
        } catch (SQLException ex) {
            throw new CTException(ex, CTError.DB_SELECT);
        } finally {
            closeQuietly(ps);
        }
    }

    public void update(Connection dbConnection, String[] values) throws CTException {

        if (values.length != updateFields.length()) {
            throw new CTException("incoherent number of values for table %s".formatted(name), CTError.DB_UPDATE);
        }

        PreparedStatement ps = null;
        try {
            if (dbConnection.isClosed()) {
                throw new CTException("connection to DB is closed".formatted(name), CTError.DB_STATE);
            }
            if (dbConnection.isReadOnly()) {
                throw new CTException("DB is read only: unable to update".formatted(name), CTError.DB_STATE);
            }
            ps = dbConnection.prepareStatement(UPDATE.formatted(updateFields));
            ps.setString(1, name);
            for (int iField = 1; iField < updateFields.length(); iField++) {
                ps.setString(iField, values[iField - 1]);
            }
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new CTException(ex, CTError.DB_UPDATE);
        } finally {
            closeQuietly(ps);
        }
    }

    public void delete(Connection dbConnection, Condition condition) throws CTException {

        PreparedStatement ps = null;
        try {
            if (dbConnection.isClosed()) {
                throw new CTException("connection to DB is closed".formatted(name), CTError.DB_STATE);
            }
            ps = dbConnection.prepareStatement(DELETE);
            ps.setString(1, name);
            ps.setString(2, condition.getField());
            ps.setString(3, condition.getOperator());
            ps.setString(4, condition.getValue());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new CTException(ex, CTError.DB_DELETE);
        } finally {
            closeQuietly(ps);
        }
    }

    protected void closeQuietly(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {

        }
    }

    private static String toInsertFields(String[] fields) {
        String ret = "";
        //"path, name, year, month, day, elaborated, descriptionRef", 
        for (String field : fields) {
            ret += "%s, ".formatted(field);
        }
        ret = ret.trim();
        ret = ret.substring(0, ret.length() - 1);
        return ret;
    }

    private static String toUpdateFields(String[] fields) {
        String ret = "";
        //"path = ?, name = ?, year = ?, month = ?, day = ?, elaborated = ?, descriptionRef = ?"
        for (String field : fields) {
            ret += "%s = ?, ".formatted(field);
        }
        ret = ret.trim();
        ret = ret.substring(0, ret.length() - 1);
        return ret;
    }
}
