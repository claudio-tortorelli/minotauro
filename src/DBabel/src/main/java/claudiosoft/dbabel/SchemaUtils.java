package claudiosoft.dbabel;

/**
 *
 * @author claudio.tortorelli
 */
public class SchemaUtils {

    private static final String schemaVersion = "20260408";

    public static String getSchemaVersion() {
        return schemaVersion;
    }

    public static DataType getFieldType(String rawField) {
        for (DataType dt : DataType.values()) {
            if (rawField.startsWith(dt.getId())) {
                return dt;
            }
        }
        return DataType.UNKNOWN;
    }

    public static String getFieldName(String rawField) {
        for (DataType dt : DataType.values()) {
            if (dt == DataType.UNKNOWN) {
                continue;
            }
            rawField = rawField.replace(dt.getId(), "");
        }
        return rawField;
    }

    public static String printSchema() {
        String ret = "Schema ver. %s:\n".formatted(schemaVersion);
        ret += "  - - - - - - - \n";
        for (Table table : Table.values()) {
            ret += printTable(table);
            ret += "  - - - - - - - \n";
        }
        return ret;
    }

    private static String printTable(Table table) {
        String ret = "  Table %s:\n".formatted(table.getName());
        for (String rawField : table.getRawFields()) {
            ret += "        %s %s\n".formatted(getFieldName(rawField), getFieldType(rawField).name());
        }
        return ret;
    }
}
