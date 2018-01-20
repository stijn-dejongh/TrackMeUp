package be.doji.productivity.trambuapp.views;

import be.doji.productivity.trambuapp.controllers.ActivityController;
import be.doji.productivity.trambuapp.controls.ActivityControlAccordion;
import be.doji.productivity.trambuapp.controls.MainMenuBar;
import be.doji.productivity.trambuapp.components.data.ActivityAccordion;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import tornadofx.View;

public class ActivityOverview extends View {

    private final ActivityController activityController;

    private BorderPane root;

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
        this.setTitle(DisplayConstants.TITLE_APPLICATION + " - " + DisplayConstants.TITLE_ACTIVITY);
        this.activityController = find(ActivityController.class);

        root = new BorderPane();
        root.setPrefHeight(DisplayConstants.UI_DEFAULT_WINDOW_HEIGHT);
        root.setPrefWidth(DisplayConstants.UI_DEFAULT_WINDOW_WIDTH);
        root.setCenter(createContentSplitPane());
        root.setBottom(new MainMenuBar(this).getRoot());
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
        this.activityAccordion = new ActivityAccordion(this,
                this.activityController.getActivityManager().getActivitiesWithDateHeader());
        return this.activityAccordion;
    }

    public void reloadActivities() {
        if (StringUtils.isNotBlank(getProjectFilter())) {
            this.activityAccordion.updateActivities(
                    this.activityController.getActivityManager().getActivitiesByProject(projectFilter));
        } else if (StringUtils.isNotBlank(this.getTagFilter())) {
            this.activityAccordion
                    .updateActivities(this.activityController.getActivityManager().getActivitiesByTag(tagFilter));
        } else {
            this.activityAccordion
                    .updateActivities(this.activityController.getActivityManager().getActivitiesWithDateHeader());
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

    public boolean isFilterDone() {
        return filterDone;
    }

    public void setFilterDone(boolean filterDone) {
        this.filterDone = filterDone;
    }

    @Override public void onDelete() {
        this.activityController.getTimeTrackingManager().stopAll();
    }

    @Override public void onDock() {
        this.reloadActivities();
    }

    public ActivityController getActivityController() {
        return activityController;
    }

}
