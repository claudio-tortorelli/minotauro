
import claudiosoft.commons.BasicLogger;
import claudiosoft.commons.Constants;
import claudiosoft.utils.BasicUtils;
import java.io.File;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author claudio.tortorelli
 */
public class TestParserFolder {

    public static void main(String[] args) throws Exception {

        LinkedList<Pattern> patterns = new LinkedList<>();

        patterns.add(Pattern.compile("[0-9]{4}\\s[0-9]{2}\\s.+", Pattern.CASE_INSENSITIVE)); //2008 04 bla
        patterns.add(Pattern.compile("[0-9]{4}", Pattern.CASE_INSENSITIVE)); //2002
        patterns.add(Pattern.compile("[0-9]{2}xx", Pattern.CASE_INSENSITIVE)); //19xx
        patterns.add(Pattern.compile("[0-9]{4}\\s[0-9]{2}.[0-9]{2}-[0-9]{1}\\s.+", Pattern.CASE_INSENSITIVE)); //2002 04.20-1 bla
        patterns.add(Pattern.compile("[0-9]{4}\\s[0-9]{2}.[0-9]{2}\\s.+", Pattern.CASE_INSENSITIVE)); //2002 04.20 bla
        patterns.add(Pattern.compile("[0-9]{4}.[0-9]{2}.[0-9]{2}", Pattern.CASE_INSENSITIVE)); //2002.05.20
        patterns.add(Pattern.compile("[0-9]{4}.[0-9]{2}\\s+", Pattern.CASE_INSENSITIVE)); //2002.05 bla
        patterns.add(Pattern.compile("[0-9]{4}\\s+", Pattern.CASE_INSENSITIVE)); //2002 bla
        patterns.add(Pattern.compile("\\s+[0-9]{4}", Pattern.CASE_INSENSITIVE)); //bla 2002
        patterns.add(Pattern.compile("\\s+[0-9]{2}-[0-9]{2}-[0-9]{4}", Pattern.CASE_INSENSITIVE)); //bla 10-02-2002 
        patterns.add(Pattern.compile("[0-9]{2}_[0-9]{2}_[0-9]{4}\\s+", Pattern.CASE_INSENSITIVE)); //10_02_2002 bla
        patterns.add(Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}", Pattern.CASE_INSENSITIVE)); //2002-02-20
        patterns.add(Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}\\s+", Pattern.CASE_INSENSITIVE)); //2002-02-20 bla

        File logFile = new File("./target/foldersNotRecognized.log");
        BasicLogger logger = BasicLogger.get(BasicLogger.LogLevel.DEBUG, Constants.LOGGER_NAME, logFile);
        logger.info("test patterns against folders\n----------------\n");

        File foldersFile = BasicUtils.getFileFromRes("folders.txt");
        List<String> fullPaths = Files.readAllLines(foldersFile.toPath());

        final int checkCondition = 7;
        for (String fullPath : fullPaths) {
            fullPath = fullPath.replace("\\", "/").toLowerCase();
            String[] folders = fullPath.split("/");
            int nMatcher = 0;
            for (String folder : folders) {
                for (Pattern pattern : patterns) {
                    Matcher matcher = pattern.matcher(folder);
                    if (matcher.find()) {
                        nMatcher++;
                    }
                }
            }
            if (nMatcher == checkCondition) {
                logger.info("check this folder " + fullPath);
            }
        }
    }
}
