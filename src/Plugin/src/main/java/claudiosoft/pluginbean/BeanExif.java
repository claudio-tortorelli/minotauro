package claudiosoft.pluginbean;

import claudiosoft.commons.CTException;
import claudiosoft.transientimage.TransientImage;
import java.util.Date;

/**
 *
 * @author claudio.tortorelli
 */
public class BeanExif extends BasePluginBean {

    public int imgWidthPix;
    public int imgHeightPix;
    public String make;
    public String model;
    public Date date;
    public String orientation;
    public String photographer;
    public String latitude;
    public String latitudeRef;
    public String longitude;
    public String longitudeRef;

    public BeanExif(String pluginName) {
        super(pluginName);

        imgWidthPix = 0;
        imgHeightPix = 0;
        make = "";
        model = "";
        date = null;
        orientation = "";
        photographer = "";
        latitude = "";
        latitudeRef = "";
        longitude = "";
        longitudeRef = "";
    }

    @Override
    public void store(TransientImage transientImage) throws CTException {
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
    public void read(TransientImage transientImage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
