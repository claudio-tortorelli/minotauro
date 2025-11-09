package claudiosoft.indexer;

/**
 *
 * @author claudio.tortorelli
 */
public class RefreshDBMechanism extends IndexMechanism {
    /**
     * rules to follow in this mechanism that implements a refresh only
     * approach. 'build DB only' switch was provided
     *
     * 1. look for an existent index
     *
     * 2. verify if it is related to current root folder
     *
     * 3. if 1 or 2 are not satisfied
     *
     * 3.1 build the index from scratch and proceed (*)
     *
     * 4.1 otherwise the previous index is loaded (*)
     *
     * (*) = use this index as index to be visited later
     */
}
