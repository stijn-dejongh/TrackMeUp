package be.doji.productivity.trambuapp.presentation.util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class DisplayUtils {

    /**
     * Utility classes should now have a public or default constructor
     */
    private DisplayUtils() {}

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

}
