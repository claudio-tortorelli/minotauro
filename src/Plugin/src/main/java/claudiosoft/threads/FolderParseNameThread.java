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

/**
 *
 * @author claudio.tortorelli
 */
public class FolderParseNameThread extends PluginThread {

    private final FolderParseNameConfig plugConf;
    private final BeanFolderParseName data;

    private final boolean VERBOSE_MODE = true;

    public FolderParseNameThread(File curFolder, FolderParseNameConfig plugConf, BeanFolderParseName data) throws CTException {
        super(curFolder);
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
                if (plugConf.storedTools.contains(folderName)) {
                    data.elaborated = true;
                    continue;
                }

                //TODO: gestire nell'indexer le folder skipped
                for (FolderPattern pattern : plugConf.foldPatterns) {
                    if (!pattern.applyPattern(folderName)) {
                        if (VERBOSE_MODE) {
                            logger.debug("NO pattern " + pattern.getPattern().pattern());
                        }
                        continue;
                    }
                    data.year = pattern.getYear();
                    data.month = pattern.getMonth();
                    data.description = pattern.getDescription();

                    if (VERBOSE_MODE) {
                        logger.debug("MATCH with pattern " + pattern.getId().name() + " " + pattern.getPattern().pattern() + " and schema " + pattern.getId().getSchema() + "\n\tgot " + pattern.toString());
                    }

                    checkAdvanced(pattern.getDescription());

                    logger.debug(String.format("folder %s was parsed", folderName));
                    data.store(transientFolder);
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                logger.warn(String.format("unable to analyze or parse the folder: %s", data.path));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
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
            word = word.trim();
            word = word.replace(",", "");
            if (word.length() <= 2) {
                continue;
            }
            if (plugConf.storedEvents.contains(word)) {
                data.events.add(word);
                continue;
            }
            if (plugConf.storedCities.contains(word)) {
                data.cities.add(word);
                continue;
            }
            if (plugConf.storedCountries.contains(word)) {
                data.countries.add(word);
                continue;
            }
            if (plugConf.storedNames.contains(word)) {
                data.people.add(word);
                continue;
            }
            if (wiki != null) {
                //https://github.com/fastily/jwiki
                //wiki.search(year, step, ns) 
            }
        }
    }

}
