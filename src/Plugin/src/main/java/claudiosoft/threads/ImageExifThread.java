package claudiosoft.threads;

import claudiosoft.commons.CTException;
import claudiosoft.pluginbean.BeanExif;
import claudiosoft.pluginconfig.ImageExifConfig;
import claudiosoft.transientdata.TransientFile;
import claudiosoft.transientdata.TransientProvider;
import claudiosoft.utils.Failures;
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
public class ImageExifThread extends PluginThread {

    private final ImageExifConfig plugConf;
    private final BeanExif data;

    public ImageExifThread(File curImage, ImageExifConfig plugConf, BeanExif data) throws CTException {
        super(curImage);
        this.plugConf = plugConf;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            super.run();
            TransientFile transientImage = TransientProvider.getProvider().get(curFile);

            Metadata metadata = ImageMetadataReader.readMetadata(curFile);
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
            if (plugConf.SHOW_EXIF_MODE) {
                for (Directory directoryX : metadata.getDirectories()) {
                    for (Tag tag : directoryX.getTags()) {
                        System.out.println(tag.toString());
                    }
                }
            }
            if (!somethingToStore) {
                logger.warn("no exif data tag");
                return;
            }

            data.store(transientImage);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            Failures.addFailure();
        } finally {

        }
    }

}
