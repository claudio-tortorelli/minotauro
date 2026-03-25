package claudiosoft.dbabel;

/**
 *
 * @author claudio.tortorelli
 */
public enum Table {

    ALBUM("album", "path", "name", "year", "month", "day", "elaborated", "descriptionRef"),
    CONFIG("config", "project"),
    DESCRIPTION("description", "text", "blob"),
    EVENT("event", "name", "date", "verified", "descriptionRef", "albumRef"),
    EXIF("exif", "imgWidthPix", "imHeightPix", "make", "model", "date", "orientation", "photographer", "latitude", "latitudeRef", "longitude", "longitudeRef"),
    FACE("face", "imageRef", "peopleRef"),
    IMAGE("image", "blob", "isThumb", "isFace", "wPix", "hPix"),
    PEOPLE("people", "name", "surname", "birthDate", "verified", "descriptionRef", "albumRef"),
    PICTURE("picture", "name", "type", "editDate", "sizeByte", "wPix", "hPix", "albumRef", "imageRef", "exifRef", "tagRef"),
    PLACE("place", "name", "isCity", "isCountry", "latitude", "longitude", "verified", "albumRef", "descriptionRef"),
    TAG("tag", "tag");

    private String name;
    private String[] fields;

    private Table(String name, String... fields) {
        this.name = name;
        this.fields = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            this.fields[i] = fields[i];
        }
    }

    public String getDbName() {
        return name;
    }

    public String[] getFields() {
        return fields;
    }
}
