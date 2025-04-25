package claudiosoft.imageplugin;

import claudiosoft.commons.CTException;
import claudiosoft.commons.Config;
import claudiosoft.pluginbean.BeanAnalyzeFolderName;
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

    private boolean advanced;
    private boolean enableWikipedia;
    private LinkedList<Pattern> patterns;

    private List<String> storedEvents;
    private List<String> storedCities;
    private List<String> storedCountries;
    private List<String> storedNames;
    private List<String> storedTools;

    public ImageAnalyzeFolderName(int step) {
        super(step);
    }

    @Override
    public void init(Config config, String pluginName) throws CTException {
        super.init(config, pluginName);

        /**
         * TODO adesso deve essere implementato il multi pattern per parsare i
         * casi diversi dallo standard
         */
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

    @Override
    public void apply(File image, TransientImage transientImage) throws CTException {
        super.apply(image, transientImage);

        BeanAnalyzeFolderName data = new BeanAnalyzeFolderName(this.getClass().getSimpleName());

        String folderPath = image.getParent();
        try {
            folderPath = folderPath.replace("\\", "/").toLowerCase();

            String[] folders = folderPath.split("/");
            if (folders.length < 2) {
                logger.error(String.format("unable to extract target folder from %s", folderPath));
                return;
            }

            boolean matched = false;
            for (int i = folders.length - 1; i >= 0; i--) {
                String folderName = folders[i];
                if (storedTools.contains(folderName)) {
                    data.elaborated = true;
                    continue;
                }

                folderName = folderName.replace(".", " ");
                folderName = folderName.replace("-", " ");

                Matcher matcher = patterns.get(0).matcher(folderName);
                if (!matcher.find()) {
                    data = checkAdvanced(data, folderName);
                    continue;
                }
                String[] fields = folderName.split(" ");
                if (fields.length < 3) {
                    logger.error(String.format("unable to extract fields from %s", folderPath));
                    break;
                }
                data.year = fields[0];
                data.month = fields[1];
                for (int iD = 2; iD < fields.length; iD++) {
                    data.description += fields[iD];
                    data.description += " ";
                }
                data.description = data.description.trim();
                matched = true;

                data = checkAdvanced(data, data.description);

                logger.debug(String.format("found this folder %s", folderName));
                data.store(transientImage);
                break;
            }
            if (!matched) {
                logger.warn(String.format("unable to analyze or parse the folder"));
                failed = true;
            }
        } catch (Exception ex) {
            throw new CTException(ex.getMessage(), ex);
        }
    }

    private BeanAnalyzeFolderName checkAdvanced(BeanAnalyzeFolderName data, String description) {
        if (!advanced) {
            return data;
        }
        String[] words = new String[1];
        words[0] = description;
        if (description.contains(",")) {
            words = description.split(",");
        } else {
            words = description.split(" ");
        }

        Wiki wiki = null;
        if (enableWikipedia) {
            wiki = new Wiki.Builder().build();
        }

        for (String word : words) {
            word = word.trim();
            word = word.replace(",", "");
            if (word.length() <= 2) {
                continue;
            }
            if (storedEvents.contains(word)) {
                data.events.add(word);
                continue;
            }
            if (storedCities.contains(word)) {
                data.cities.add(word);
                continue;
            }
            if (storedCountries.contains(word)) {
                data.countries.add(word);
                continue;
            }
            if (storedNames.contains(word)) {
                data.people.add(word);
                continue;
            }
            if (wiki != null) {
                //https://github.com/fastily/jwiki
                //wiki.search(year, step, ns) 
            }
        }
        return data;
    }
}
