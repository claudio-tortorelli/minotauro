package claudiosoft.dbabel;

import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;
import java.util.LinkedList;

/**
 *
 * @author claudio.tortorelli
 */
public class TableRow {

    private LinkedList<String> column;
    private LinkedList<Object> data;
    private LinkedList<DataType> dataType;

    public TableRow() {
        column = new LinkedList<>();
        data = new LinkedList<>();
        dataType = new LinkedList<>();
    }

    public void addField(String column, Object data, DataType dt) {
        this.column.add(column);
        this.data.add(data);
        this.dataType.add(dt);
    }

    public int getInt(String column) throws CTException {
        int colIndex = this.column.indexOf(column);
        if (colIndex < 0) {
            throw new CTException("invalid column name %s".formatted(column), CTError.DB_ACCESS);
        }
        if (this.dataType.get(colIndex) != DataType.INTEGER) {
            throw new CTException("column %s not type int".formatted(column), CTError.DB_ACCESS);
        }
        return Integer.parseInt("%s".formatted(this.data.get(colIndex)));
    }

    public String getString(String column) throws CTException {
        int colIndex = this.column.indexOf(column);
        if (colIndex < 0) {
            throw new CTException("invalid column name %s".formatted(column), CTError.DB_ACCESS);
        }
        if (this.dataType.get(colIndex) != DataType.TEXT) {
            throw new CTException("column %s not type string".formatted(column), CTError.DB_ACCESS);
        }
        return (String) this.data.get(colIndex);
    }

    public byte[] getByte(String column) throws CTException {
        int colIndex = this.column.indexOf(column);
        if (colIndex < 0) {
            throw new CTException("invalid column name %s".formatted(column), CTError.DB_ACCESS);
        }
        if (this.dataType.get(colIndex) != DataType.BLOB) {
            throw new CTException("column %s not type blob".formatted(column), CTError.DB_ACCESS);
        }
        return ((String) this.data.get(colIndex)).getBytes();
    }

}
