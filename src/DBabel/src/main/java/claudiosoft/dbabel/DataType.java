package claudiosoft.dbabel;

/**
 *
 * @author claudio.tortorelli
 */
public enum DataType {
    INTEGER("I_"),
    TEXT("T_"),
    BLOB("B_"),
    UNKNOWN("!");

    private String id;

    private DataType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
