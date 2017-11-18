package be.doji.productivity.trambuapp.presentation;

import be.doji.productivity.trackme.TrackMeConstants;
import be.doji.productivity.trackme.managers.ActivityManager;
import be.doji.productivity.trackme.managers.TimeTrackingManager;
import be.doji.productivity.trackme.model.tasks.Activity;
import be.doji.productivity.trambuapp.exception.InitialisationException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Doji
 */
public class TrambuApplication extends Application {

    private static final double DEFAULT_WINDOW_WIDTH = 450.0;
    private static final double DEFAULT_WINDOW_HEIGHT = 650.0;

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

    @Override public void start(Stage primaryStage) throws Exception {
        initialize();

        primaryStage = new Stage();
        primaryStage.setTitle("Track My Bitch Up");

        Accordion accordion = new Accordion();
        accordion.getPanes().addAll(createActivityNodes());
        Scene rootScene = new Scene(accordion, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);

        primaryStage.setScene(rootScene);
        primaryStage.show();

    }

    private List<TitledPane> createActivityNodes() {
        //        ArrayList<Node> nodes = new ArrayList<>();
        //        for (Activity activity : am.getActivities()) {
        //            nodes.add(createActivityNode(activity));
        //        }

        return am.getActivities().stream().map(activity -> createActivityNode(activity)).collect(Collectors.toList());
    }

    private TitledPane createActivityNode(Activity activity) {
        Label content = new Label();
        content.setText("This will contain information about the activity");
        return new TitledPane("[" + (activity.isCompleted()?"DONE":"TODO") + "]" + " " + activity.getName(), content);
    }

    public static void main(String[] args) {
        Application.launch(TrambuApplication.class);
    }
}
