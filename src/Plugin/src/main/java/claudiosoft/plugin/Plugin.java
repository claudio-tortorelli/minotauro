package claudiosoft.plugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.indexer.Indexer;

/**
 *
 * @author claudio.tortorelli
 */
public interface Plugin {

    public void init(Config config) throws CTException;

    public void apply(Indexer indexer) throws CTException;

    public void close() throws CTException;

}
