package be.doji.productivity.trambuapp.controllers;

import be.doji.productivity.trambuapp.userconfiguration.UserConfigurationManager;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambucore.TrackMeConstants;
import be.doji.productivity.trambucore.managers.ActivityManager;
import be.doji.productivity.trambucore.managers.NoteManager;
import be.doji.productivity.trambucore.managers.TimeTrackingManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tornadofx.Controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;

public class ActivityController extends Controller {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityController.class);

    private ActivityManager activityManager;
    private TimeTrackingManager timeTrackingManager;
    private UserConfigurationManager configManager;
    private NoteManager noteManager;

    public ActivityController() {
        super();
        try {
            this.configManager = new UserConfigurationManager(DisplayConstants.NAME_CONFIGURATION_FILE);
            Optional<String> todoLocation = configManager.getProperty(DisplayConstants.NAME_PROPERTY_TODO_LOCATION);
            initializeActivities(todoLocation.orElse(TrackMeConstants.DEFAULT_TODO_FILE_LOCATION));
            Optional<String> timeLocation = configManager.getProperty(DisplayConstants.NAME_PROPERTY_TIME_LOCATION);
            initializeTimeTracking(timeLocation.orElse(TrackMeConstants.DEFAULT_TIMELOG_FILE_LOCATION));
            Optional<String> notesLocation = configManager.getProperty(DisplayConstants.NAME_PROPERTY_NOTES_LOCATION);
            initializeNotes(notesLocation.orElse(TrackMeConstants.DEFAULT_NOTE_DIRECTORY_LOCATION));
        } catch (IOException | ParseException e) {
            LOG.error(DisplayConstants.ERROR_MESSAGE_INITIALIZATION, e);
        }
    }

    private void initializeActivities(String fileLocation) throws IOException, ParseException {
        if (activityManager == null) {
            this.activityManager = new ActivityManager(fileLocation);
            this.activityManager.readActivitiesFromFile();
        }
    }

    private void initializeTimeTracking(String fileLocation) throws IOException, ParseException {
        if (timeTrackingManager == null) {
            this.timeTrackingManager = new TimeTrackingManager(fileLocation);
            this.timeTrackingManager.readLogs();
        }
    }

    private void initializeNotes(String noteLocation) throws IOException {
        if (noteManager == null) {
            this.noteManager = new NoteManager(noteLocation);
        }
    }

    public ActivityManager getActivityManager() {
        return activityManager;
    }

    public void setActivityManager(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }

    public TimeTrackingManager getTimeTrackingManager() {
        return timeTrackingManager;
    }

    public void setTimeTrackingManager(TimeTrackingManager timeTrackingManager) {
        this.timeTrackingManager = timeTrackingManager;
    }

    public UserConfigurationManager getConfigManager() {
        return configManager;
    }

    public void setConfigManager(UserConfigurationManager configManager) {
        this.configManager = configManager;
    }

    public boolean isSetFileOptions() {
        return configManager.containsProperty(DisplayConstants.NAME_PROPERTY_TODO_LOCATION) || configManager
                .containsProperty(DisplayConstants.NAME_PROPERTY_TIME_LOCATION);
    }

    public NoteManager getNoteManager() {
        return noteManager;
    }
}
