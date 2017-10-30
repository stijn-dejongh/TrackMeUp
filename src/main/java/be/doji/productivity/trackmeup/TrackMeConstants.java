package be.doji.productivity.TrackMeUp;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
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

    public static DateTimeFormatter DATA_DATE_FORMAT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd:HH:mm:ss.SSS", Locale.FRANCE);
    public static final Duration DEFAULT_WARNING_PERIOD = Duration.ofDays(1);

    public static final String INDICATOR_DONE = "X";
    public static final String INDICATOR_PROJECT = "+";
    public static final String INDICATOR_TAG = "@";
    public static final String INDICATOR_DEADLINE = "due:";
    public static final String INDICATOR_WARNING_PERIOD = "warningPeriod:";

}
