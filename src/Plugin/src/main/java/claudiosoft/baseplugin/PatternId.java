package claudiosoft.baseplugin;

/**
 *
 * @author claudio.tortorelli
 */
public enum PatternId {

    p01("AAAA_MM_X"),
    p02("AAAA"),
    p03("AAAA"),
    p04("AAAA_MM_DD_X"),
    p05("AAAA_MM_DD_X"),
    p06("AAAA_MM_DD"),
    p07("AAAA_MM_X"),
    p08("AAAA_X"),
    p09("X_AAAA"),
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
