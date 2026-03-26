package claudiosoft.test;

import claudiosoft.dbabel.SchemaUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 *
 * @author claudio.tortorelli
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TPrintSchema extends BaseJUnitTest {

    public TPrintSchema() {
        super(false, false);
    }

    @Test
    public void t01PrintSchema() {
        System.out.print(SchemaUtils.printSchema());
    }
}
