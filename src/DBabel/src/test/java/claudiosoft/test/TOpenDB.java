
import claudiosoft.test.BaseJUnitTest;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author Claudio
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TOpenDB extends BaseJUnitTest {

    public TOpenDB() {
        super(false, false);
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
    }

    @Test
    public void t01OpenDB() {
        //Assert.assertTrue(new File(TestUtils.CUSTOM_TEST_LOG_PATH).exists());
    }

}
