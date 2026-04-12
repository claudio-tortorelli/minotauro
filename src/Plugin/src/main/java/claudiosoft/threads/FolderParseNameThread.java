package claudiosoft.threads;

import claudiosoft.baseplugin.FolderPattern;
import claudiosoft.commons.CTException;
import claudiosoft.pluginbean.BeanFolderParseName;
import claudiosoft.pluginconfig.FolderParseNameConfig;
import claudiosoft.transientdata.TransientFile;
import claudiosoft.transientdata.TransientProvider;
import claudiosoft.utils.Failures;
import io.github.fastily.jwiki.core.Wiki;
import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author claudio.tortorelli
 */
public class FolderParseNameThread extends PluginThread {

    private final FolderParseNameConfig plugConf;
    private final BeanFolderParseName data;

    private final boolean VERBOSE_MODE = true; // develop only

    public FolderParseNameThread(UUID uuid, File curFolder, FolderParseNameConfig plugConf, BeanFolderParseName data) throws CTException {
        super(uuid, curFolder);
        this.plugConf = plugConf;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            TransientFile transientFolder = TransientProvider.getProvider().get(curFile);
            data.path = curFile.getCanonicalPath().replace("\\", "/").toLowerCase();
            String relativePath = data.path.substring(plugConf.rootFolder.length(), data.path.length());
            String[] folders = relativePath.split("/");

            boolean matched = false;
            for (int i = folders.length - 1; i >= 0; i--) {
                String folderName = folders[i];
                if (VERBOSE_MODE) {
                    logger.debug("\n\n----- folder " + folderName);
                }
                if (find(plugConf.storedTools, folderName)) {
                    data.elaborated = true;
                    continue;
                }

                for (FolderPattern pattern : plugConf.foldPatterns) {
                    if (!pattern.applyPattern(folderName)) {
                        if (VERBOSE_MODE) {
                            logger.debug(logThreadMessage("NO pattern " + pattern.getPattern().pattern()));
                        }
                        continue;
                    }
                    data.year = pattern.getYear();
                    data.month = pattern.getMonth();
                    data.description = pattern.getDescription();

                    if (VERBOSE_MODE) {
                        logger.debug(logThreadMessage("MATCH with pattern " + pattern.getId().name() + " " + pattern.getPattern().pattern() + " and schema " + pattern.getId().getSchema() + "\n\tgot " + pattern.toString()));
                    }

                    checkAdvanced(pattern.getDescription());

                    logger.debug(logThreadMessage(String.format(">> folder %s was parsed <<", folderName)));
                    data.store(transientFolder);
                    matched = true;
                    break;
                }
                if (!matched) {
                    checkAdvanced(folderName);
                }
            }
            if (!matched) {
                logger.warn(logThreadMessage(String.format("unable to analyze or parse the folder: %s", data.path)));
            }
        } catch (Exception ex) {
            logger.error(logThreadMessage(ex.getMessage()), ex);
            Failures.addFailure();
        } finally {

        }
    }

    private void checkAdvanced(String description) {
        if (!plugConf.advanced) {
            return;
        }
        String[] words = new String[1];
        words[0] = description;
        if (description.contains(",")) {
            words = description.split(",");
        } else {
            words = description.split(" ");
        }

        Wiki wiki = null;
        if (plugConf.enableWikipedia) {
            wiki = new Wiki.Builder().build();
        }

        for (String word : words) {
            word = word.trim().toLowerCase().replace(",", "");
            if (word.length() <= 2) {
                continue;
            }
            if (find(plugConf.storedEvents, word)) {
                if (VERBOSE_MODE) {
                    logger.debug(logThreadMessage("added event " + word));
                }
                data.events.add(word);
            }
            if (find(plugConf.storedCities, word)) {
                if (VERBOSE_MODE) {
                    logger.debug(logThreadMessage("added city " + word));
                }
                data.cities.add(word);
            }
            if (find(plugConf.storedCountries, word)) {
                if (VERBOSE_MODE) {
                    logger.debug(logThreadMessage("added country " + word));
                }
                data.countries.add(word);
            }
            if (find(plugConf.storedPeople, word)) {
                if (VERBOSE_MODE) {
                    logger.debug(("added name " + word));
                }
                data.people.add(word);
            }
            if (wiki != null) {
                //https://github.com/fastily/jwiki
                //wiki.search(year, step, ns) 
            }
        }
    }

    private boolean find(List<String> inList, String word) {
        for (String curWord : inList) {
            if (curWord.equals(word)) {
                return true;
            }
        }
        return false;
    }

}
