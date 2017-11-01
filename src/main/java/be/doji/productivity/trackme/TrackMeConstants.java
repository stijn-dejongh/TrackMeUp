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

    public static final SimpleDateFormat DATA_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss.SSS", Locale.FRANCE);
    public static final Duration DEFAULT_WARNING_PERIOD = Duration.ofDays(1);
    public static final Date DEFAULT_DATE_HEADER = new Date(3550, 12 ,31);

    public static final String INDICATOR_DONE = "X";
    public static final String INDICATOR_PROJECT = "+";
    public static final String INDICATOR_TAG = "@";
    public static final String INDICATOR_DEADLINE = "due:";
    public static final String INDICATOR_WARNING_PERIOD = "warningPeriod:";
    public static final String INDICATOR_PARENT_ACTIVITY = "super:";

}
