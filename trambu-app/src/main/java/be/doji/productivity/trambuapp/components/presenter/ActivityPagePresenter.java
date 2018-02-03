package be.doji.productivity.trambuapp.components.presenter;

import be.doji.productivity.trambuapp.components.view.ActivityPageView;
import be.doji.productivity.trambuapp.components.view.ActivityView;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambucore.managers.NoteManager;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.model.tracker.ActivityLog;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ActivityPagePresenter extends Presenter {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityPagePresenter.class);

    private ActivityManagerContainer model;
    private ActivityPageView view;

    private String tagFilter;
    private String projectFilter;
    private boolean filterDone = false;

    public ActivityPagePresenter(ActivityPageView view) {
        this.view = view;
        this.model = find(ActivityManagerContainer.class);
    }

    public void populate() {
        refresh();
    }

    public void refresh() {
        populateActivities();
        applyFilters();
        view.refreshAccordion();
    }

    public void populateActivities() {
        view.getPanes().clear();
        Map<Date, List<Activity>> groupedActivities = model.getActivityManager().getActivitiesWithDateHeader();
        for (Map.Entry<Date, List<Activity>> activityGroupEntry : groupedActivities.entrySet()) {
            List<Activity> activityGroup = activityGroupEntry.getValue();
            if (!activityGroup.isEmpty()) {
                view.addPane(DisplayUtils
                        .createSeperatorPane(DisplayUtils.getDateSeperatorText(activityGroupEntry.getKey())));
                for (Activity activity : activityGroup) {
                    view.addPane(new ActivityView(activity, this));
                }
            }
        }
    }

    public void applyFilters() {
        List<ActivityView> acitivtyPanes = view.getActivityPanes();
        for (ActivityView pane : acitivtyPanes) {
            if (shouldBeFiltered(pane)) {
                pane.setVisible(false);
            }

        }
    }

    public boolean shouldBeFiltered(ActivityView pane) {
        boolean shouldFilterBasedOnProject = pane.getPresenter().shouldBeFilteredOnProject(getProjectFilter());
        boolean shouldFilterBasedOnTag = pane.getPresenter().shouldBeFilteredOnTag(getTagFilter());
        boolean shouldFilterBasedOnCompletion = filterDone && pane.getPresenter().isActivityCompleted();
        return shouldFilterBasedOnProject || shouldFilterBasedOnTag || shouldFilterBasedOnCompletion;
    }

    private String getTagFilter() {
        return tagFilter;
    }

    public void setTagFilter(String tagFilter) {
        this.resetFilter();
        this.tagFilter = tagFilter;
        view.getControlAccordion().updateFilterLabel();
    }

    private String getProjectFilter() {
        return projectFilter;
    }

    public void setProjectFilter(String projectFilter) {
        this.resetFilter();
        this.projectFilter = projectFilter;
        view.getControlAccordion().updateFilterLabel();
    }

    public void resetFilter() {
        this.tagFilter = "";
        this.projectFilter = "";
        this.filterDone = false;
        view.getControlAccordion().updateFilterLabel();
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

    public void setFilterDone(boolean filterDone) {
        this.filterDone = filterDone;
    }

    public ActivityPageView getView() {
        return view;
    }

    public void setView(ActivityPageView view) {
        this.view = view;
    }

    public void onViewClose() {
        this.model.getTimeTrackingManager().stopAll();

    }

    public void onViewLoad() {
        this.refresh();
    }

    public ActivityLog getLogForActivityId(UUID id) {
        return this.model.getTimeTrackingManager().getLogForActivityId(id);
    }

    public void saveActivity(Activity newActivity) {
        try {
            this.model.getActivityManager().save(newActivity);

        } catch (IOException | ParseException exception) {
            LOG.error("Error creation new activity", exception);
        }

    }

    public List<String> getExistingLocations() {
        return this.model.getActivityManager().getExistingLocations();
    }

    public List<String> getExistingTags() {
        return this.model.getActivityManager().getExistingTags();
    }

    public List<String> getExistingProjects() {
        return this.model.getActivityManager().getExistingProjects();
    }

    public List<String> getAllActivityNames() {
        return this.model.getActivityManager().getAllActivityNames();
    }

    public NoteManager getNoteManager() {
        return this.model.getNoteManager();
    }

    public ActivityManagerContainer getActivityController() {
        return this.model;
    }
}
