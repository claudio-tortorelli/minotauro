package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.transientimage.TransientImage;
import claudiosoft.utils.BasicUtils;
import io.github.fastily.jwiki.core.Wiki;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageAnalyzeFolderName extends BaseImagePlugin {

    private String year;
    private String month;
    private String description;
    private boolean elaborated;

    private boolean advanced;
    private LinkedList<String> places;
    private LinkedList<String> people;
    private LinkedList<String> events;

    private boolean enableWikipedia;

    private LinkedList<Pattern> patterns;

    private File fileCities;
    private File fileEvents;
    private File fileNames;
    private File fileTools;

    private boolean done;

    public ImageAnalyzeFolderName(int step) {
        super(step);
    }

    @Override
    public void init(Config config, String pluginName) throws CTException {
        super.init(config, pluginName);

        patterns = new LinkedList<>();
        String regex = config.get(this.getClass().getSimpleName(), "regex1", "");
        patterns.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));

        advanced = config.get(this.getClass().getSimpleName(), "parseDesc", "false").equalsIgnoreCase("true");
        if (advanced) {
            try {
                fileCities = BasicUtils.getFileFromRes("files/city_list.txt");
            } catch (IOException ex) {
                logger.error("city files not found");
                throw new CTException(ex);
            }
            places = new LinkedList<>();

            try {
                fileEvents = BasicUtils.getFileFromRes("files/common_events.txt");
            } catch (IOException ex) {
                logger.error("event files not found");
                throw new CTException(ex);
            }
            events = new LinkedList<>();

            try {
                fileNames = BasicUtils.getFileFromRes("files/nomi_italiani.txt");
            } catch (IOException ex) {
                logger.error("name files not found");
                throw new CTException(ex);
            }
            people = new LinkedList<>();

            try {
                fileTools = BasicUtils.getFileFromRes("files/tool_list.txt");
            } catch (IOException ex) {
                logger.error("name files not found");
                throw new CTException(ex);
            }
        }
        elaborated = false;
        enableWikipedia = config.get(this.getClass().getSimpleName(), "enableWikipedia", "false").equalsIgnoreCase("true");
        done = false;
    }

    @Override
    public void apply(File image, TransientImage transientImage) throws CTException {
        super.apply(image, transientImage);
        if (done) {
            return;
        }

        String folderPath = image.getParent();
        try {
            folderPath = folderPath.replace("\\", "/").toLowerCase();

            String[] folders = folderPath.split("/");
            if (folders.length < 2) {
                logger.error(String.format("unable to extract target folder from %s", folderPath));
                return;
            }

            List<String> storedEvents = Files.readAllLines(this.fileEvents.toPath());
            List<String> storedCities = Files.readAllLines(this.fileCities.toPath());
            List<String> storedNames = Files.readAllLines(this.fileNames.toPath());
            List<String> storedTools = Files.readAllLines(this.fileTools.toPath());

            for (String parent : folders) {
                if (storedTools.contains(parent)) {
                    elaborated = true;
                }

                Matcher matcher = patterns.get(0).matcher(parent);
                if (!matcher.find()) {
                    continue;
                }
                String[] fields = parent.split(" ");
                if (fields.length < 3) {
                    logger.error(String.format("unable to extract fields from %s", folderPath));
                    break;
                }
                year = fields[0];
                month = fields[1];
                description = fields[2];

                if (advanced) {
                    Wiki wiki = null;
                    if (enableWikipedia) {
                        wiki = new Wiki.Builder().build();
                    }

                    String[] phrases = new String[1];
                    phrases[0] = description;
                    if (description.contains(",")) {
                        phrases = description.split(",");
                    } else {
                        phrases = description.split(" ");
                    }
                    for (String phrase : phrases) {
                        if (phrase.length() <= 2) {
                            continue;
                        }
                        if (storedEvents.contains(phrase)) {
                            events.add(phrase);
                            continue;
                        }
                        if (storedCities.contains(phrase)) {
                            places.add(phrase); //TODO use countries and other lists
                            continue;
                        }
                        if (storedNames.contains(phrase)) {
                            people.add(phrase);
                            continue;
                        }
                        // TODO wiki.
                        if (wiki != null) {
                            //https://github.com/fastily/jwiki
                            //wiki.search(year, step, ns) 
                        }
                    }
                }

                logger.debug(String.format("found this folder %s", parent));
                store();
                done = true;
                break;
            }
        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex);
        }
    }

    @Override
    public void store() throws CTException {
        transientImage.set(this.getClass().getSimpleName(), "year", year);
        transientImage.set(this.getClass().getSimpleName(), "month", month);
        transientImage.set(this.getClass().getSimpleName(), "description", description);
        if (advanced) {
            if (!events.isEmpty()) {
                String eventList = "";
                for (String event : events) {
                    eventList += event;
                    eventList += ",";
                }
                eventList = eventList.substring(0, eventList.length() - 1);
                transientImage.set(this.getClass().getSimpleName(), "events", eventList);
            }
            if (!places.isEmpty()) {
                String placeList = "";
                for (String place : places) {
                    placeList += place;
                    placeList += ",";
                }
                placeList = placeList.substring(0, placeList.length() - 1);
                transientImage.set(this.getClass().getSimpleName(), "places", placeList);
            }
            if (!people.isEmpty()) {
                String peopleList = "";
                for (String name : people) {
                    peopleList += name;
                    peopleList += ",";
                }
                peopleList = peopleList.substring(0, peopleList.length() - 1);
                transientImage.set(this.getClass().getSimpleName(), "peoples", peopleList);
            }
        }
        //transientImage.store();
    }

}
