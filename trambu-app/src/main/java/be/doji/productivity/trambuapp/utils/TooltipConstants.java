package be.doji.productivity.trambuapp.utils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

public final class TooltipConstants {

    /**
     * Utility classes should not have a public or default constructor
     */
    private TooltipConstants() {
    }

    public static final FontAwesomeIcon TOOLTIP_DEFAULT_ICON = FontAwesomeIcon.INFO_CIRCLE;
    public static final String TOOLTIP_TEXT_DONE = "Click when you have completed the activity";
    public static final String TOOLTIP_TEXT_EDIT = "Make changes to the activity";
    public static final String TOOLTIP_TEXT_TIMING_CONTROL_START = "Start tracking time spent on activity";
    public static final String TOOLTIP_TEXT_TIMING_CONTROL_STOP = "Start tracking time spent on activity";
    public static final String TOOLTIP_TEXT_DELETE = "Permanently delete this activity (can not be undone)";
}
