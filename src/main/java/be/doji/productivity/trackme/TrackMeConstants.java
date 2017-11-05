package be.doji.productivity.trackme;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
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

    public static final String DATA_DATE_FORMAT = "yyyy-MM-dd:HH:mm:ss.SSS";
    public static final Duration DEFAULT_WARNING_PERIOD = Duration.ofDays(1);
    public static final Date DEFAULT_DATE_HEADER = new Date(3550, 12, 31);

    public static final String INDICATOR_DONE = "X";
    public static final String INDICATOR_PROJECT = "+";
    public static final String INDICATOR_TAG = "@";
    public static final String INDICATOR_DEADLINE = "due:";
    public static final String INDICATOR_WARNING_PERIOD = "warningPeriod:";
    public static final String INDICATOR_PARENT_ACTIVITY = "super:";

    public static final String INDICATOR_LOG_START = "LOG_START";
    public static final String INDICATOR_LOG_END = "LOG_END";
    public static final String INDICATOR_LOGPOINT_START = "STARTTIME:";
    public static final String INDICATOR_LOGPOINT_END = "ENDTIME:";

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat(DATA_DATE_FORMAT, Locale.FRANCE);
    }
}
