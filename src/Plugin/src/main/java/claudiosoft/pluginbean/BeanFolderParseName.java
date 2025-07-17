package claudiosoft.pluginbean;

import claudiosoft.commons.CTException;
import claudiosoft.transientdata.TransientFile;
import java.util.LinkedList;

/**
 *
 * @author claudio.tortorelli
 */
public class BeanFolderParseName extends BasePluginBean {

    public String year;
    public String month;
    public String description;
    public boolean elaborated;

    public LinkedList<String> cities;
    public LinkedList<String> countries;
    public LinkedList<String> people;
    public LinkedList<String> events;

    public BeanFolderParseName(String pluginName) {
        super(pluginName);

        year = "";
        month = "";
        description = "";
        elaborated = false;

        cities = new LinkedList<>();
        countries = new LinkedList<>();
        people = new LinkedList<>();
        events = new LinkedList<>();
    }

    @Override
    public void store(TransientFile transientFolder) throws CTException {
        transientFolder.set(pluginName, "year", year);
        transientFolder.set(pluginName, "month", month);
        transientFolder.set(pluginName, "description", description);

        if (!events.isEmpty()) {
            String eventList = "";
            for (String event : events) {
                eventList += event;
                eventList += ",";
            }
            eventList = eventList.substring(0, eventList.length() - 1);
            transientFolder.set(pluginName, "events", eventList);
        }
        if (!cities.isEmpty()) {
            String placeList = "";
            for (String place : cities) {
                placeList += place;
                placeList += ",";
            }
            placeList = placeList.substring(0, placeList.length() - 1);
            transientFolder.set(pluginName, "cities", placeList);
        }
        if (!countries.isEmpty()) {
            String placeList = "";
            for (String place : countries) {
                placeList += place;
                placeList += ",";
            }
            placeList = placeList.substring(0, placeList.length() - 1);
            transientFolder.set(pluginName, "countries", placeList);
        }
        if (!people.isEmpty()) {
            String peopleList = "";
            for (String name : people) {
                peopleList += name;
                peopleList += ",";
            }
            peopleList = peopleList.substring(0, peopleList.length() - 1);
            transientFolder.set(pluginName, "peoples", peopleList);
        }
        transientFolder.set(pluginName, "elaborated", elaborated);

        transientFolder.store();
    }

    @Override
    public void read(TransientFile transientImage) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
