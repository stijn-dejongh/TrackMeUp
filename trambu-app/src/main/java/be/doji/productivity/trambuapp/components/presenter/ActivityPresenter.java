package be.doji.productivity.trambuapp.components.presenter;

import be.doji.productivity.trambuapp.components.view.ActivityView;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambuapp.utils.TooltipConstants;
import be.doji.productivity.trambucore.model.tasks.Activity;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.ObservableList;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActivityPresenter extends Presenter {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityPresenter.class);

    private final ActivityManagerContainer managerContainer;
    private Activity model;
    private ActivityView view;
    private Presenter parent;
    private boolean modelParentChanged;

    public ActivityPresenter(ActivityView view, Activity model) {
        this.view = view;
        this.model = model;
        this.managerContainer = find(ActivityManagerContainer.class);
    }

    public ActivityPresenter(ActivityView view, Activity model, Presenter parent) {
        this(view, model);
        this.parent = parent;
    }

    public void populate() {
        this.refresh();
    }

    public void refresh() {
        refreshHeader();
        refreshViewStyle();
        refreshFields();
    }

    public void refreshFields() {
        refreshSubActivities();
    }

    public void refreshHeader() {
        view.setText(getHeaderTitle());
        view.getTitleLabel().setGraphic(getHeaderIcon());
        view.getTitleLabel().setTooltip(getDoneTooltipText());
    }

    public void refreshViewStyle() {
        view.getStyleClass()
                .removeAll(DisplayConstants.STYLE_CLASS_ACTIVITY_DONE, DisplayConstants.STYLE_CLASS_ACTIVITY_TODO,
                        DisplayConstants.STYLE_CLASS_ACTIVITY_ALERT);
        view.getStyleClass().add(getActivityStyle());
    }

    private FontAwesomeIconView getHeaderIcon() {
        FontAwesomeIconView checkedCalendar = DisplayUtils.createStyledIcon(FontAwesomeIcon.CHECK_CIRCLE);
        FontAwesomeIconView uncheckedCalendar = DisplayUtils.createStyledIcon(FontAwesomeIcon.CIRCLE_ALT);
        FontAwesomeIconView editing = DisplayUtils.createStyledIcon(FontAwesomeIcon.EDIT);
        if (view.isEditable()) {
            return editing;
        } else {
            return model.isCompleted()?checkedCalendar:uncheckedCalendar;
        }
    }

    private Tooltip getDoneTooltipText() {
        return DisplayUtils.createTooltip(model.isCompleted()?
                TooltipConstants.TOOLTIP_TEXT_ACTIVITY_NOT_DONE:
                TooltipConstants.TOOLTIP_TEXT_ACTIVITY_DONE);
    }

    public String getHeaderTitle() {
        return model.getName();
    }

    public void headerButtonClicked() {
        try {
            view.toggleCompleted();
            refreshHeader();
            save();
        } catch (IOException | ParseException e) {
            LOG.error("Error while saving activity to file: " + e.getMessage());
        }
    }

    public String getActivityStyle() {
        if (this.model.isCompleted() && this.model.isAllSubActivitiesCompleted()) {
            return DisplayConstants.STYLE_CLASS_ACTIVITY_DONE;
        } else {
            return this.model.isAlertActive()?
                    DisplayConstants.STYLE_CLASS_ACTIVITY_ALERT:
                    DisplayConstants.STYLE_CLASS_ACTIVITY_TODO;
        }
    }

    public void refreshSubActivities() {
        ObservableList<TitledPane> panes = view.getSubActivitiesAccordion().getPanes();
        panes.clear();
        for (Activity subActivity : this.model.getSubActivities()) {
            panes.add(new ActivityView(subActivity));
        }
    }

    private void save() throws IOException, ParseException {
        updateActivityFields();
        this.managerContainer.getActivityManager().save(getActivityToSave());
        if (!this.modelParentChanged) {
            refresh();
        } else if (this.parent != null) {
            this.parent.refresh();
        }
    }

    private Activity getActivityToSave() {
        if (!this.modelParentChanged) {
            return this.model;
        } else {
            return getRootActivity();
        }

    }

    @NotNull private Activity getRootActivity() {
        Activity activityToSave = this.model;
        while (StringUtils.isNotBlank(activityToSave.getParentActivity())) {
            Optional<Activity> savedParent = this.managerContainer.getActivityManager()
                    .getSavedActivityById(activityToSave.getParentActivity());
            if (savedParent.isPresent()) {
                activityToSave = savedParent.get();
            }
        }
        return activityToSave;
    }

    private void updateActivityFields() {
        if (view.getNameField() != null) {
            model.setName(view.getNameField().getText());
        }
        if (view.getLocationField() != null) {
            model.setLocation(view.getLocationField().getText());
        }
        updateActivityProjects();
        updateActivityTags();
        updateActivityWarningPeriod();
    }

    private void updateActivityWarningPeriod() {
        if (warningFieldFilledInCorrectly()) {
            String warningTimeframe = view.getWarningPeriodInHours().getText();
            Duration timeFrame = Duration.ofHours(Long.parseLong(warningTimeframe));
            model.setWarningTimeFrame(timeFrame);
        }
    }

    private boolean warningFieldFilledInCorrectly() {
        return view.getWarningPeriodInHours() != null && StringUtils
                .isNotBlank(view.getWarningPeriodInHours().getText()) && view.getWarningPeriodInHours().getText()
                .matches(DisplayConstants.REGEX_WARNING_PERIOD);
    }

    private void updateActivityProjects() {
        if (view.getProjectsField() != null && StringUtils.isNotBlank(view.getProjectsField().getText())) {
            String conctatenatedProjects = view.getProjectsField().getText();
            List<String> newProjects = splitTextFieldValueOnSeperator(conctatenatedProjects,
                    DisplayConstants.FIELD_SEPERATOR);
            model.setProjects(newProjects);
        }
    }

    private List<String> splitTextFieldValueOnSeperator(String conctatenatedProjects, String seperator) {
        List<String> newTags = new ArrayList<>();
        String[] split = conctatenatedProjects.split(seperator);
        for (String aSplit : split) {
            newTags.add(aSplit.trim());
        }
        return newTags;
    }

    private void updateActivityTags() {
        if (view.getTagsField() != null && StringUtils.isNotBlank(view.getTagsField().getText())) {
            String conctatenatedProjects = view.getTagsField().getText();
            List<String> newTags = splitTextFieldValueOnSeperator(conctatenatedProjects,
                    DisplayConstants.FIELD_SEPERATOR);
            model.setTags(newTags);
        }
    }

}
