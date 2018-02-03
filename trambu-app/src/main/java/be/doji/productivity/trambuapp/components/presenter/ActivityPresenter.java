package be.doji.productivity.trambuapp.components.presenter;

import be.doji.productivity.trambuapp.components.view.ActivityView;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambuapp.utils.TooltipConstants;
import be.doji.productivity.trambucore.managers.NoteManager;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.model.tasks.Note;
import be.doji.productivity.trambucore.model.tracker.ActivityLog;
import be.doji.productivity.trambucore.model.tracker.TimeLog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class ActivityPresenter extends Presenter {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityPresenter.class);

    private ActivityManagerContainer managerContainer;
    private ActivityLog activityLog;
    private Activity model;
    private ActivityView view;
    private ActivityPagePresenter parent;
    private boolean modelParentChanged;

    public ActivityPresenter(ActivityView view, Activity model) {
        this.view = view;
        this.model = model;
        this.managerContainer = find(ActivityManagerContainer.class);
        this.activityLog = getActivityLog();
    }

    public ActivityPresenter(ActivityView view, Activity model, ActivityPagePresenter parent) {
        this(view, model);
        this.parent = parent;
    }

    public void populate() {
        this.refresh();
    }

    public void refresh() {
        this.activityLog = getActivityLog();
        refreshHeader();
        refreshViewStyle();
        refreshFields();
    }

    public ActivityLog getActivityLog() {
        return managerContainer.getTimeTrackingManager().getLogForActivityId(this.model.getId());
    }

    public void refreshFields() {
        refreshSubActivities();
        refreshEditableTagsField();
        refreshEditableProjectsField();
        refreshEditableLocation();
    }

    public void refreshHeader() {
        view.setText(getActivityName());
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

    public String getActivityName() {
        return model.getName();
    }

    public void headerButtonClicked() {
        this.doneClicked();
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
        updateModel();
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

    private void updateModel() {
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

    private void refreshEditableTagsField() {
        Optional<String> reducedTags = this.model.getTags().stream()
                .reduce((s, s2) -> s + DisplayConstants.FIELD_SEPERATOR + " " + s2);
        reducedTags.ifPresent(s -> view.getTagsField().setText(s));

        SortedSet<String> treeSetTags = new TreeSet<>();
        treeSetTags.addAll(this.managerContainer.getActivityManager().getExistingTags());
        view.getTagsField().setSuggestions(treeSetTags);
    }

    private void refreshEditableProjectsField() {
        Optional<String> reducedProjects = this.model.getProjects().stream()
                .reduce((s, s2) -> s + DisplayConstants.FIELD_SEPERATOR + " " + s2);

        reducedProjects.ifPresent(s -> view.getProjectsField().setText(s));
        SortedSet<String> treeSetProjects = new TreeSet<>();
        treeSetProjects.addAll(this.managerContainer.getActivityManager().getExistingProjects());
        view.getProjectsField().setSuggestions(treeSetProjects);
    }

    private void refreshEditableLocation() {
        SortedSet<String> existingLocations = new TreeSet<>();
        existingLocations.addAll(this.managerContainer.getActivityManager().getExistingLocations());
        view.getLocationField().setSuggestions(existingLocations);

        if (this.model.isSetLocation()) {
            view.getLocationField().setText(this.model.getLocation());
        } else {
            view.getLocationField().setText("UNKNOWN");
        }
    }

    private void refreshDeadlineLabel() {
        view.getDeadlineLabel()
                .setText(DateFormat.getDateInstance(DateFormat.DEFAULT).format(this.model.getDeadline()));
        if (this.model.isAlertActive()) {
            view.getDeadlineLabel().getStyleClass().add("warningLabel");
        }
    }

    private void refreshTags() {
        view.getTagsBox().getChildren().addAll(this.model.getTags().stream().map(tag -> {
            Button button = new Button(tag);
            button.setOnAction(e -> {
                if (parent != null) {
                    parent.setTagFilter(tag);
                }
                refresh();
            });
            return button;
        }).collect(Collectors.toList()));
    }

    private void refreshProjects() {
        view.getProjectsBox().getChildren().addAll(this.model.getProjects().stream().map(project -> {
            Button button = new Button(project);
            button.setOnAction(e -> {
                if (parent != null) {
                    parent.setProjectFilter(project);
                }
                refresh();
            });
            return button;
        }).collect(Collectors.toList()));
    }

    public boolean hasSubActivities() {
        return !this.model.getSubActivities().isEmpty();
    }

    public String getActivityPriority() {
        return this.model.getPriority();
    }

    public void setActivityPriority(String priority) {
        if (StringUtils.isNotBlank(priority)) {
            this.model.setPriority(priority);
        }
    }

    public void deadlinePicked() {
        view.getDeadlineDatePicker().getValue();
        this.model.setDeadline(
                Date.from(view.getDeadlineDatePicker().getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public void openNotes() {
        try {
            NoteManager noteManager = this.managerContainer.getNoteManager();
            Optional<Note> noteForActivity = noteManager.findNoteForActivity(this.model.getId());
            Note note;
            if (noteForActivity.isPresent()) {
                note = noteForActivity.get();
            } else {
                note = noteManager.createNoteForActivity(this.model.getId());
            }

            TextArea textField = new TextArea();
            textField.setPrefWidth(view.getOverlay().getWidth());
            textField.setPrefHeight(view.getOverlay().getHeight());
            textField.setText(note.getContent().stream().collect(Collectors.joining(System.lineSeparator())));
            textField.setWrapText(true);
            textField.setEditable(true);
            view.getOverlay().setContent(textField);
            view.getOverlay().setControlButtons(createNoteControlButtons(note, textField));
            view.getOverlay().refreshContent();
            view.getOverlay().setVisible(true);
        } catch (IOException e) {
            view.getOverlay().setContent(new Label("Error reading notes: " + e.getMessage()));
        }
    }

    private List<Button> createNoteControlButtons(Note noteToSave, TextArea textField) {
        List<Button> controls = new ArrayList<>();
        Button saveButton = new Button("Save changes");
        saveButton.setGraphic(DisplayUtils.createStyledIcon(FontAwesomeIcon.SAVE));
        saveButton.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_ACTIVITY_SAVE_NOTE));
        saveButton.setOnAction(event -> {
            try {
                noteToSave.setContent(Arrays.asList(textField.getText().split(System.lineSeparator())));
                noteToSave.save();
            } catch (IOException e) {
                LOG.error("Error saving note to file: " + e.getMessage());
            }
        });
        controls.add(saveButton);
        return controls;
    }

    public List<String> getPossibleParents() {
        List<String> possibleParents = this.managerContainer.getActivityManager().getAllActivityNames();
        possibleParents.remove(this.model.getName());
        return possibleParents;
    }

    public void changeParent(String newParent) {
        Optional<Activity> savedParent = this.managerContainer.getActivityManager().getSavedActivityByName(newParent);
        if (savedParent.isPresent()) {
            this.managerContainer.getActivityManager().addActivityAsSub(this.model, savedParent.get());
        }
        this.modelParentChanged = true;
    }

    public void refreshDoneButton() {
        view.getDoneButton().setText(DisplayUtils.getDoneButtonText(this.model));
        view.getDoneButton().setTooltip(getDoneTooltipText());
    }

    public void doneClicked() {
        try {
            if (!this.model.isCompleted() && !this.model.isAllSubActivitiesCompleted()) {
                LOG.warn("Completing activity with incomplete subactivities");
            }

            this.model.setCompleted(!model.isCompleted());
            view.getDoneButton().setText(DisplayUtils.getDoneButtonText(this.model));
            view.getDoneButton().setTooltip(getDoneTooltipText());
            save();
            refreshHeader();
        } catch (IOException | ParseException e) {
            LOG.error(DisplayConstants.ERROR_MESSAGE_ACTIVITY_SAVING + ": " + e.getMessage());
        }
    }

    public void editButtonClicked() {
        try {
            if (view.isEditable()) {
                view.makeUneditable();
                save();
            } else {
                view.makeEditable();
            }
            refresh();
            view.getEditButton().setText(getEditButonText());
        } catch (IOException | ParseException e) {
            LOG.error(DisplayConstants.ERROR_MESSAGE_ACTIVITY_SAVING + ": " + e.getMessage());
        }
    }

    public String getEditButonText() {
        return view.isEditable()?DisplayConstants.BUTTON_TEXT_SAVE:DisplayConstants.BUTTON_TEXT_EDIT;
    }

    public void deleteButtonClicked() {
        try {
            this.managerContainer.getActivityManager().delete(this.model);
            view.setVisible(false);
            if (this.parent != null) {
                parent.refresh();
            }
        } catch (IOException | ParseException e) {
            LOG.error(DisplayConstants.ERROR_MESSAGE_ACTIVITY_SAVING + ": " + e.getMessage());
        }
    }

    public void timingButtonPressed() {
        if (getActiveLog().isPresent()) {
            activityLog.stopActiveLog();
        } else {
            activityLog.startLog();
        }
        view.getTimingButton().setText(getTimingButtonText());
        view.getTimingButton().setGraphic(getTimingButtonIcon());
        this.managerContainer.getTimeTrackingManager().save(activityLog);
    }

    public FontAwesomeIconView getTimingButtonIcon() {
        return DisplayUtils.createStyledIcon(
                activityLog.getActiveLog().isPresent()?FontAwesomeIcon.HOURGLASS_END:FontAwesomeIcon.HOURGLASS_START);
    }

    public String getTimingButtonText() {
        Optional<TimeLog> activeLog = getActiveLog();
        if (activeLog.isPresent()) {
            return DisplayConstants.BUTTON_TEXT_TIMER_STOP;
        } else {
            return DisplayConstants.BUTTON_TEXT_TIMER_START;
        }
    }

    public Tooltip getTimingButtonTooltipText() {
        return DisplayUtils.createTooltip(getActiveLog().isPresent()?
                TooltipConstants.TOOLTIP_TEXT_ACTIVITY_TIMING_CONTROL_STOP:
                TooltipConstants.TOOLTIP_TEXT_ACTIVITY_TIMING_CONTROL_START);
    }

    private Optional<TimeLog> getActiveLog() {
        return getActivityLog().getActiveLog();
    }

    void setManagerContainer(ActivityManagerContainer container) {
        this.managerContainer = container;
    }

    public boolean shouldBeFilteredOnProject(String projectFilter) {
        return StringUtils.isNotBlank(projectFilter) && !this.model.getProjects().parallelStream()
                .anyMatch(project -> StringUtils.equalsIgnoreCase(project, projectFilter));
    }

    public boolean shouldBeFilteredOnTag(String tagFilter) {
        return StringUtils.isNotBlank(tagFilter) && !this.model.getTags().parallelStream()
                .anyMatch(tag -> StringUtils.equalsIgnoreCase(tag, tagFilter));
    }

    public boolean isActivityCompleted() {
        return this.model.isCompleted();
    }
}
