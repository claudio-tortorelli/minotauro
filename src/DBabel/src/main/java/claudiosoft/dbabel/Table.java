package claudiosoft.dbabel;

/**
 *
 * @author claudio.tortorelli
 */
public enum Table {

    ALBUM("album", "T_path", "T_name", "I_year", "I_month", "I_day", "I_elaborated", "I_descriptionRef"),
    CONFIG("config", "T_project", "T_dbVersion"),
    DESCRIPTION("description", "T_text", "B_blob"),
    EVENT("event", "T_name", "T_date", "I_verified", "I_descriptionRef", "I_albumRef"),
    EXIF("exif", "I_imgWidthPix", "I_imgHeightPix", "T_make", "T_model", "T_date", "T_orientation", "T_photographer", "T_latitude", "T_latitudeRef", "T_longitude", "T_longitudeRef"),
    FACE("face", "I_imageRef", "I_peopleRef"),
    IMAGE("image", "B_blob", "I_isThumb", "I_isFace", "I_wPix", "I_hPix"),
    PEOPLE("people", "T_name", "T_surname", "T_birthDate", "I_verified", "I_descriptionRef", "T_albumRef"),
    PICTURE("picture", "T_name", "T_type", "T_editDate", "I_sizeByte", "I_wPix", "I_hPix", "I_albumRef", "I_imageRef", "I_exifRef", "I_tagRef"),
    PLACE("place", "T_name", "I_isCity", "I_isCountry", "T_latitude", "T_longitude", "I_verified", "I_albumRef", "I_descriptionRef"),
    TAG("tag", "I_tag");

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
