package claudiosoft.imageplugin.plugins;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.imageplugin.ImagePlugin;
import java.io.File;

/**
 *
 * @author claudio.tortorelli
 */
public abstract class BasePlugin implements ImagePlugin {

    protected int step;
    protected File imageFile;
    protected Config config;

    public BasePlugin(int step) {
        this.step = step;
    }

    public int getStep() {
        return step;
    }

    @Override
    public void init(Config config) throws CTException {
        this.config = config;
    }

    @Override
    public void apply() throws CTException {

    }

    protected void store() throws CTException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
