package claudiosoft.indexer;

import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;

/**
 *
 * @author claudio.tortorelli
 */
public class IndexFactory {

    private IndexFactory() {

    }

    public static IndexMechanism get(IndexMechanismType mechType) throws CTException {
        if (mechType == IndexMechanismType.BASIC) {
            return new BasicMechanism(); // just this for now
        }
        throw new CTException("unsupported mechanism", CTError.INDEXER_UNSUPPORTED_MECHANISM);
    }

}
