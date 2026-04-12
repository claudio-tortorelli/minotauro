package claudiosoft.dbabel;

/**
 *
 * @author claudio.tortorelli
 */
public enum Table {

    ALBUM("ALBUM", "T_path", "T_name", "I_year", "I_month", "I_day", "I_elaborated", "I_descriptionRef"),
    CONFIG("CONFIG", "T_project", "T_dbVersion"),
    DESCRIPTION("DESCRIPTION", "T_text", "B_blob"),
    EVENT("EVENT", "T_name", "T_date", "I_verified", "I_descriptionRef", "I_albumRef"),
    EXIF("EXIF", "I_imgWidthPix", "I_imgHeightPix", "T_make", "T_model", "T_date", "T_orientation", "T_photographer", "T_latitude", "T_latitudeRef", "T_longitude", "T_longitudeRef"),
    FACE("FACE", "I_imageRef", "I_peopleRef"),
    IMAGE("IMAGE", "B_blob", "I_isThumb", "I_isFace", "I_wPix", "I_hPix"),
    PEOPLE("PEOPLE", "T_name", "T_surname", "T_birthDate", "I_verified", "I_descriptionRef", "T_albumRef"),
    PICTURE("PICTURE", "T_name", "T_type", "T_editDate", "I_sizeByte", "I_wPix", "I_hPix", "I_albumRef", "I_imageRef", "I_exifRef", "I_tagRef"),
    PLACE("PLACE", "T_name", "I_isCity", "I_isCountry", "T_latitude", "T_longitude", "I_verified", "I_albumRef", "I_descriptionRef"),
    TAG("TAG", "I_tag"),
    TEST("TEST", "I_dataInt", "T_dataText", "B_dataBlob");

    private String name;
    private String[] rawFields;

    private Table(String name, String... fields) {
        this.name = name;
        this.rawFields = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            this.rawFields[i] = fields[i];
        }
    }

    public String getName() {
        return name;
    }

    public String[] getRawFields() {
        return rawFields;
    }

    public String[] getFields() {
        String[] fields = new String[rawFields.length];
        for (int iField = 0; iField < rawFields.length; iField++) {
            fields[iField] = SchemaUtils.getFieldName(rawFields[iField]);
        }
        return fields;
    }

}
