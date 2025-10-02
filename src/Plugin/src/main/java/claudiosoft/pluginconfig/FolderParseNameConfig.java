package claudiosoft.pluginconfig;

import claudiosoft.baseplugin.FolderPattern;
import claudiosoft.baseplugin.PatternId;
import claudiosoft.commons.CTError;
import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.utils.BasicUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author claudio.tortorelli
 */
public class FolderParseNameConfig extends PluginConfig {

    public int maxThread;
    public boolean advanced;
    public boolean enableWikipedia;
    public LinkedList<FolderPattern> foldPatterns;
    public String rootFolder;

    public List<String> storedEvents;
    public List<String> storedCities;
    public List<String> storedCountries;
    public List<String> storedPeople;
    public List<String> storedTools;

    public FolderParseNameConfig(Config config, String pluginName) throws CTException {
        super(config, pluginName);

        rootFolder = config.get("index", "rootPath");

        foldPatterns = new LinkedList<>();
        String regex = config.get(pluginName, "regex01", "");
        int id = 1;
        while (!regex.isEmpty()) {
            PatternId patId = PatternId.valueOf(String.format("p%02d", id));
            foldPatterns.add(new FolderPattern(patId, Pattern.compile(regex, Pattern.CASE_INSENSITIVE)));

            id++;
            regex = config.get(pluginName, String.format("regex%02d", id), "");
        }
        Collections.sort(foldPatterns, new Comparator<FolderPattern>() {
            @Override
            public int compare(FolderPattern a, FolderPattern b) {
                return a.getId().name().compareTo(b.getId().name());
            }
        });

        storedEvents = new LinkedList<>();
        storedCities = new LinkedList<>();
        storedCountries = new LinkedList<>();
        storedPeople = new LinkedList<>();
        storedTools = new LinkedList<>();

        advanced = config.get(pluginName, "parseDesc", "false").equalsIgnoreCase("true");
        if (advanced) {
            File fileCities;
            try {
                fileCities = BasicUtils.getFileFromRes("files/city_list.txt");
                storedCities = Files.readAllLines(fileCities.toPath());
            } catch (IOException ex) {
                logger.error("city files not found");
                throw new CTException(ex, CTError.IO_GENERIC_ERROR);
            }

            File fileCountries;
            try {
                fileCountries = BasicUtils.getFileFromRes("files/country_list.txt");
                storedCountries = Files.readAllLines(fileCountries.toPath());
            } catch (IOException ex) {
                logger.error("country files not found");
                throw new CTException(ex, CTError.IO_GENERIC_ERROR);
            }

            File fileEvents;
            try {
                fileEvents = BasicUtils.getFileFromRes("files/common_events.txt");
                storedEvents = Files.readAllLines(fileEvents.toPath());
            } catch (IOException ex) {
                logger.error("event files not found");
                throw new CTException(ex, CTError.IO_GENERIC_ERROR);
            }

            File fileNames;
            try {
                fileNames = BasicUtils.getFileFromRes("files/nomi_italiani.txt");
                storedPeople = Files.readAllLines(fileNames.toPath());
            } catch (IOException ex) {
                logger.error("name files not found");
                throw new CTException(ex, CTError.IO_GENERIC_ERROR);
            }

            File fileTools;
            try {
                fileTools = BasicUtils.getFileFromRes("files/tool_list.txt");
                storedTools = Files.readAllLines(fileTools.toPath());
            } catch (IOException ex) {
                logger.error("name files not found");
                throw new CTException(ex, CTError.IO_GENERIC_ERROR);
            }
        }

        enableWikipedia = config.get(pluginName, "enableWikipedia", "false").equalsIgnoreCase("true");
    }

}
