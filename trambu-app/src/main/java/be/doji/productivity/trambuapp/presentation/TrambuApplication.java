package be.doji.productivity.trambuapp.presentation;

import be.doji.productivity.trackme.TrackMeConstants;
import be.doji.productivity.trackme.managers.ActivityManager;
import be.doji.productivity.trackme.managers.TimeTrackingManager;
import be.doji.productivity.trambuapp.exception.InitialisationException;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.ParseException;

/**
 * @author Doji
 */
public class TrambuApplication extends Application {

    private ActivityManager am;
    private TimeTrackingManager tm;

    public void initialize() throws InitialisationException {
        try {
            initializeActivities(TrackMeConstants.DEFAULT_TODO_FILE_LOCATION);
            initializeTimeTracking(TrackMeConstants.DEFAULT_TIMELOG_FILE_LOCATION);
        } catch (IOException | ParseException e) {
            String errorMessage = "Error while initializing the application.";
            throw new InitialisationException(errorMessage, e);
        }
    }

    private void initializeActivities(String fileLocation) throws IOException, ParseException {
        if (am == null) {
            this.am = new ActivityManager(fileLocation);
            am.readActivitiesFromFile();
        }
    }

    private void initializeTimeTracking(String fileLocation) throws IOException, ParseException {
        if (tm == null) {
            this.tm = new TimeTrackingManager(fileLocation);
            tm.readLogs();
        }
    }

    public void start(Stage primaryStage) throws Exception {

    }
}
