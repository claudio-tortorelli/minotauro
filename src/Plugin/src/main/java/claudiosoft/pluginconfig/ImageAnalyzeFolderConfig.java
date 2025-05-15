package claudiosoft.pluginconfig;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.utils.BasicUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author claudio.tortorelli
 */
public class ImageAnalyzeFolderConfig extends PluginConfig {

    public boolean advanced;
    public boolean enableWikipedia;
    public LinkedList<Pattern> patterns;

    public List<String> storedEvents;
    public List<String> storedCities;
    public List<String> storedCountries;
    public List<String> storedNames;
    public List<String> storedTools;

    public ImageAnalyzeFolderConfig(Config config) throws CTException {
        super(config);

        patterns = new LinkedList<>();
        String regex = config.get(this.getClass().getSimpleName(), "regex1", "");
        patterns.add(Pattern.compile(regex, Pattern.CASE_INSENSITIVE));

        storedEvents = new LinkedList<>();
        storedCities = new LinkedList<>();
        storedCountries = new LinkedList<>();
        storedNames = new LinkedList<>();
        storedTools = new LinkedList<>();

        advanced = config.get(this.getClass().getSimpleName(), "parseDesc", "false").equalsIgnoreCase("true");
        if (advanced) {
            File fileCities;
            try {
                fileCities = BasicUtils.getFileFromRes("files/city_list.txt");
                storedCities = Files.readAllLines(fileCities.toPath());
            } catch (IOException ex) {
                logger.error("city files not found");
                throw new CTException(ex);
            }

            File fileCountries;
            try {
                fileCountries = BasicUtils.getFileFromRes("files/country_list.txt");
                storedCountries = Files.readAllLines(fileCountries.toPath());
            } catch (IOException ex) {
                logger.error("country files not found");
                throw new CTException(ex);
            }

            File fileEvents;
            try {
                fileEvents = BasicUtils.getFileFromRes("files/common_events.txt");
                storedEvents = Files.readAllLines(fileEvents.toPath());
            } catch (IOException ex) {
                logger.error("event files not found");
                throw new CTException(ex);
            }

            File fileNames;
            try {
                fileNames = BasicUtils.getFileFromRes("files/nomi_italiani.txt");
                storedNames = Files.readAllLines(fileNames.toPath());
            } catch (IOException ex) {
                logger.error("name files not found");
                throw new CTException(ex);
            }

            File fileTools;
            try {
                fileTools = BasicUtils.getFileFromRes("files/tool_list.txt");
                storedTools = Files.readAllLines(fileTools.toPath());
            } catch (IOException ex) {
                logger.error("name files not found");
                throw new CTException(ex);
            }
        }

        enableWikipedia = config.get(this.getClass().getSimpleName(), "enableWikipedia", "false").equalsIgnoreCase("true");
    }

}
