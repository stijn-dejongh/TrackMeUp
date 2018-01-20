package be.doji.productivity.trambuapp.utils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

public final class TooltipConstants {

    /**
     * Utility classes should not have a public or default constructor
     */
    private TooltipConstants() {
    }

    public static final FontAwesomeIcon TOOLTIP_DEFAULT_ICON = FontAwesomeIcon.INFO_CIRCLE;
    public static final String TOOLTIP_TEXT_ACTIVITY_DONE = "Click to toggle activity completion on";
    public static final String TOOLTIP_TEXT_ACTIVITY_NOT_DONE = "Click to toggle activity completion off";
    public static final String TOOLTIP_TEXT_ACTIVITY_EDIT = "Make changes to the activity";
    public static final String TOOLTIP_TEXT_ACTIVITY_TIMING_CONTROL_START = "Start tracking time spent on activity";
    public static final String TOOLTIP_TEXT_ACTIVITY_TIMING_CONTROL_STOP = "Start tracking time spent on activity";
    public static final String TOOLTIP_TEXT_ACTIVITY_DELETE = "Permanently delete this activity (can not be undone)";
    public static final String TOOLTIP_TEXT_ACTIVITY_LOGPOINT_EXPAND = "Show registered timelogs";
    public static final String TOOLTIP_TEXT_ACTIVITY_NOTE_EXPAND = "Open the notes associated with this activity";
    public static final String TOOLTIP_TEXT_ACTIVITY_SAVE_NOTE = "Save your notes";

    public static final String TOOLTIP_TEXT_CONTROL_REFRESH = "Refresh data from files";
    public static final String TOOLTIP_TEXT_CONTROL_CREATE = "Create a new activity";
    public static final String TOOLTIP_TEXT_CONTROL_FILTER_RESET = "Clear active filters";
    public static final String TOOLTIP_TEXT_CONTROL_FILTER_DONE = "Filter all completed activities";

    public static final String TOOLTIP_TEXT_MENU_ACTIVITIES = "Navigate to the activity overview";
    public static final String TOOLTIP_TEXT_MENU_TIMESHEET = "Navigate to the activity timesheet";
    public static final String TOOLTIP_TEXT_MENU_OPTIONS = "Navigate to the application options";

    public static final String TOOLTIP_TEXT_OPTIONS_TODO_FILE_SELECT = "Select a file to save your activity data to";
    public static final String TOOLTIP_TEXT_OPTIONS_TIME_FILE_SELECT = "Select a file to save your timetracking data to";
    public static final String TOOLTIP_TEXT_OPTIONS_REMEMBER = "Click to make the application remember your choices when you close the program";


    public static final String TOOLTIP_TEXT_TIMESHEET_REFRESH = "Get timesheet for the selected interval";
    public static final String TOOLTIP_TEXT_TIMESHEET_EXPORT = "Export the timesheet to a CSV file";

}
