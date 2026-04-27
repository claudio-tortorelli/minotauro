package claudiosoft.test;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Constants;
import claudiosoft.dbabel.DBabel;
import claudiosoft.pluginbean.BeanFolderParseName;
import claudiosoft.pluginbean.BeanImageDescription;
import claudiosoft.pluginbean.BeanImageExif;
import claudiosoft.pluginbean.BeanImageFileData;
import claudiosoft.pluginbean.BeanImageId;
import claudiosoft.pluginbean.BeanImageTags;
import claudiosoft.pluginbean.BeanImageThumbnail;
import claudiosoft.transientdata.TransientFile;
import claudiosoft.transientdata.TransientProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author Claudio
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TPluginOutput extends BaseJUnitTest {

    protected static DBabel db;

    public TPluginOutput() throws CTException, IOException {
        super(false, false);
        BasicLogger.get(BasicLogger.LogLevel.DEBUG, Constants.LOGGER_NAME, new File("./target/test-output/pluginOutput.log"));

        File rootFolder = new File("./target/test-output/");
        File imgOrig = new File("../../testImg/lante.jpg");
        File imgTarget = new File("./target/test-output/lante.jpg");

        Files.copy(imgOrig.toPath(), imgTarget.toPath(), StandardCopyOption.REPLACE_EXISTING);

        TransientProvider.init(rootFolder, rootFolder);
    }

    @Test
    public void t01BeanImageId() throws CTException, IOException {

        File imgTarget = new File("./target/test-output/lante.jpg");
        TransientFile transientImage = TransientProvider.getProvider().get(imgTarget);

        BeanImageId curData = new BeanImageId("ImageId");
        curData.hashId = "hashId";
        curData.store(transientImage);

        Assert.assertTrue(transientImage.exists());
    }

    @Test
    public void t01BeanFolderParseName() throws CTException {

        File imgTarget = new File("./target/test-output/lante.jpg");
        TransientFile transientImage = TransientProvider.getProvider().get(imgTarget);

        BeanFolderParseName curData = new BeanFolderParseName("FolderParseName");
        curData.description = "description";
        curData.elaborated = false;
        curData.month = "month";
        curData.path = "path";
        curData.year = "year";
        curData.cities = new LinkedList<>();
        curData.cities.add("city");
        curData.countries = new LinkedList<>();
        curData.countries.add("country");
        curData.events = new LinkedList<>();
        curData.events.add("event");
        curData.people = new LinkedList<>();
        curData.people.add("people");

        curData.store(transientImage);
    }

    @Test
    public void t01BeanImageDescription() throws CTException {

        File imgTarget = new File("./target/test-output/lante.jpg");
        TransientFile transientImage = TransientProvider.getProvider().get(imgTarget);

        BeanImageDescription curData = new BeanImageDescription("ImageDescription");
        curData.description = "description";
        curData.store(transientImage);
    }

    @Test
    public void t01BeanImageExif() throws CTException {

        File imgTarget = new File("./target/test-output/lante.jpg");
        TransientFile transientImage = TransientProvider.getProvider().get(imgTarget);

        BeanImageExif curData = new BeanImageExif("ImageExif");
        curData.imgWidthPix = 1;
        curData.imgHeightPix = 1;
        curData.make = "make";
        curData.model = "model";
        curData.date = "date";
        curData.orientation = "orientation";
        curData.photographer = "photographer";
        curData.latitude = "latitude";
        curData.latitudeRef = "latitudeRef";
        curData.longitude = "longitude";
        curData.longitudeRef = "longitudeRef";
        curData.store(transientImage);
    }

    @Test
    public void t01BeanImageFileData() throws CTException {

        File imgTarget = new File("./target/test-output/lante.jpg");
        TransientFile transientImage = TransientProvider.getProvider().get(imgTarget);

        BeanImageFileData curData = new BeanImageFileData("ImageFileData");
        curData.folder = "folder";
        curData.fileName = "fileName";
        curData.lastModifiedDate = "lastModifiedDate";
        curData.ext = "ext";
        curData.fileSize = "fileSize";
        curData.imgWidthPix = "imgWidthPix";
        curData.imgHeightPix = "imgHeightPix";
        curData.store(transientImage);
    }

    @Test
    public void t01BeanImageTags() throws CTException {

        File imgTarget = new File("./target/test-output/lante.jpg");
        TransientFile transientImage = TransientProvider.getProvider().get(imgTarget);

        BeanImageTags curData = new BeanImageTags("ImageTags");
        curData.tagList = "tagList";
        curData.store(transientImage);
    }

    @Test
    public void t01BeanImageThumbnail() throws CTException {

        File imgTarget = new File("./target/test-output/lante.jpg");
        TransientFile transientImage = TransientProvider.getProvider().get(imgTarget);

        BeanImageThumbnail curData = new BeanImageThumbnail("ImageThumbnail");
        curData.base64Image = "base64Image";
        curData.store(transientImage);
    }

}
