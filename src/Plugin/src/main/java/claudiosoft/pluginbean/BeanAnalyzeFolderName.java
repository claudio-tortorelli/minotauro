package claudiosoft.pluginbean;

import claudiosoft.commons.CTException;
import claudiosoft.transientimage.TransientImage;
import java.util.LinkedList;

/**
 *
 * @author claudio.tortorelli
 */
public class BeanAnalyzeFolderName extends BasePluginBean {

    public String year;
    public String month;
    public String description;
    public boolean elaborated;

    public LinkedList<String> places;
    public LinkedList<String> people;
    public LinkedList<String> events;

    public BeanAnalyzeFolderName(String pluginName) {
        super(pluginName);

        year = "";
        month = "";
        description = "";
        elaborated = false;

        places = new LinkedList<>();
        people = new LinkedList<>();
        events = new LinkedList<>();
    }

    @Override
    public void store(TransientImage transientImage) throws CTException {
        transientImage.set(pluginName, "year", year);
        transientImage.set(pluginName, "month", month);
        transientImage.set(pluginName, "description", description);

        if (!events.isEmpty()) {
            String eventList = "";
            for (String event : events) {
                eventList += event;
                eventList += ",";
            }
            eventList = eventList.substring(0, eventList.length() - 1);
            transientImage.set(pluginName, "events", eventList);
        }
        if (!places.isEmpty()) {
            String placeList = "";
            for (String place : places) {
                placeList += place;
                placeList += ",";
            }
            placeList = placeList.substring(0, placeList.length() - 1);
            transientImage.set(pluginName, "places", placeList);
        }
        if (!people.isEmpty()) {
            String peopleList = "";
            for (String name : people) {
                peopleList += name;
                peopleList += ",";
            }
            peopleList = peopleList.substring(0, peopleList.length() - 1);
            transientImage.set(pluginName, "peoples", peopleList);
        }
        transientImage.store();
    }

    @Override
    public void read(TransientImage transientImage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
