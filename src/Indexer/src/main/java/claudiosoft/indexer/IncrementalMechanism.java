package claudiosoft.indexer;

/**
 *
 * @author claudio.tortorelli
 */
public class IncrementalMechanism extends IndexMechanism {

    /**
     * rules to follow in this mechanism that implements a incremental approach.
     * no 'build DB only' switch was provided
     *
     * 1. look for an existent index
     *
     * 2. verify if it is related to current root folder
     *
     * 3. if 1 or 2 are not satisfied
     *
     * 3.1 build the index from scratch and proceed (*)
     *
     * 4.1 otherwise the previous index is loaded
     *
     * 4.2 rebuild the current new index
     *
     * 4.3 do a difference index by previous one and save to a diff index (*)
     *
     * 4.4 current index replaces the previous index
     *
     *
     * (*) = use this index as index to be visited later
     */
    @Override
    public void buildIndex() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
