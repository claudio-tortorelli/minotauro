package claudiosoft.plugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;

/**
 *
 * @author claudio.tortorelli
 */
public interface ImagePlugin {

    public void init(Config config) throws CTException;

    public void apply() throws CTException;

}
