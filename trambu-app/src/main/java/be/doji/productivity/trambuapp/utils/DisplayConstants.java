package be.doji.productivity.trambuapp.utils;

public final class DisplayConstants {

    /**
     * Utility classes should not have a public or default constructor
     */
    private DisplayConstants() {
    }

    public static final String STYLE_GLYPH_DEFAULT = "-fx-fill: linear-gradient(#ffffff, #d2d2d2); -fx-effect: dropshadow( one-pass-box , rgba(0,0,0,0.8) , 4 , 0.0 , 1 , 1 );";
    public static final String STYLE_CLASS_ACTIVITY_DONE = "done";
    public static final String STYLE_CLASS_ACTIVITY_TODO = "todo";
    public static final String STYLE_CLASS_ACTIVITY_ALERT = "alert";
    public static final String STYLE_LABEL_SPECIAL = "separatorLabel";

    public static final String BUTTON_TEXT_IS_NOT_DONE = "Not Done";
    public static final String BUTTON_TEXT_IS_DONE = "Done!";
    public static final String BUTTON_TEXT_SAVE = "Save";
    public static final String BUTTON_TEXT_EDIT = "Edit";
    public static final String BUTTON_TEXT_DELETE = "Delete";
    public static final String BUTTON_TEXT_TIMER_START = "Start timing";
    public static final String BUTTON_TEXT_TIMER_STOP = "Stop timing";

    public static final String LABEL_TEXT_FILTER_COMPLETED = "Filter completed activities";
    public static final String LABEL_TEXT_FILTER_NONE = "No active filter";

    public static final String TEXT_ACTIVITY_UNKNOWN = "Unknown activity";

    public static final String NAME_CONFIGURATION_FILE = "trambu.conf";
    public static final String NAME_PROPERTY_TODO_LOCATION = "todoFile";
    public static final String NAME_PROPERTY_TIME_LOCATION = "timeFile";

    public static final String ERROR_MESSAGE_ACTIVITY_SAVING = "Error while saving activity";
    public static final String ERROR_MESSAGE_INITIALIZATION = "Error while initializing application";

    public static final String ERROR_MESSAGE_WRITE_PROPERTIES = "Error while saving preferences";

    public static final double UI_DEFAULT_WINDOW_WIDTH = 750.0;
    public static final double UI_DEFAULT_WINDOW_HEIGHT = 850.0;

}
