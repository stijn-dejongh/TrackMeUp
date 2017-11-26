package be.doji.productivity.trambuapp.views;

import be.doji.productivity.trambuapp.controls.ActivityControlAccordion;
import be.doji.productivity.trambuapp.data.ActivityAccordion;
import be.doji.productivity.trambuapp.userconfiguration.UserConfigurationManager;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambucore.TrackMeConstants;
import be.doji.productivity.trambucore.managers.ActivityManager;
import be.doji.productivity.trambucore.managers.TimeTrackingManager;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tornadofx.View;

import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;

public class ActivityOverview extends View {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityOverview.class);

    private BorderPane root;
    private ActivityManager activityManager;
    private TimeTrackingManager timeTrackingManager;
    private UserConfigurationManager configManager;

    private ActivityAccordion activityAccordion;
    private ActivityControlAccordion activityControls;

    private String tagFilter;
    private String projectFilter;
    private boolean filterDone = false;

    @NotNull @Override public Parent getRoot() {
        return this.root;
    }

    public ActivityOverview() {
        super();
        try {
            this.configManager = new UserConfigurationManager(DisplayConstants.NAME_CONFIGURATION_FILE);
            Optional<String> todoLocation = configManager.getProperty(DisplayConstants.NAME_PROPERTY_TODO_LOCATION);
            initializeActivities(todoLocation.orElse(TrackMeConstants.DEFAULT_TODO_FILE_LOCATION));
            Optional<String> timeLocation = configManager.getProperty(DisplayConstants.NAME_PROPERTY_TIME_LOCATION);
            initializeTimeTracking(timeLocation.orElse(TrackMeConstants.DEFAULT_TIMELOG_FILE_LOCATION));

            root = new BorderPane();
            root.setCenter(createContentSplitPane());

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

    private SplitPane createContentSplitPane() {
        ScrollPane activitySplitPane = new ScrollPane();
        activityAccordion = createActivityAccordeon();
        activitySplitPane.setContent(activityAccordion);
        activitySplitPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        this.activityControls = new ActivityControlAccordion(this);

        SplitPane splitPane = new SplitPane();
        splitPane.setPrefHeight(DisplayConstants.UI_DEFAULT_WINDOW_HEIGHT);
        splitPane.setPrefWidth(DisplayConstants.UI_DEFAULT_WINDOW_WIDTH);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.getItems().add(activitySplitPane);
        splitPane.getItems().add(this.activityControls);
        splitPane.setDividerPosition(0, 0.65);
        return splitPane;
    }

    private ActivityAccordion createActivityAccordeon() {
        this.activityAccordion = new ActivityAccordion(this, this.activityManager.getActivitiesWithDateHeader());
        return this.activityAccordion;
    }

    public void reloadActivities() {
        if (StringUtils.isNotBlank(getProjectFilter())) {
            this.activityAccordion.updateActivities(activityManager.getActivitiesByProject(projectFilter));
        } else if (StringUtils.isNotBlank(this.getTagFilter())) {
            this.activityAccordion.updateActivities(activityManager.getActivitiesByTag(tagFilter));
        } else {
            this.activityAccordion.updateActivities(activityManager.getActivitiesWithDateHeader());
        }
    }

    private String getTagFilter() {
        return tagFilter;
    }

    public void setTagFilter(String tagFilter) {
        this.resetFilter();
        this.tagFilter = tagFilter;
        this.activityControls.updateFilterLabel();
    }

    private String getProjectFilter() {
        return projectFilter;
    }

    public void setProjectFilter(String projectFilter) {
        this.resetFilter();
        this.projectFilter = projectFilter;
        this.activityControls.updateFilterLabel();
    }

    public void resetFilter() {
        this.tagFilter = "";
        this.projectFilter = "";
        this.filterDone = false;
        this.activityControls.updateFilterLabel();
    }

    public String getActiveFilter() {
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

    public ActivityManager getActivityManager() {
        return activityManager;
    }

    public TimeTrackingManager getTimeTrackingManager() {
        return timeTrackingManager;
    }

    public UserConfigurationManager getConfigManager() {
        return configManager;
    }

    public boolean isFilterDone() {
        return filterDone;
    }

    public void setFilterDone(boolean filterDone) {
        this.filterDone = filterDone;
    }

    @Override public void onDelete() {
        this.getTimeTrackingManager().stopAll();
    }
}
