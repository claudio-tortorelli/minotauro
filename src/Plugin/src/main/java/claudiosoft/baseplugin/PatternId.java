package claudiosoft.baseplugin;

/**
 *
 * @author claudio.tortorelli
 */
public enum PatternId {

    //1 2002 04.20-1 bla
    //2 2002 04.20 bla
    //3 2002.05.20
    //4 2002.05.20 bla
    //5 2002.05 bla
    //6 bla 10-02-2002 
    //7 10_02_2002 bla
    //8 2002-02-20
    //9 2002-02-20 bla
    //10 2002 04 bla
    //11 bla 2002
    //12 19xx
    //13 2002
    //14 2002 bla
    p01("YYYY_MM_DD_X"),
    p02("YYYY_MM_DD_X"),
    p03("YYYY_MM_DD"),
    p04("YYYY_MM_DD_X"),
    p05("YYYY_MM_X"),
    p06("X_DD_MM_YYYY"),
    p07("DD_MM_YYYY_X"),
    p08("YYYY_MM_DD"),
    p09("YYYY_MM_DD_X"),
    p10("YYYY_MM_X"),
    p11("X_YYYY"),
    p12("YYYY"),
    p13("YYYY"),
    p14("YYYY_X");

    private String schema;

    private PatternId(String schema) {
        this.schema = schema;
    }

    public String getSchema() {
        return schema;
    }

}
