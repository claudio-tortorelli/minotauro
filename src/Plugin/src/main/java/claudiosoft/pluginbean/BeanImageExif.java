package claudiosoft.pluginbean;

import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;
import claudiosoft.transientdata.TransientFile;

/**
 *
 * @author claudio.tortorelli
 */
public class BeanImageExif extends BasePluginBean {

    public int imgWidthPix;
    public int imgHeightPix;
    public String make;
    public String model;
    public String date;
    public String orientation;
    public String photographer;
    public String latitude;
    public String latitudeRef;
    public String longitude;
    public String longitudeRef;

    public BeanImageExif(String pluginName) {
        super(pluginName);

        imgWidthPix = 0;
        imgHeightPix = 0;
        make = "";
        model = "";
        date = "";
        orientation = "";
        photographer = "";
        latitude = "";
        latitudeRef = "";
        longitude = "";
        longitudeRef = "";
    }

    @Override
    public void store(TransientFile transientImage) throws CTException {
        transientImage.set(pluginName, "imgWidthPix", imgWidthPix);
        transientImage.set(pluginName, "imgHeightPix", imgHeightPix);
        transientImage.set(pluginName, "make", make);
        transientImage.set(pluginName, "model", model);
        transientImage.set(pluginName, "date", date);
        transientImage.set(pluginName, "orientation", orientation);
        transientImage.set(pluginName, "photographer", photographer);
        transientImage.set(pluginName, "latitude", latitude);
        transientImage.set(pluginName, "latitudeRef", latitudeRef);
        transientImage.set(pluginName, "longitude", longitude);
        transientImage.set(pluginName, "longitudeRef", longitudeRef);
        transientImage.store();
    }

    @Override
    public void read(TransientFile transientImage) throws CTException {
        try {
            imgWidthPix = Integer.parseInt(transientImage.get(pluginName, "imgWidthPix", "0"));
            imgHeightPix = Integer.parseInt(transientImage.get(pluginName, "imgHeightPix", "0"));
            make = transientImage.get(pluginName, "make", "");
            model = transientImage.get(pluginName, "model", "");
            date = transientImage.get(pluginName, "date", "");
            orientation = transientImage.get(pluginName, "orientation", "");
            photographer = transientImage.get(pluginName, "photographer", "");
            latitude = transientImage.get(pluginName, "latitude", "");
            latitudeRef = transientImage.get(pluginName, "latitudeRef", "");
            longitude = transientImage.get(pluginName, "longitude", "");
            longitudeRef = transientImage.get(pluginName, "longitudeRef", "");
        } catch (Exception ex) {
            BasicLogger.get().error(ex.getMessage(), ex);
            throw new CTException(ex, CTError.TRANSIENT_READ_FILE);
        }
    }

}
