package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;

/**
 *
 * @author claudio.tortorelli
 */
public interface ImagePlugin {

    public void init() throws CTException;

    public void doIt() throws CTException;

    public void publish() throws CTException;
}
