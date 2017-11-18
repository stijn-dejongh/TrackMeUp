package be.doji.productivity.trambuapp.presentation;

import be.doji.productivity.trackme.TrackMeConstants;
import be.doji.productivity.trackme.managers.ActivityManager;
import be.doji.productivity.trackme.managers.TimeTrackingManager;
import be.doji.productivity.trackme.model.tasks.Activity;
import be.doji.productivity.trambuapp.exception.InitialisationException;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Doji
 */
public class TrambuApplication extends Application {

    private static final double DEFAULT_WINDOW_WIDTH = 650.0;
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
        primaryStage = createPrimaryStage();
        primaryStage.show();

    }

    private Stage createPrimaryStage() {
        Stage primaryStage;
        primaryStage = new Stage();
        primaryStage.setTitle("Track My Bitch Up");
        primaryStage.setMinWidth(DEFAULT_WINDOW_WIDTH);
        primaryStage.setMinHeight(DEFAULT_WINDOW_HEIGHT);
        primaryStage.setScene(createRootScene(createContentSplitPane()));
        return primaryStage;
    }

    private SplitPane createContentSplitPane() {
        Accordion accordion = createActivityAccordeon();
        Accordion controls = createControlsAccordeon();

        SplitPane splitPane = new SplitPane();
        splitPane.setPrefHeight(DEFAULT_WINDOW_HEIGHT);
        splitPane.setPrefWidth(DEFAULT_WINDOW_WIDTH);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.getItems().add(accordion);
        splitPane.getItems().add(controls);
        return splitPane;
    }

    private Accordion createActivityAccordeon() {
        Accordion accordion = new Accordion();
        accordion.getPanes().addAll(createActivityNodes());
        return accordion;
    }

    private Accordion createControlsAccordeon() {
        Accordion accordion = new Accordion();
        accordion.getPanes().addAll(createFileOptionsControls());
        accordion.getStylesheets().clear();
        accordion.getStylesheets().add("style/css/trambu-controls.css");
        return accordion;
    }

    private TitledPane createFileOptionsControls() {
        TitledPane gridTitlePane = new TitledPane();
        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.add(new Label("To: "), 0, 0);
        grid.add(new TextField(), 1, 0);
        grid.add(new Label("Cc: "), 0, 1);
        grid.add(new TextField(), 1, 1);
        grid.add(new Label("Subject: "), 0, 2);
        grid.add(new TextField(), 1, 2);
        grid.add(new Label("Attachment: "), 0, 3);
        gridTitlePane.setText("File Options");
        gridTitlePane.setContent(grid);
        gridTitlePane.setVisible(true);
        return gridTitlePane;
    }

    private Scene createRootScene(SplitPane splitPane) {
        Scene rootScene = new Scene(new Group(), DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
        rootScene.getStylesheets().add("style/css/trambu-main.css");
        rootScene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            System.out.println("Width: " + newSceneWidth);
            splitPane.setPrefWidth((Double) newSceneWidth);
        });
        rootScene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
            System.out.println("Height: " + newSceneHeight);
            splitPane.setPrefHeight((Double) newSceneHeight);
        });

        Group root = (Group) rootScene.getRoot();
        root.getChildren().add(splitPane);

        return rootScene;
    }

    private List<TitledPane> createActivityNodes() {
        return am.getActivities().stream().map(activity -> createActivityNode(activity)).collect(Collectors.toList());
    }

    private TitledPane createActivityNode(Activity activity) {
        Label content = new Label();
        content.setText("This will contain information about the activity");
        TitledPane titledPane = new TitledPane(activity.getName(), content);
        Button titleLabel = AwesomeDude
                .createIconButton(activity.isCompleted()?AwesomeIcon.CHECK_SIGN:AwesomeIcon.CHECK_EMPTY);
        titledPane.setGraphic(titleLabel);
        titledPane.getStyleClass().clear();
        titledPane.getStyleClass().add(activity.isCompleted()?"done" :"todo");
        return titledPane;
    }

    public static void main(String[] args) {
        Application.launch(TrambuApplication.class);
    }
}
