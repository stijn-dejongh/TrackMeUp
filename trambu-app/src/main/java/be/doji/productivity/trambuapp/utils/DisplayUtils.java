package be.doji.productivity.trambuapp.utils;

import be.doji.productivity.trambucore.model.tasks.Activity;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class DisplayUtils {

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

    private static boolean isWithinYearRange(GregorianCalendar calendarOfHeader, GregorianCalendar calendarOfToday) {
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

    public static Tooltip createTooltip(String toolTipText) {
        return createTooltip(toolTipText, TooltipConstants.TOOLTIP_DEFAULT_ICON);
    }

    public static Tooltip createTooltip(String tooltipText, FontAwesomeIcon tooltipIconDone) {
        Tooltip tooltip = new Tooltip();
        tooltip.setText(tooltipText);
        tooltip.setGraphic(createStyledIcon(tooltipIconDone));
        return tooltip;
    }

    public static FontAwesomeIconView createStyledIcon(FontAwesomeIcon icon) {
        FontAwesomeIconView tooltipIcon = new FontAwesomeIconView(icon);
        tooltipIcon.setGlyphStyle(DisplayConstants.STYLE_GLYPH_DEFAULT);
        return tooltipIcon;
    }

}
