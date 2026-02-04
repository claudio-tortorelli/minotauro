package claudiosoft.baseplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.indexer.IndexMechanism;

/**
 *
 * @author claudio.tortorelli
 */
public interface Plugin {

    public void init(Config config) throws CTException;

    public void apply(IndexMechanism indexer) throws CTException;

    public void close() throws CTException;

}
