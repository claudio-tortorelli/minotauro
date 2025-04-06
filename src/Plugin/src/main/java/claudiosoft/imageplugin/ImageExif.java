package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.transientimage.TransientImage;
import claudiosoft.transientimage.TransientImageProvider;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageExif extends BaseImagePlugin {

    private int imgWidthPix;
    private int imgHeightPix;
    private String make;
    private String model;
    private Date date;
    private String orientation;
    private String photographer;
    private String latitude;
    private String latitudeRef;
    private String longitude;
    private String longitudeRef;

    private final boolean SHOW_EXIF_MODE = false;

    public ImageExif(int step) {
        super(step);
    }

    @Override
    public void init(Config config, TransientImageProvider transientImageProvider) throws CTException {
        super.init(config, transientImageProvider);
        imgWidthPix = 0;
        imgHeightPix = 0;
        make = "";
        model = "";
        date = null;
        orientation = "";
        photographer = "";
        latitude = "";
        latitudeRef = "";
        longitude = "";
        longitudeRef = "";
    }

    @Override
    public void apply(File image) throws CTException {
        super.apply(image);
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
            boolean somethingToStore = false;

            Directory directoryBase = metadata.getFirstDirectoryOfType(ExifDirectoryBase.class);
            if (directoryBase != null) {
                if (directoryBase.containsTag(ExifSubIFDDirectory.TAG_IMAGE_WIDTH)) {
                    imgWidthPix = directoryBase.getInt(ExifSubIFDDirectory.TAG_IMAGE_WIDTH);
                    somethingToStore = true;
                }
                if (directoryBase.containsTag(ExifSubIFDDirectory.TAG_IMAGE_HEIGHT)) {
                    imgHeightPix = directoryBase.getInt(ExifSubIFDDirectory.TAG_IMAGE_HEIGHT);
                    somethingToStore = true;
                }
            }

            Directory directoryIf = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (directoryIf != null) {
                if (directoryIf.containsTag(ExifSubIFDDirectory.TAG_MAKE)) {
                    make = directoryIf.getString(ExifSubIFDDirectory.TAG_MAKE);
                    somethingToStore = true;
                }
                if (directoryIf.containsTag(ExifSubIFDDirectory.TAG_MODEL)) {
                    model = directoryIf.getString(ExifSubIFDDirectory.TAG_MODEL);
                    somethingToStore = true;
                }
                if (directoryIf.containsTag(ExifSubIFDDirectory.TAG_DATETIME)) {
                    date = directoryIf.getDate(ExifSubIFDDirectory.TAG_DATETIME);
                    somethingToStore = true;
                }
                if (directoryIf.containsTag(ExifSubIFDDirectory.TAG_ORIENTATION)) {
                    orientation = directoryIf.getString(ExifSubIFDDirectory.TAG_ORIENTATION);
                    somethingToStore = true;
                }
                if (directoryIf.containsTag(ExifSubIFDDirectory.TAG_IMAGE_HEIGHT)) {
                    photographer = directoryIf.getString(ExifSubIFDDirectory.TAG_ARTIST);
                    somethingToStore = true;
                }
            }

            Directory directorySubIf = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (directorySubIf != null) {
                // none...
            }

            Directory directoryGPS = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (directoryGPS != null) {
                if (directoryGPS.containsTag(GpsDirectory.TAG_LATITUDE)) {
                    latitude = directorySubIf.getString(GpsDirectory.TAG_LATITUDE);
                    somethingToStore = true;
                }
                if (directoryGPS.containsTag(GpsDirectory.TAG_LATITUDE_REF)) {
                    latitudeRef = directorySubIf.getString(GpsDirectory.TAG_LATITUDE_REF);
                    somethingToStore = true;
                }
                if (directoryGPS.containsTag(GpsDirectory.TAG_LONGITUDE)) {
                    longitude = directorySubIf.getString(GpsDirectory.TAG_LONGITUDE);
                    somethingToStore = true;
                }
                if (directoryGPS.containsTag(GpsDirectory.TAG_LONGITUDE_REF)) {
                    longitudeRef = directorySubIf.getString(GpsDirectory.TAG_LONGITUDE_REF);
                    somethingToStore = true;
                }
            }
            if (SHOW_EXIF_MODE) {
                for (Directory directoryX : metadata.getDirectories()) {
                    for (Tag tag : directoryX.getTags()) {
                        System.out.println(tag.toString());
                    }
                }
            }
            if (!somethingToStore) {
                logger.debug("no exif data tag");
                return;
            }
            store();
        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex);
        }

    }

    protected void store() throws NoSuchAlgorithmException, IOException, CTException {
        TransientImage transientImage = transientImageProvider.get(imageFile);
        transientImage.set(this.getClass().getSimpleName(), "imgWidthPix", imgWidthPix);
        transientImage.set(this.getClass().getSimpleName(), "imgHeightPix", imgHeightPix);
        transientImage.set(this.getClass().getSimpleName(), "make", make);
        transientImage.set(this.getClass().getSimpleName(), "model", model);
        transientImage.set(this.getClass().getSimpleName(), "date", date);
        transientImage.set(this.getClass().getSimpleName(), "orientation", orientation);
        transientImage.set(this.getClass().getSimpleName(), "photographer", photographer);
        transientImage.set(this.getClass().getSimpleName(), "latitude", latitude);
        transientImage.set(this.getClass().getSimpleName(), "latitudeRef", latitudeRef);
        transientImage.set(this.getClass().getSimpleName(), "longitude", longitude);
        transientImage.set(this.getClass().getSimpleName(), "longitudeRef", longitudeRef);
        transientImage.store();
    }

}
