package be.doji.productivity.trambucore;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

/**
 * Created by Doji on 22/10/2017.
 */
public final class TrackMeConstants {

    /**
     * Utility classes should not have a public or default constructor
     */
    private TrackMeConstants() {
    }

    public static final String DEFAULT_TODO_FILE_LOCATION = "data/todo.txt";
    public static final String DEFAULT_TIMELOG_FILE_LOCATION = "data/timelog.txt";
    public static final String DEFAULT_NOTE_DIRECTORY_LOCATION = "data/notes";

    private static final String DATA_DATE_FORMAT = "yyyy-MM-dd:HH:mm:ss.SSS";
    public static final Duration DEFAULT_WARNING_PERIOD = Duration.ofDays(1);

    public static final String INDICATOR_DONE = "x";
    public static final String INDICATOR_PROJECT = "+";
    public static final String INDICATOR_TAG = "@";
    public static final String INDICATOR_DEADLINE = "due:";
    public static final String INDICATOR_WARNING_PERIOD = "warningPeriod:";
    public static final String INDICATOR_PARENT_ACTIVITY = "super:";
    public static final String INDICATOR_UUID = "uuid:";
    public static final String INDICATOR_LOCATION = "loc:";

    public static final String INDICATOR_LOG_START = "LOG_START";
    public static final String INDICATOR_LOG_END = "LOG_END";
    public static final String INDICATOR_LOGPOINT_START = "STARTTIME:";
    public static final String INDICATOR_LOGPOINT_END = "ENDTIME:";

    public static final String REGEX_DATE = "[0-9\\-\\:\\.]*";

    public static final String NOTES_FILE_EXTENSION = ".md";

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat(DATA_DATE_FORMAT, Locale.FRANCE);
    }

    public static List<String> getPriorityList() {
        String[] priorities = { "A", "B", "C", "D", "E", "F", "G", "H" };
        return Arrays.asList(priorities);
    }

    public static Date getDefaultDateHeader() {
        GregorianCalendar calendar = new GregorianCalendar(3550, Calendar.DECEMBER, 31, 0, 0, 0);
        return calendar.getTime();
    }

}
