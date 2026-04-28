package claudiosoft.test;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Constants;
import claudiosoft.dbabel.DBabel;
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

/**
 *
 * @author Claudio
 */
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
        Assert.assertTrue(!folderPath.isEmpty());

        //db.insert(Table.TEST, SchemaUtils.getSchemaVersion(), "text", "YmxvYg==");
        BasicLogger.get().debug("inserted");
    }

    @Test
    public void t99Close() throws CTException {
        if (db.isOpen()) {
            db.close();
        }
    }

}
