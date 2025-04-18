package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.pluginbean.BeanExif;
import claudiosoft.transientimage.TransientImage;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import java.io.File;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageExif extends BaseImagePlugin {

    private final boolean SHOW_EXIF_MODE = false;

    public ImageExif(int step) {
        super(step);
    }

    @Override
    public void init(Config config, String pluginName) throws CTException {
        super.init(config, pluginName);
    }

    @Override
    public void apply(File image, TransientImage transientImage) throws CTException {
        super.apply(image, transientImage);
        try {
            BeanExif data = new BeanExif(this.getClass().getSimpleName());

            Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
            boolean somethingToStore = false;

            Directory directoryBase = metadata.getFirstDirectoryOfType(ExifDirectoryBase.class);
            if (directoryBase != null) {
                if (directoryBase.containsTag(ExifSubIFDDirectory.TAG_IMAGE_WIDTH)) {
                    data.imgWidthPix = directoryBase.getInt(ExifSubIFDDirectory.TAG_IMAGE_WIDTH);
                    somethingToStore = true;
                }
                if (directoryBase.containsTag(ExifSubIFDDirectory.TAG_IMAGE_HEIGHT)) {
                    data.imgHeightPix = directoryBase.getInt(ExifSubIFDDirectory.TAG_IMAGE_HEIGHT);
                    somethingToStore = true;
                }
            }

            Directory directoryIf = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (directoryIf != null) {
                if (directoryIf.containsTag(ExifSubIFDDirectory.TAG_MAKE)) {
                    data.make = directoryIf.getString(ExifSubIFDDirectory.TAG_MAKE);
                    somethingToStore = true;
                }
                if (directoryIf.containsTag(ExifSubIFDDirectory.TAG_MODEL)) {
                    data.model = directoryIf.getString(ExifSubIFDDirectory.TAG_MODEL);
                    somethingToStore = true;
                }
                if (directoryIf.containsTag(ExifSubIFDDirectory.TAG_DATETIME)) {
                    data.date = directoryIf.getDate(ExifSubIFDDirectory.TAG_DATETIME);
                    somethingToStore = true;
                }
                if (directoryIf.containsTag(ExifSubIFDDirectory.TAG_ORIENTATION)) {
                    data.orientation = directoryIf.getString(ExifSubIFDDirectory.TAG_ORIENTATION);
                    somethingToStore = true;
                }
                if (directoryIf.containsTag(ExifSubIFDDirectory.TAG_IMAGE_HEIGHT)) {
                    data.photographer = directoryIf.getString(ExifSubIFDDirectory.TAG_ARTIST);
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
                    data.latitude = directorySubIf.getString(GpsDirectory.TAG_LATITUDE);
                    somethingToStore = true;
                }
                if (directoryGPS.containsTag(GpsDirectory.TAG_LATITUDE_REF)) {
                    data.latitudeRef = directorySubIf.getString(GpsDirectory.TAG_LATITUDE_REF);
                    somethingToStore = true;
                }
                if (directoryGPS.containsTag(GpsDirectory.TAG_LONGITUDE)) {
                    data.longitude = directorySubIf.getString(GpsDirectory.TAG_LONGITUDE);
                    somethingToStore = true;
                }
                if (directoryGPS.containsTag(GpsDirectory.TAG_LONGITUDE_REF)) {
                    data.longitudeRef = directorySubIf.getString(GpsDirectory.TAG_LONGITUDE_REF);
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
            data.store(transientImage);
        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex);
        }
    }

}
