package be.doji.productivity.trackme;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    public static final String DATA_DATE_FORMAT = "yyyy-MM-dd:HH:mm:ss.SSS";
    public static final Duration DEFAULT_WARNING_PERIOD = Duration.ofDays(1);
    public static final Date DEFAULT_DATE_HEADER = new Date(3550, 12, 31);

    public static final String INDICATOR_DONE = "X";
    public static final String INDICATOR_PROJECT = "+";
    public static final String INDICATOR_TAG = "@";
    public static final String INDICATOR_DEADLINE = "due:";
    public static final String INDICATOR_WARNING_PERIOD = "warningPeriod:";
    public static final String INDICATOR_PARENT_ACTIVITY = "super:";
    public static final String INDICATOR_UUID = "uuid:";

    public static final String INDICATOR_LOG_START = "LOG_START";
    public static final String INDICATOR_LOG_END = "LOG_END";
    public static final String INDICATOR_LOGPOINT_START = "STARTTIME:";
    public static final String INDICATOR_LOGPOINT_END = "ENDTIME:";

    public static final String REGEX_DATE = "[0-9\\-\\:\\.]*";

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat(DATA_DATE_FORMAT, Locale.FRANCE);
    }

    public static List<String> getPriorityList() {
        String[] priorities = { "A", "B", "C", "D", "E", "F", "G", "H" };
        List<String> priorityList = Arrays.asList(priorities);
        return priorityList;
    }
}
