package claudiosoft.baseplugin;

/**
 *
 * @author claudio.tortorelli
 */
public enum PatternId {

    p1("AAAA_MM_X"),
    p2("AAAA"),
    p3("AAAA"),
    p4("AAAA_MM_DD_X"),
    p5("AAAA_MM_DD_X"),
    p6("AAAA_MM_DD"),
    p7("AAAA_MM_X"),
    p8("AAAA_X"),
    p9("X_AAAA"),
    p10("X_DD_MM_AAAA"),
    p11("DD_MM_AAAA_X"),
    p12("AAAA_MM_DD"),
    p13("AAAA_MM_DD_X");

    private String schema;

    private PatternId(String schema) {
        this.schema = schema;
    }

    public String getSchema() {
        return schema;
    }

}
