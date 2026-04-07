package claudiosoft.dbabel;

import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;
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

    protected static final String INSERT = "INSERT INTO %s (%s) VALUES (%s)";
    protected static final String SELECT = "SELECT * FROM %s WHERE %s %s %s";
    protected static final String UPDATE = "UPDATE %s SET %s WHERE %s %s %s";
    protected static final String DELETE = "DELETE FROM %s WHERE %s %s %s;";

    protected String name;
    protected String insertFields;
    protected String updateFields;

    public Entity(Table table) {
        this(table.getName(), toInsertFields(table.getFields()), toUpdateFields(table.getFields()));
    }

    private Entity(String name, String insertFields, String updateFields) {
        this.name = name;
        this.insertFields = insertFields;
        this.updateFields = updateFields;
    }

    public void insert(Connection dbConnection, String[] values) throws CTException {

        if (values.length != insertFields.split(",").length) {
            throw new CTException("incoherent number of values for table %s".formatted(name), CTError.DB_INSERT);
        }

        PreparedStatement ps = null;
        try {
            if (dbConnection.isClosed()) {
                throw new CTException("connection to DB is closed".formatted(name), CTError.DB_STATUS);
            }
            if (dbConnection.isReadOnly()) {
                throw new CTException("DB is read only: unable to update".formatted(name), CTError.DB_STATUS);
            }
            ps = dbConnection.prepareStatement(INSERT.formatted(name, insertFields, toInsertValues(values)));
            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new CTException(ex, CTError.DB_INSERT);
        } finally {
            closeQuietly(ps);
        }
    }

    public TableData select(Connection dbConnection, Condition condition) throws CTException {
        PreparedStatement ps = null;
        try {
            if (dbConnection.isClosed()) {
                throw new CTException("connection to DB is closed".formatted(name), CTError.DB_STATUS);
            }
            ps = dbConnection.prepareStatement(SELECT.formatted(name, condition.getField(), condition.getOperator(), condition.getValue()));
            ResultSet res = ps.executeQuery();

            Table table = Table.valueOf(name);
            TableData td = new TableData();
            while (res.next()) {
                TableRow tr = new TableRow();
                for (String rawField : table.getRawFields()) {
                    String col = SchemaUtils.getFieldName(rawField);
                    DataType dt = SchemaUtils.getFieldType(rawField);
                    tr.addField(col, res.getObject(col), dt);
                }
                td.addRow(tr);
            }
            return td;
        } catch (SQLException ex) {
            throw new CTException(ex, CTError.DB_SELECT);
        } finally {
            closeQuietly(ps);
        }
    }

    public void update(Connection dbConnection, String[] values, Condition condition) throws CTException {
        update(dbConnection, updateFields.replace(" = '%s'", "").trim().split(","), values, condition);
    }

    public void update(Connection dbConnection, String[] fields, String[] values, Condition condition) throws CTException {
        if (values.length != fields.length) {
            throw new CTException("incoherent number of values for table %s".formatted(name), CTError.DB_UPDATE);
        }

        PreparedStatement ps = null;
        try {
            if (dbConnection.isClosed()) {
                throw new CTException("connection to DB is closed".formatted(name), CTError.DB_STATUS);
            }
            if (dbConnection.isReadOnly()) {
                throw new CTException("DB is read only: unable to update".formatted(name), CTError.DB_STATUS);
            }
            String set = toUpdateFields(fields).formatted(values);
            ps = dbConnection.prepareStatement(UPDATE.formatted(name, set, condition.getField(), condition.getOperator(), condition.getValue()));
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
                throw new CTException("connection to DB is closed".formatted(name), CTError.DB_STATUS);
            }
            ps = dbConnection.prepareStatement(DELETE.formatted(name, condition.getField(), condition.getOperator(), condition.getValue()));
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

    private static String toInsertValues(String[] values) {
        String ret = "";
        for (String val : values) {
            ret += "'%s', ".formatted(val);
        }
        ret = ret.trim();
        ret = ret.substring(0, ret.length() - 1);
        return ret;
    }

    private static String toUpdateFields(String[] fields) {
        String ret = "";
        //"path = ?, name = ?, year = ?, month = ?, day = ?, elaborated = ?, descriptionRef = ?"
        for (String field : fields) {
            ret += "%s = '%%s', ".formatted(field);
        }
        ret = ret.trim();
        ret = ret.substring(0, ret.length() - 1);
        return ret;
    }

    private static String toUpdateValues(String[] values) {
        String ret = "";
        for (String val : values) {
            ret += "'%s', ".formatted(val);
        }
        ret = ret.trim();
        ret = ret.substring(0, ret.length() - 1);
        return ret;
    }
}
