package be.doji.productivity.trambuapp.utils;

import be.doji.productivity.trambuapp.components.ActivityNode;
import be.doji.productivity.trambucore.model.tasks.Activity;
import javafx.geometry.Orientation;
import javafx.scene.control.Accordion;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

public final class DisplayUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DisplayUtils.class);

    /**
     * Utility classes should now have a public or default constructor
     */
    private DisplayUtils() {
    }

    public static String getDateSeperatorText(Date key) {
        GregorianCalendar calendarOfHeader = new GregorianCalendar();
        calendarOfHeader.setTime(key);
        GregorianCalendar calendarOfToday = new GregorianCalendar();
        calendarOfToday.setTime(new Date());
        return isWithinYearRange(calendarOfHeader, calendarOfToday)?
                DateFormat.getDateInstance(DateFormat.DEFAULT).format(key):
                "No deadline in sight";
    }

    public static boolean isWithinYearRange(GregorianCalendar calendarOfHeader, GregorianCalendar calendarOfToday) {
        return calendarOfHeader.get(Calendar.YEAR) - calendarOfToday.get(Calendar.YEAR) < 25;
    }

    public static String getDoneButtonText(Activity activity) {
        return activity.isCompleted()?DisplayConstants.BUTTON_TEXT_IS_NOT_DONE:DisplayConstants.BUTTON_TEXT_IS_DONE;
    }

    public static Separator createHorizontalSpacer() {
        Separator sep = new Separator();
        sep.setOrientation(Orientation.HORIZONTAL);
        return sep;
    }
}
