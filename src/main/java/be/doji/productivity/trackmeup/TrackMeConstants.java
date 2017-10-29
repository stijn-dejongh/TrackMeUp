package be.doji.productivity.TrackMeUp;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Doji on 22/10/2017.
 */
public final class TrackMeConstants {

    public static final int PORT_TO_USE = 9666;

    private TrackMeConstants() {
    }

    public static SimpleDateFormat DATA_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss.SSS", Locale.FRANCE);
}
