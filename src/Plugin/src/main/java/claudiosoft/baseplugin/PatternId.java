package claudiosoft.baseplugin;

/**
 *
 * @author claudio.tortorelli
 */
public enum PatternId {

    //1 2002 04.20-1 bla
    //2 2002 04.20 bla
    //3 2002.05.20
    //4 2002.05.20-bla
    //5 2002.05.20 bla
    //6 2002.05 bla
    //7 bla 10-02-2002 
    //8 10_02_2002 bla
    //9 2002-02-20
    //10 2002-02-20 bla
    //11 2002 04 bla
    //12 bla 2002
    //13 19xx
    //14 2002
    //15 2002 bla
    p01("YYYY_MM_DD_X"),
    p02("YYYY_MM_DD_X"),
    p03("YYYY_MM_DD"),
    p04("YYYY_MM_DD_X"),
    p05("YYYY_MM_DD_X"),
    p06("YYYY_MM_X"),
    p07("X_DD_MM_YYYY"),
    p08("DD_MM_YYYY_X"),
    p09("YYYY_MM_DD"),
    p10("YYYY_MM_DD_X"),
    p11("YYYY_MM_X"),
    p12("X_YYYY"),
    p13("YYYY"),
    p14("YYYY"),
    p15("YYYY_X");

    private final String schema;

    private PatternId(String schema) {
        this.schema = schema;
    }

    public String getSchema() {
        return schema;
    }

}
