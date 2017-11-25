package be.doji.productivity.trambuapp.presentation;

import be.doji.productivity.trambuapp.components.ActivityAcordeon;
import be.doji.productivity.trambuapp.exception.InitialisationException;
import be.doji.productivity.trambuapp.userconfiguration.UserConfigurationManager;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambucore.TrackMeConstants;
import be.doji.productivity.trambucore.managers.ActivityManager;
import be.doji.productivity.trambucore.managers.TimeTrackingManager;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.model.tracker.ActivityLog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author Doji
 */
public class TrambuApplication extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(TrambuApplication.class);

    private static final double DEFAULT_WINDOW_WIDTH = 750.0;
    private static final double DEFAULT_WINDOW_HEIGHT = 850.0;

    private UserConfigurationManager configManager;
    private ActivityManager activityManager;
    private TimeTrackingManager timeTrackingManager;
    private Stage primaryStage;
    private ActivityAcordeon activityAcordeon;
    private String tagFilter;
    private String projectFilter;
    private boolean filterDone = false;
    private Label activeFilter;
    private Date startDate;
    private Date endDate;

    private String configuredTodoLocation;
    private String configuredTimeLocation;

    public static void main(String[] args) {
        Application.launch(TrambuApplication.class);
    }

    @Override public void start(Stage primaryStage) throws Exception {
        initialize();
        this.primaryStage = createPrimaryStage();
        this.primaryStage.show();
    }

    @Override public void stop() {
        getTimeTrackingManager().stopAll();
    }

    private void initialize() throws InitialisationException {
        try {
            configManager = new UserConfigurationManager(DisplayConstants.NAME_CONFIGURATION_FILE);
            Optional<String> todoLocation = configManager.getProperty(DisplayConstants.NAME_PROPERTY_TODO_LOCATION);
            initializeActivities(todoLocation.orElse(TrackMeConstants.DEFAULT_TODO_FILE_LOCATION));
            Optional<String> timeLocation = configManager.getProperty(DisplayConstants.NAME_PROPERTY_TIME_LOCATION);
            initializeTimeTracking(timeLocation.orElse(TrackMeConstants.DEFAULT_TIMELOG_FILE_LOCATION));
            endDate = new Date();
            startDate = DateUtils.addDays(endDate, -7);
        } catch (IOException | ParseException e) {
            LOG.error(DisplayConstants.ERROR_MESSAGE_INITIALIZATION + ": " + e.getMessage());
            throw new InitialisationException(DisplayConstants.ERROR_MESSAGE_INITIALIZATION, e);
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

    private Stage createPrimaryStage() {
        Stage rootStage = new Stage();
        rootStage.setTitle("Track My Bitch Up");
        rootStage.setMinWidth(DEFAULT_WINDOW_WIDTH);
        rootStage.setMinHeight(DEFAULT_WINDOW_HEIGHT);
        rootStage.setScene(createRootScene(createContentSplitPane()));
        return rootStage;
    }

    private SplitPane createContentSplitPane() {
        ScrollPane activitySplitPane = new ScrollPane();
        Accordion activities = createActivityAccordeon();
        activitySplitPane.setContent(activities);
        activitySplitPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Accordion controls = createControlsAccordeon();

        SplitPane splitPane = new SplitPane();
        splitPane.setPrefHeight(DEFAULT_WINDOW_HEIGHT);
        splitPane.setPrefWidth(DEFAULT_WINDOW_WIDTH);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.getItems().add(activitySplitPane);
        splitPane.getItems().add(controls);
        splitPane.setDividerPosition(0, 0.65);
        return splitPane;
    }

    private Accordion createActivityAccordeon() {
        activityAcordeon = new ActivityAcordeon(this, activityManager.getActivitiesWithDateHeader());
        return activityAcordeon;
    }

    private Accordion createControlsAccordeon() {
        Accordion accordion = new Accordion();
        TitledPane fileOptionsControls = createFileOptionsControls();
        accordion.getPanes().add(fileOptionsControls);
        TitledPane generalControls = createGeneralControls();
        accordion.getPanes().add(generalControls);
        if (!isSetFileOptions()) {
            accordion.setExpandedPane(fileOptionsControls);
        } else {
            accordion.setExpandedPane(generalControls);
        }

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

        Button openTodoButton = createOpenFileButton("Select TODO file", todoFileChooser, file -> {
            try {
                String filePath = file.getAbsolutePath();
                this.configuredTodoLocation = filePath;
                activityManager.updateFileLocation(filePath);
                reloadActivities();
            } catch (IOException | ParseException e) {
                LOG.error("Error opening todo file", e);
            }
        });
        grid.add(openTodoButton, 1, 0);

        grid.add(new Label("Timetracking file: "), 0, 1);
        FileChooser timeFileChooser = new FileChooser();
        timeFileChooser.setTitle("Open time tracking File");

        Button openTimeButton = createOpenFileButton("Select timelog file", timeFileChooser, file -> {
            try {
                String filePath = file.getAbsolutePath();
                this.configuredTimeLocation = filePath;
                timeTrackingManager.updateFileLocation(filePath);
            } catch (IOException | ParseException e) {
                LOG.error("Error opening time tracking file", e);
            }
        });
        grid.add(openTimeButton, 1, 1);
        grid.add(DisplayUtils.createHorizontalSpacer(), 0, 2, 2, 1);

        Button savePreferences = new Button("Remember choices");
        savePreferences.setOnAction(event -> {
            if (StringUtils.isNotBlank(configuredTodoLocation)) {
                configManager.addProperty(DisplayConstants.NAME_PROPERTY_TODO_LOCATION, configuredTodoLocation);
            }
            if (StringUtils.isNotBlank(configuredTimeLocation)) {
                configManager.addProperty(DisplayConstants.NAME_PROPERTY_TIME_LOCATION, configuredTimeLocation);
            }
            try {
                configManager.writeToFile();
            } catch (IOException e) {
                LOG.error(DisplayConstants.ERROR_MESSAGE_WRITE_PROPERTIES, e);
            }
        });
        grid.add(savePreferences, 0, 3);

        gridTitlePane.setText("File Options");
        gridTitlePane.setContent(grid);
        gridTitlePane.setVisible(true);
        return gridTitlePane;
    }

    private Button createOpenFileButton(String buttonText, FileChooser fileChooser, Consumer<File> fileLambda) {
        Button button = new Button(buttonText);
        button.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                fileLambda.accept(file);
                reloadActivities();
            }
        });
        return button;
    }

    private TitledPane createGeneralControls() {
        TitledPane gridTitlePane = new TitledPane();
        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(5);
        grid.setPadding(new Insets(5, 5, 5, 5));

        Label filterLabel = new Label("Active filter: ");
        filterLabel.getStyleClass().clear();
        filterLabel.getStyleClass().add(DisplayConstants.STYLE_LABEL_SPECIAL);
        activeFilter = new Label(getActiveFilter());
        grid.add(filterLabel, 0, 0);
        grid.add(activeFilter, 1, 0);

        Button filterButton = new Button("Filter completed");
        filterButton.setOnAction(e -> {
            this.filterDone = true;
            updateFilterLabel();
            reloadActivities();
        });
        grid.add(filterButton, 0, 1);

        Button resetFilter = new Button("Reset filter");
        resetFilter.setOnAction(e -> {
            this.resetFilter();
            reloadActivities();
        });
        grid.add(resetFilter, 1, 1);

        grid.add(DisplayUtils.createHorizontalSpacer(), 0, 2, 2, 1);

        Button addActivity = new Button("Add activity");
        FontAwesomeIconView addIcon = new FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE);
        addIcon.setGlyphStyle(DisplayConstants.STYLE_GLYPH_DEFAULT);
        addActivity.setGraphic(addIcon);

        addActivity.setOnAction(event -> {
            try {
                Activity newActivity = new Activity("EDIT ME I AM A NEW ACTIVITY");
                this.activityManager.save(newActivity);
                this.reloadActivities();
            } catch (IOException | ParseException exception) {
                LOG.error("Error creation new activity", exception);
            }
        });
        grid.add(addActivity, 0, 3);

        grid.add(DisplayUtils.createHorizontalSpacer(), 0, 4, 2, 1);

        Button refresh = new Button("");
        refresh.setOnAction(event -> this.reloadActivities());
        FontAwesomeIconView glyph = new FontAwesomeIconView(FontAwesomeIcon.REFRESH);
        glyph.setGlyphStyle(DisplayConstants.STYLE_GLYPH_DEFAULT);
        refresh.setGraphic(glyph);

        grid.add(refresh, 0, 5);

        gridTitlePane.setContent(grid);
        gridTitlePane.setText("General controls");
        return gridTitlePane;
    }

    private Scene createRootScene(SplitPane defaultPane) {
        Scene rootScene = new Scene(new Group(), DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
        rootScene.getStylesheets().add("style/css/trambu-main.css");
        Group root = (Group) rootScene.getRoot();

        BorderPane borderPane = new BorderPane();
        MenuBar topMenu = createMenuBar(rootScene.getWidth());

        rootScene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            defaultPane.setPrefWidth((Double) newSceneWidth);
        });
        rootScene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
            defaultPane.setPrefHeight((Double) newSceneHeight - topMenu.getHeight());
        });

        borderPane.setTop(topMenu);
        borderPane.setCenter(defaultPane);
        root.getChildren().add(borderPane);

        return rootScene;
    }

    private MenuBar createMenuBar(double width) {
        MenuBar topMenu = new MenuBar();
        topMenu.setPrefWidth(width);

        MenuItem activityView = new Menu("Activities");
        activityView.setOnAction(event -> this.primaryStage.setScene(createRootScene(createContentSplitPane())));

        MenuItem timeOverview = new Menu("Timesheet");
        timeOverview.setOnAction(event -> this.primaryStage.setScene(createRootScene(createTimesheetPane())));

        Menu viewMenu = new Menu("View");
        viewMenu.getItems().add(activityView);
        viewMenu.getItems().add(timeOverview);

        topMenu.getMenus().add(viewMenu);

        return topMenu;
    }

    private SplitPane createTimesheetPane() {
        SplitPane splitPane = new SplitPane();
        splitPane.setPrefHeight(DEFAULT_WINDOW_HEIGHT);
        splitPane.setPrefWidth(DEFAULT_WINDOW_WIDTH);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPosition(0, 0.75);

        splitPane.getItems().add(createTimeLogGrid(timeTrackingManager.getActivityLogsInInterval(startDate, endDate)));
        splitPane.getItems().add(createTimesheetControls());
        return splitPane;
    }

    private GridPane createTimesheetControls() {

        GridPane controls = new GridPane();
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setOnAction(event -> {
            startDate = Date.from(startDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        });
        Label startDateLabel = new Label("Start Date");
        controls.add(startDateLabel, 0, 0);
        controls.add(startDatePicker, 1, 0);

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setOnAction(event -> {
            endDate = Date.from(endDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        });

        Label endDateLabel = new Label("End Date");
        controls.add(endDateLabel, 0, 1);
        controls.add(endDatePicker, 1, 1);

        controls.add(DisplayUtils.createHorizontalSpacer(), 0, 2, 2, 1);

        Button refreshTimeSheet = new Button("Get timesheet");
        FontAwesomeIconView glyph = new FontAwesomeIconView(FontAwesomeIcon.REFRESH);
        glyph.setGlyphStyle(DisplayConstants.STYLE_GLYPH_DEFAULT);
        refreshTimeSheet.setGraphic(glyph);
        refreshTimeSheet.setOnAction(event -> this.primaryStage.setScene(createRootScene(createTimesheetPane())));
        controls.add(refreshTimeSheet, 0, 3);
        return controls;
    }

    private GridPane createTimeLogGrid(List<ActivityLog> activityLogsInInterval) {
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);

        int row = 0;
        Label entrieTitle = new Label(
                "Timesheet for " + TrackMeConstants.getDateFormat().format(startDate) + " to " + TrackMeConstants
                        .getDateFormat().format(endDate));
        entrieTitle.getStyleClass().add(DisplayConstants.STYLE_LABEL_SPECIAL);
        grid.add(entrieTitle, 0, row++);
        grid.add(DisplayUtils.createHorizontalSpacer(), 0, row++, 3, 1);

        LOG.debug("Found " + activityLogsInInterval.size() + " timelog entries");
        for (ActivityLog log : activityLogsInInterval) {
            Label activityLabel = new Label();
            activityLabel.setText(getActivityName(log.getActivityId()));
            grid.add(activityLabel, 0, row);

            Label seperatorLabel = new Label(":");
            grid.add(seperatorLabel, 1, row);

            Label timeLabel = new Label(log.getTimeSpent());
            grid.add(timeLabel, 2, row++);
        }
        return grid;
    }

    private String getActivityName(UUID activityId) {
        Optional<Activity> savedActivityById = activityManager.getSavedActivityById(activityId.toString());
        return savedActivityById.isPresent()?savedActivityById.get().getName():DisplayConstants.TEXT_ACTIVITY_UNKNOWN;
    }

    public void reloadActivities() {
        if (StringUtils.isNotBlank(getProjectFilter())) {
            this.activityAcordeon.updateActivities(activityManager.getActivitiesByProject(projectFilter));
        } else if (StringUtils.isNotBlank(this.getTagFilter())) {
            this.activityAcordeon.updateActivities(activityManager.getActivitiesByTag(tagFilter));
        } else {
            this.activityAcordeon.updateActivities(activityManager.getActivitiesWithDateHeader());
        }
    }

    public ActivityManager getActivityManager() {
        return activityManager;
    }

    public TimeTrackingManager getTimeTrackingManager() {
        return timeTrackingManager;
    }

    private String getTagFilter() {
        return tagFilter;
    }

    public void setTagFilter(String tagFilter) {
        this.resetFilter();
        this.tagFilter = tagFilter;
        this.updateFilterLabel();
    }

    private String getProjectFilter() {
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

    private String getActiveFilter() {
        if (StringUtils.isNotBlank(tagFilter)) {
            return tagFilter;
        } else if (StringUtils.isNotBlank(projectFilter)) {
            return projectFilter;
        } else if (this.filterDone) {
            return DisplayConstants.LABEL_TEXT_FILTER_COMPLETED;
        } else {
            return DisplayConstants.LABEL_TEXT_FILTER_NONE;
        }
    }

    public boolean isSetFileOptions() {
        return configManager.containsProperty(DisplayConstants.NAME_PROPERTY_TODO_LOCATION) || configManager
                .containsProperty(DisplayConstants.NAME_PROPERTY_TIME_LOCATION);
    }

    public boolean isFilterDone() {
        return filterDone;
    }
}
