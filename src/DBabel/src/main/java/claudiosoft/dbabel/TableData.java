package claudiosoft.dbabel;

import java.util.LinkedList;

/**
 *
 * @author claudio.tortorelli
 */
public class TableData {

    private LinkedList<TableRow> data;

    public TableData() {
        data = new LinkedList<>();
    }

    public void addRow(TableRow row) {
        data.add(row);
    }

    public final LinkedList<TableRow> getData() {
        return data;
    }

    public int getRows() {
        return data.size();
    }
}
