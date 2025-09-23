package claudiosoft.baseplugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author claudio.tortorelli
 */
public class FolderPattern {

    public final String YEAR = "AAAA";
    public final String MONTH = "MM";
    public final String DAY = "DD";
    public final String DESC = "X";

    private PatternId id;
    private Pattern pattern;

    private String year;
    private String month;
    private String day;
    private String description;

    public FolderPattern(PatternId id, Pattern pattern) {
        this.id = id;
        this.pattern = pattern;
        this.year = "";
        this.month = "";
        this.description = "";
    }

    public PatternId getId() {
        return id;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public boolean applyPattern(String folder) {
        Matcher matcher = pattern.matcher(folder);
        if (!matcher.find()) {
            return false;
        }
        year = "";
        month = "";
        description = "";

        String schema = id.getSchema();
        int indexY = schema.indexOf(YEAR);
        if (indexY >= 0) {
            year = folder.substring(indexY, indexY + YEAR.length());
        }
        int indexM = schema.indexOf(MONTH);
        if (indexM >= 0) {
            month = folder.substring(indexM, indexM + MONTH.length());
        }
        int indexD = schema.indexOf(DAY);
        if (indexD >= 0) {
            day = folder.substring(indexD, indexD + DAY.length());
        }
        int minDataIndex = Math.min(Math.min(indexY, indexM), indexD);
        int index = schema.indexOf(DESC);
        if (index >= 0) {
            if (index == 0 && minDataIndex >= 0) {
                description = folder.substring(0, index + minDataIndex);
            } else {
                description = folder.substring(index, folder.length());
            }
        }
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

    @Override
    public String toString() {
        return String.format("year (%s), month (%s), description (%s)", year, month, description);
    }

}
