package claudiosoft.baseplugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author claudio.tortorelli
 */
public class FolderPattern {

    private PatternId id;
    private Pattern pattern;

    private String year;
    private String month;
    private String day;
    private String description;

    public FolderPattern(PatternId id, Pattern pattern) {
        this.id = id;
        this.pattern = pattern;
    }

    public PatternId getId() {
        return id;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public boolean applyPattern(String folder) {
        Matcher matcher = pattern.matcher(folder);
        if (matcher.find()) {
            return false;
        }
        // TODO split in base allo schema
        return true;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getDescription() {
        return description;
    }

}
