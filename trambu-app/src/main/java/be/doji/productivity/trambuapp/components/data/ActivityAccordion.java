package be.doji.productivity.trambuapp.components.data;

import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambuapp.views.ActivityOverview;
import be.doji.productivity.trambucore.model.tasks.Activity;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ActivityAccordion extends Accordion {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityAccordion.class);

    private final ActivityOverview view;

    public ActivityAccordion(ActivityOverview view, Map<Date, List<Activity>> activitiesWithDateHeader) {
        this.view = view;
        this.getPanes().addAll(createActivityNodes(activitiesWithDateHeader));
    }

    public ActivityAccordion(ActivityOverview view, List<Activity> activities) {
        this.view = view;
        this.getPanes().addAll(createActivityNodes(activities));
    }

    private List<TitledPane> createActivityNodes(Map<Date, List<Activity>> activitiesWithHeader) {
        List<TitledPane> panes = new ArrayList<>();
        for (Map.Entry<Date, List<Activity>> activityWithHeader : activitiesWithHeader.entrySet()) {
            List<ActivityNode> activities = createActivityNodes(activityWithHeader.getValue());
            if (!activities.isEmpty()) {
                panes.add(createSeperatorPane(activityWithHeader.getKey()));
                panes.addAll(activities);
            }
        }
        return panes;
    }

    private List<ActivityNode> createActivityNodes(List<Activity> activityNodes) {
        return activityNodes.stream().filter(activity -> !(activity.isCompleted() && view.isFilterDone()))
                .map(activity -> new ActivityNode(activity, view)).collect(Collectors.toList());
    }

    private TitledPane createSeperatorPane(Date key) {
        TitledPane headerPane = new TitledPane();
        String formattedDate = DisplayUtils.getDateSeperatorText(key);

        headerPane.setText(formattedDate);
        headerPane.setCollapsible(false);
        headerPane.setStyle("-fx-start-margin: 15px;");
        return headerPane;
    }

    public void refresh() {
        for (TitledPane pane : this.getPanes()) {
            if (pane.getClass().equals(ActivityNode.class)) {
                ActivityNode castedPane = (ActivityNode) pane;
                castedPane.refresh();
            }
        }
        getActivePane().ifPresent(this::setExpandedPane);
    }

    private Optional<ActivityNode> getActivePane() {
        LOG.debug("Looking for active pane");
        for (TitledPane pane : this.getPanes()) {
            if (pane.getClass().equals(ActivityNode.class)) {
                ActivityNode castedPane = (ActivityNode) pane;
                if (castedPane.isActive()) {
                    LOG.debug("Found active Pane");
                    return Optional.of(castedPane);
                }
            }
        }
        return Optional.empty();
    }

    public void updateActivities(Map<Date, List<Activity>> activitiesWithDateHeader) {
        this.getPanes().clear();
        this.getPanes().addAll(createActivityNodes(activitiesWithDateHeader));

    }
}
