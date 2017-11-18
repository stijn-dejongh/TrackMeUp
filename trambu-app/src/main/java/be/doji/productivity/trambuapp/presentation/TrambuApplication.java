package be.doji.productivity.trambuapp.presentation;

import be.doji.productivity.trackme.TrackMeConstants;
import be.doji.productivity.trackme.managers.ActivityManager;
import be.doji.productivity.trackme.managers.TimeTrackingManager;
import be.doji.productivity.trackme.model.tasks.Activity;
import be.doji.productivity.trambuapp.components.ActivityNode;
import be.doji.productivity.trambuapp.exception.InitialisationException;
import be.doji.productivity.trambuapp.presentation.util.DisplayUtils;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Doji
 */
public class TrambuApplication extends Application {

    private static final double DEFAULT_WINDOW_WIDTH = 750.0;
    private static final double DEFAULT_WINDOW_HEIGHT = 850.0;

    private ActivityManager activityManager;
    private TimeTrackingManager timeTrackingManager;
    private Stage primaryStage;
    private Accordion activityAcordeon;
    private String tagFilter;
    private String projectFilter;
    private boolean filterDone = false;
    private Label activeFilter;

    private void initialize() throws InitialisationException {
        try {
            initializeActivities(TrackMeConstants.DEFAULT_TODO_FILE_LOCATION);
            initializeTimeTracking(TrackMeConstants.DEFAULT_TIMELOG_FILE_LOCATION);
        } catch (IOException | ParseException e) {
            String errorMessage = "Error while initializing the application.";
            throw new InitialisationException(errorMessage, e);
        }
    }

    private void initializeActivities(String fileLocation) throws IOException, ParseException {
        if (activityManager == null) {
            this.activityManager = new ActivityManager(fileLocation);
            activityManager.readActivitiesFromFile();
        }
    }

    private void initializeTimeTracking(String fileLocation) throws IOException, ParseException {
        if (timeTrackingManager == null) {
            this.timeTrackingManager = new TimeTrackingManager(fileLocation);
            timeTrackingManager.readLogs();
        }
    }

    @Override public void start(Stage primaryStage) throws Exception {
        initialize();
        primaryStage = createPrimaryStage();
        this.primaryStage = primaryStage;
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
        Accordion activities = createActivityAccordeon();
        Accordion controls = createControlsAccordeon();

        SplitPane splitPane = new SplitPane();
        splitPane.setPrefHeight(DEFAULT_WINDOW_HEIGHT);
        splitPane.setPrefWidth(DEFAULT_WINDOW_WIDTH);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.getItems().add(activities);
        splitPane.getItems().add(controls);
        splitPane.setDividerPosition(0, 0.65);
        return splitPane;
    }

    private Accordion createActivityAccordeon() {
        activityAcordeon = new Accordion();
        activityAcordeon.getPanes().addAll(createActivityNodes(activityManager.getActivitiesWithDateHeader()));
        return activityAcordeon;
    }

    private Accordion createControlsAccordeon() {
        Accordion accordion = new Accordion();
        accordion.getPanes().add(createFileOptionsControls());
        TitledPane generalControls = createGeneralControls();
        accordion.getPanes().add(generalControls);
        accordion.setExpandedPane(generalControls);
        accordion.getStylesheets().clear();
        accordion.getStylesheets().add("style/css/trambu-controls.css");
        return accordion;
    }

    private TitledPane createFileOptionsControls() {
        TitledPane gridTitlePane = new TitledPane();
        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.add(new Label("Todo file: "), 0, 0);

        FileChooser todoFileChooser = new FileChooser();
        todoFileChooser.setTitle("Open TODO list File");

        Button openTodoButton = new Button("Select TODO file");
        openTodoButton.setOnAction(e -> {
            File file = todoFileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    activityManager.updateFileLocation(file.getAbsolutePath());
                    updateActivities();
                } catch (IOException | ParseException e1) {
                    System.out.println("Error opening todo file: " + e1.getMessage());
                }
            }
        });
        grid.add(openTodoButton, 1, 0);

        grid.add(new Label("Timetracking file: "), 0, 1);
        FileChooser timeFileChooser = new FileChooser();
        timeFileChooser.setTitle("Open time tracking File");

        Button openTimeButton = new Button("Select timelog file");
        openTimeButton.setOnAction(e -> {
            File file = timeFileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    timeTrackingManager.updateFileLocation(file.getAbsolutePath());
                    updateActivities();
                } catch (IOException | ParseException e1) {
                    System.out.println("Error opening todo file: " + e1.getMessage());
                }
            }
        });
        grid.add(openTimeButton, 1, 1);

        gridTitlePane.setText("File Options");
        gridTitlePane.setContent(grid);
        gridTitlePane.setVisible(true);
        return gridTitlePane;
    }

    private TitledPane createGeneralControls() {
        TitledPane gridTitlePane = new TitledPane();
        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setPadding(new Insets(5, 5, 5, 5));

        Label filterLabel = new Label("Active filter: ");
        filterLabel.getStyleClass().clear();
        filterLabel.getStyleClass().add("separator-label");
        activeFilter = new Label(getActiveFilter());
        grid.add(filterLabel, 0, 0);
        grid.add(activeFilter, 1, 0);

        Button resetFilter = new Button("Reset filter");
        resetFilter.setOnAction(e -> {
            this.resetFilter();
            updateActivities();
        });
        grid.add(resetFilter, 1, 1);

        Button filterDone = new Button("Filter completed");
        filterDone.setOnAction(e -> {
            this.filterDone = true;
            updateFilterLabel();
            updateActivities();
        });
        grid.add(filterDone, 0, 1);

        gridTitlePane.setContent(grid);
        gridTitlePane.setText("General controls");
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

    public void updateActivities() {
        if (StringUtils.isNotBlank(getProjectFilter())) {
            this.updateActivities(activityManager.getActivitiesByProject(projectFilter));
        } else if (StringUtils.isNotBlank(this.getTagFilter())) {
            this.updateActivities(activityManager.getActivitiesByTag(tagFilter));
        } else {
            this.updateActivities(activityManager.getActivitiesWithDateHeader());
        }
    }

    private void updateActivities(Map<Date, List<Activity>> activities) {
        ObservableList<TitledPane> panes = this.activityAcordeon.getPanes();
        panes.clear();
        panes.addAll(createActivityNodes(activities));
    }

    private List<TitledPane> createActivityNodes(Map<Date, List<Activity>> activitiesWithHeader) {
        List<TitledPane> panes = new ArrayList<>();
        for (Map.Entry<Date, List<Activity>> activityWithHeader : activitiesWithHeader.entrySet()) {

            List<ActivityNode> activities = activityWithHeader.getValue().stream()
                    .filter(activity -> !(activity.isCompleted() && this.filterDone))
                    .map(activity -> new ActivityNode(activity, this)).collect(Collectors.toList());
            if (!activities.isEmpty()) {
                panes.add(createSeperatorPane(activityWithHeader.getKey()));
                panes.addAll(activities);
            }

        }
        return panes;
    }

    private TitledPane createSeperatorPane(Date key) {
        TitledPane headerPane = new TitledPane();
        String formattedDate = DisplayUtils.getDateSeperatorText(key);

        headerPane.setText(formattedDate);
        headerPane.setCollapsible(false);
        headerPane.setStyle("-fx-start-margin: 15px;");
        return headerPane;
    }

    public static void main(String[] args) {
        Application.launch(TrambuApplication.class);
    }

    public ActivityManager getActivityManager() {
        return activityManager;
    }

    public TimeTrackingManager getTimeTrackingManager() {
        return timeTrackingManager;
    }

    public String getTagFilter() {
        return tagFilter;
    }

    public void setTagFilter(String tagFilter) {
        this.resetFilter();
        this.tagFilter = tagFilter;
        this.updateFilterLabel();
    }

    public String getProjectFilter() {
        return projectFilter;
    }

    public void setProjectFilter(String projectFilter) {
        this.resetFilter();
        this.projectFilter = projectFilter;
        this.updateFilterLabel();
    }

    private void resetFilter() {
        this.tagFilter = "";
        this.projectFilter = "";
        this.filterDone = false;
        this.updateFilterLabel();
    }

    private void updateFilterLabel() {
        activeFilter.setText(getActiveFilter());
    }

    public String getActiveFilter() {
        return StringUtils.isNotBlank(tagFilter)?
                tagFilter:
                StringUtils.isNotBlank(projectFilter)?
                        projectFilter:
                        this.filterDone?"Filter completed activities":"No active filter";
    }
}
