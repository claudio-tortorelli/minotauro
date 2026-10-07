package claudiosoft.test;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Constants;
import claudiosoft.dbabel.DBabel;
import claudiosoft.dbabel.Table;
import claudiosoft.transientdata.TransientFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TTransientToDB extends BaseJUnitTest {

    protected static DBabel db;
    protected static TransientFile transientImage;
    protected static BasicLogger logger;

    public TTransientToDB() throws CTException {
        super(false, false);
        logger = BasicLogger.get(BasicLogger.LogLevel.DEBUG, Constants.LOGGER_NAME, new File("./target/test-output/TransientToDB.log"));

        File dbFileOrig = new File("../../base_db/babel.db");
        File dbFile = new File("./target/test-output/dbTestInsert.db");

        File transFileOrig = new File("../../testTransient/test.transient");
        File transFile = new File("./target/test-output/test.transient");
        try {
            Files.copy(dbFileOrig.toPath(), dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(transFileOrig.toPath(), transFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            transientImage = new TransientFile(transFile);
            db = new DBabel(dbFile.getAbsolutePath());
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }

    }

//    per inserire nuova entity picture
//    album >                         FolderParseName
//    image >                         ImageThumbnail
//        > picture                   ImageId/ImageFileData
//            > ? exif                ImageExif
//            > ? description         
//            > ? tag                 ImageTags
//        > ? people                  FolderParseName
//            > ? description 
//            > ? album
//        > ? face                    ImageFaceRecognition
//            > ? image               
//            > ? people
//        > ? place                   FolderParseName
//            > ? album
//            > ? description
    @Test
    public void t01InsertParsedData() throws CTException, SQLException {
        String folderPath = transientImage.get("FolderParseName", "path", "");
        Assert.assertFalse("Folder path should not be empty", folderPath.isEmpty());

        String description = transientImage.get("FolderParseName", "description", "");
        int descRef = db.insert(Table.DESCRIPTION, description, null);

        String eventsList = transientImage.get("FolderParseName", "events", "");
        String countries = transientImage.get("FolderParseName", "countries", "");
        String peopleList = transientImage.get("FolderParseName", "peoples", "");
        String year = transientImage.get("FolderParseName", "year", "");
        String month = transientImage.get("FolderParseName", "month", "");
        String day = transientImage.get("FolderParseName", "day", "");

        int elaborated = "true".equalsIgnoreCase(transientImage.get("FolderParseName", "elaborated", "false")) ? 1 : 0;

        int albumRef = db.insert(Table.ALBUM, folderPath, "<name>", year, month, day, String.valueOf(elaborated), String.valueOf(descRef));

        if (!peopleList.isEmpty()) {
            for (String person : peopleList.split(",")) {
                String trimmedPerson = person.trim();
                if (!trimmedPerson.isEmpty()) {
                    db.insert(Table.PEOPLE, trimmedPerson, "<surname>", "<date>", "0", "<desc>", String.valueOf(albumRef));
                }
            }
        }

        if (!eventsList.isEmpty()) {
            for (String event : eventsList.split(",")) {
                String trimmedEvent = event.trim();
                if (!trimmedEvent.isEmpty()) {
                    db.insert(Table.EVENT, trimmedEvent, year, "0", "<desc>", String.valueOf(albumRef));
                }
            }
        }

        if (!countries.isEmpty()) {
            for (String country : countries.split(",")) {
                String trimmedCountry = country.trim();
                if (!trimmedCountry.isEmpty()) {
                    db.insert(Table.PLACE, trimmedCountry, "0", "1", "<lat>", "<lon>", "0", String.valueOf(albumRef), String.valueOf(descRef));
                }
            }
        }
        logger.debug("Successfully inserted parsed data for albumRef: " + albumRef);
    }

    @Test
    public void t99Close() throws CTException {
        if (db.isOpen()) {
            db.close();
        }
    }

}
