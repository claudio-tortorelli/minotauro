package claudiosoft.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author claudio.tortorelli
 */
public class TimeUtils {

    public static DateTimeFormatter DT_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    public static DateTimeFormatter T_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private static List<Integer> workDays = new LinkedList<>(); // 1=Monday to 7=Sunday
    private static List<String> excludedDays = new LinkedList<>();

    private static TimeUtils utils;

    static {
        //TODO should be configurable
        excludedDays.add("0101");
        excludedDays.add("0106");
        excludedDays.add("0425");
        excludedDays.add("0501");
        excludedDays.add("0602");
        excludedDays.add("0815");
        excludedDays.add("1101");
        excludedDays.add("1208");
        excludedDays.add("1225");
        excludedDays.add("1231");
        try {
            excludedDays.add(TimeUtils.getEasterSundayDate());
            excludedDays.add(TimeUtils.getDayAfterEaster());
        } catch (MinException ex) {

        }

        workDays.add(1);
        workDays.add(2);
        workDays.add(3);
        workDays.add(4);
        workDays.add(5);
    }

    private TimeUtils() {

    }

    // https://stackoverflow.com/questions/26022233/calculate-the-date-of-easter-sunday
    public static String getEasterSundayDate() throws MinException {
        return getEasterSundayDate(0);
    }

    public static String getEasterSundayDate(int year) throws MinException {
        if (year == 0) {
            year = LocalDate.now().getYear();
        }

        // gauss algorithm
        int a = year % 19,
                b = year / 100,
                c = year % 100,
                d = b / 4,
                e = b % 4,
                g = (8 * b + 13) / 25,
                h = (19 * a + b - d - g + 15) % 30,
                j = c / 4,
                k = c % 4,
                m = (a + 11 * h) / 319,
                r = (2 * e + 2 * j - k - h + m + 32) % 7,
                n = (h - m + r + 90) / 25,
                p = (h - m + r + n + 19) % 32;

        String month = "";
        switch (n) {
            case 1:
                month = "01";
                break;
            case 2:
                month = "02";
                break;
            case 3:
                month = "03";
                break;
            case 4:
                month = "04";
                break;
            case 5:
                month = "05";
                break;
            case 6:
                month = "06";
                break;
            case 7:
                month = "07";
                break;
            case 8:
                month = "08";
                break;
            case 9:
                month = "09";
                break;
            case 10:
                month = "10";
                break;
            case 11:
                month = "11";
                break;
            case 12:
                month = "12";
                break;
            default:
                throw new MinException("invalid year");
        };

        return String.format("%s%02d", month, p);
    }

    public static String getDayAfterEaster() throws MinException {
        return getDayAfterEaster(0);
    }

    public static String getDayAfterEaster(int year) throws MinException {
        if (year == 0) {
            year = LocalDate.now().getYear();
        }
        String easter = String.format("%d%s", year, getEasterSundayDate());
        LocalDate easterD = stringToLocalDate(easter);
        return localDateToString(easterD.plusDays(1)).substring(4);
    }

    public static int daysBetween(LocalDate startDate, LocalDate endDate) {
        return daysBetween(startDate, endDate, false); //TODO handle last param by config
    }

    private static int daysBetween(LocalDate startDate, LocalDate endDate, boolean allDays) {
        //exclude particular dates and avoid negative
        if (startDate.equals(endDate)) {
            return 1;
        }
        if (startDate.isAfter(endDate)) {
            return 0;
        }

        int excluded = 0;
        if (!allDays) {
            LocalDate scanDate = startDate.minusDays(1);
            while (!scanDate.equals(endDate)) {
                scanDate = scanDate.plusDays(1);
                if (!TimeUtils.isWorkDay(scanDate)) {
                    excluded++;
                    continue;
                }
                String scanDateStr = localDateToString(scanDate).substring(4);
                if (excludedDays.indexOf(scanDateStr) >= 0) {
                    excluded++;
                }
            }
        }
        int spanDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1 - excluded;
        return Math.max(spanDays, 0);
    }

    public static LocalDate stringToLocalDate(String dateAAAAMMDD) throws DateTimeParseException {
        LocalDate localDate = LocalDate.parse(dateAAAAMMDD, DT_FORMATTER);
        return localDate;
    }

    public static String localDateToString(LocalDate dateAAAAMMDD) throws DateTimeParseException {
        return dateAAAAMMDD.format(DT_FORMATTER);
    }

    public static String nowToString() {
        return nowToString(DT_FORMATTER);
    }

    public static String nowToString(DateTimeFormatter formatter) {
        return LocalDateTime.now().format(formatter);
    }

    public static boolean isWorkDay(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        int curDay = day.getValue();
        return workDays.indexOf(curDay) != -1;
    }

    public static boolean isExcludedDay(LocalDate date) {
        String isoDate = date.format(DT_FORMATTER);
        for (String excluded : excludedDays) {
            if (isoDate.endsWith(excluded)) {
                return true;
            }
        }
        return false;
    }

    public static void setDayOfWeek(List<Integer> dayOfWeek) {
        TimeUtils.workDays = dayOfWeek;
    }

    public static void setExcludedDays(List<String> excludedDays) {
        TimeUtils.excludedDays = excludedDays;
    }

    public static LocalDate getShiftDateFromNow(int days) {
        return getShiftDate(LocalDate.now(), days);
    }

    public static LocalDate getShiftDate(LocalDate date, int days) {
        return date.plusDays(days);
    }

    public static String timeToString(LocalDateTime dateTime) {
        return dateTime.format(T_FORMATTER); //TODO hour only
    }

}
