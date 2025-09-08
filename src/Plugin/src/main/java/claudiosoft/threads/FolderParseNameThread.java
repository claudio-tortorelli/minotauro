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

            String[] folders = data.path.split("/");
            if (folders.length < 2) {
                logger.error(String.format("unable to extract target folder from %s", data.path));
                return;
            }

            boolean matched = false;
            for (int i = folders.length - 1; i >= 0; i--) {
                String folderName = folders[i];
                if (plugConf.storedTools.contains(folderName)) {
                    data.elaborated = true;
                    continue;
                }

                //folderName = folderName.replace(".", " ");
                //folderName = folderName.replace("-", " ");
                //TODO: gestire nell'indexer le folder skipped
                //TODO: qui va gestito il pattern matching multiplo. Inoltre capire se opzionalmente è da evitare l'indicizzazione di cartelle che non riportano l'anno nel path
                for (FolderPattern pattern : plugConf.foldPatterns) {
                    if (!pattern.applyPattern(folderName)) {
                        continue;
                    }

                    checkAdvanced(pattern.getDescription());

                    String[] fields = folderName.split(" ");
                    if (fields.length < 3) {
                        logger.error(String.format("unable to extract fields from %s", data.path));
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

                    checkAdvanced(data.description);

                    logger.debug(String.format("found this folder %s", folderName));
                    data.store(transientFolder);
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
