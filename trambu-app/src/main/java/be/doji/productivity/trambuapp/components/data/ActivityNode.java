package be.doji.productivity.trambuapp.components.data;

import be.doji.productivity.trambuapp.components.helper.AutocompleteTextField;
import be.doji.productivity.trambuapp.components.helper.OverlayPane;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambuapp.utils.TooltipConstants;
import be.doji.productivity.trambuapp.views.ActivityOverview;
import be.doji.productivity.trambucore.TrackMeConstants;
import be.doji.productivity.trambucore.managers.NoteManager;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.model.tasks.Note;
import be.doji.productivity.trambucore.model.tracker.ActivityLog;
import be.doji.productivity.trambucore.model.tracker.TimeLog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class ActivityNode extends TitledPane {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityNode.class);

    private static final String FIELD_SEPERATOR = ",";
    private boolean isActive;
    private ActivityOverview application;
    private boolean isEditable = false;
    private Activity activity;
    private TextField nameField;
    private AutocompleteTextField projectsField;
    private AutocompleteTextField tagsField;
    private ActivityLog activityLog;
    private boolean parentChanged;
    private TextField warningPeriodInHours;
    private AutocompleteTextField locationField;
    private OverlayPane overlay;

    public ActivityNode(Activity activity, ActivityOverview trambuApplication) {
        super();
        this.activity = activity;
        this.application = trambuApplication;
        this.activityLog = application.getActivityController().getTimeTrackingManager()
                .getLogForActivityId(activity.getId());
        updateHeader();
        overlay = new OverlayPane();
        this.setContent(createContentContainer());

        this.setVisible(true);
        this.setOnMouseClicked(event -> this.setActive(!this.isActive));
    }

    @NotNull public StackPane createContentContainer() {
        StackPane contentContainer = new StackPane();
        GridPane activityContent = createActivityContent();
        contentContainer.getChildren().add(activityContent);
        contentContainer.getChildren().add(overlay);
        return contentContainer;
    }

    private void updateHeader() {
        this.setText(activity.getName());
        Button titleLabel = new Button();

        titleLabel.setGraphic(getHeaderIcon());
        titleLabel.getStyleClass().clear();
        titleLabel.getStyleClass().add("icon-button");
        titleLabel.setOnAction(event -> {
            try {
                this.toggleCompleted();
                titleLabel.setGraphic(getHeaderIcon());
                save();
            } catch (IOException | ParseException e) {
                LOG.error("Error while saving activity to file: " + e.getMessage());
            }
        });
        titleLabel.setTooltip(getDoneTooltipText(activity));
        this.setGraphic(titleLabel);
        this.getStyleClass()
                .removeAll(DisplayConstants.STYLE_CLASS_ACTIVITY_DONE, DisplayConstants.STYLE_CLASS_ACTIVITY_TODO,
                        DisplayConstants.STYLE_CLASS_ACTIVITY_ALERT);
        this.getStyleClass().add(getActivityStyle());
    }

    private FontAwesomeIconView getHeaderIcon() {
        FontAwesomeIconView checkedCalendar = DisplayUtils.createStyledIcon(FontAwesomeIcon.CHECK_CIRCLE);
        FontAwesomeIconView uncheckedCalendar = DisplayUtils.createStyledIcon(FontAwesomeIcon.CIRCLE_ALT);
        FontAwesomeIconView editing = DisplayUtils.createStyledIcon(FontAwesomeIcon.EDIT);
        if (isEditable) {
            return editing;
        } else {
            return activity.isCompleted()?checkedCalendar:uncheckedCalendar;
        }
    }

    private Tooltip getDoneTooltipText(Activity activity) {
        return DisplayUtils.createTooltip(activity.isCompleted()?
                TooltipConstants.TOOLTIP_TEXT_ACTIVITY_NOT_DONE:
                TooltipConstants.TOOLTIP_TEXT_ACTIVITY_DONE);
    }

    String getActivityStyle() {
        if (this.activity.isCompleted() && this.activity.isAllSubActivitiesCompleted()) {
            return DisplayConstants.STYLE_CLASS_ACTIVITY_DONE;
        } else {
            return this.activity.isAlertActive()?
                    DisplayConstants.STYLE_CLASS_ACTIVITY_ALERT:
                    DisplayConstants.STYLE_CLASS_ACTIVITY_TODO;
        }
    }

    GridPane createActivityContent() {
        GridPane content = new GridPane();
        content.setVgap(4);
        content.setPadding(new Insets(5, 5, 5, 5));
        int rowIndex = 0;

        content.add(createActvityControls(), 0, rowIndex++, 2, 1);

        content.add(DisplayUtils.createHorizontalSpacer(), 0, rowIndex++, 2, 1);

        content.add(createTimingControls(), 0, rowIndex++, 2, 1);

        if (isEditable) {
            content.add(new Label("Change activity name:"), 0, rowIndex);
            content.add(createNameEdit(), 1, rowIndex++);
        }

        content.add(new Label("Priority: "), 0, rowIndex);
        content.add(createPriority(), 1, rowIndex++);

        if (activity.isSetDeadline() || isEditable) {
            content.add(new Label("Deadline: "), 0, rowIndex);
            content.add(createDeadline(), 1, rowIndex++);
        }

        if (activity.isSetLocation() || isEditable) {
            content.add(new Label("Location :"), 0, rowIndex);
            content.add(createLocation(), 1, rowIndex++);
        }

        Label warningPeriodLabel = new Label("Warning period: ");
        content.add(warningPeriodLabel, 0, rowIndex);
        if (isEditable) {

            warningPeriodInHours = new TextField();
            Label warningPeriodUnit = new Label("hours");
            content.add(warningPeriodInHours, 1, rowIndex);
            content.add(warningPeriodUnit, 2, rowIndex++);
        } else {
            Label warningPeriod = new Label(activity.getWarningTimeFrame().toString());
            content.add(warningPeriod, 1, rowIndex++);

        }

        content.add(new Label("Tags: "), 0, rowIndex);
        content.add(createTags(), 1, rowIndex++);

        content.add(new Label("Projects: "), 0, rowIndex);
        content.add(createProjects(), 1, rowIndex++);

        content.add(createLogPoints(), 0, rowIndex++, 2, 1);

        content.add(new Label("Notes: "), 0, rowIndex);
        content.add(createNotes(), 1, rowIndex++);

        if (isEditable) {
            Label parentTitle = new Label("Select parent: ");
            parentTitle.getStyleClass().clear();
            parentTitle.getStyleClass().add("separator-label");
            content.add(parentTitle, 0, rowIndex++);
            content.add(createParentSelector(), 0, rowIndex++, 2, 1);
        }

        if (!activity.getSubActivities().isEmpty()) {
            Label subActivityTitle = new Label("Subactivities: ");
            subActivityTitle.getStyleClass().clear();
            subActivityTitle.getStyleClass().add("separator-label");
            content.add(subActivityTitle, 0, rowIndex++);
            content.add(createSubActivities(), 0, rowIndex, 2, 1);
        }

        content.setVisible(true);
        return content;
    }

    private Node createNameEdit() {
        nameField = new TextField();
        nameField.setText(activity.getName());
        return nameField;
    }

    GridPane createActvityControls() {
        GridPane content = new GridPane();
        content.setVgap(4);
        content.setHgap(4);
        content.setPadding(new Insets(5, 5, 5, 5));
        content.add(createDoneButton(), 0, 0);
        content.add(createEditButton(), 1, 0);
        content.add(createDeleteButton(), 2, 0);
        return content;
    }

    Node createPriority() {
        if (isEditable) {
            return createEditablePriority();
        } else {
            return createUneditablePriority();
        }
    }

    Node createEditablePriority() {
        ObservableList<String> options = FXCollections.observableArrayList(TrackMeConstants.getPriorityList());
        final ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.setValue(activity.getPriority());
        comboBox.valueProperty().addListener((ov, t, t1) -> activity.setPriority(t1));
        return comboBox;
    }

    private Node createUneditablePriority() {
        return new Label(activity.getPriority());
    }

    Node createDeadline() {
        if (isEditable) {
            return createEditableDeadline();
        } else {
            return createUneditableDeadline();
        }
    }

    Node createLocation() {
        if (isEditable) {
            return createEditableLocation();
        } else {
            return createUneditableLocation();
        }
    }

    private Node createEditableLocation() {
        locationField = new AutocompleteTextField();
        SortedSet<String> existingLocations = new TreeSet<>();
        existingLocations.addAll(application.getActivityController().getActivityManager().getExistingLocations());
        locationField.setSuggestions(existingLocations);

        if (activity.isSetLocation()) {
            locationField.setText(activity.getLocation());
        }
        return locationField;
    }

    private Node createUneditableLocation() {
        return new Label(activity.getLocation());
    }

    private LocalDate datePickerDate;

    private Node createEditableDeadline() {
        HBox deadlinePicker = new HBox();
        deadlinePicker.getChildren().add(createDatePicker());
        return deadlinePicker;
    }

    private DatePicker createDatePicker() {
        DatePicker datePicker = new DatePicker();
        datePicker.setOnAction(event -> {
            datePickerDate = datePicker.getValue();
            activity.setDeadline(Date.from(datePickerDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        });
        return datePicker;
    }

    private Node createUneditableDeadline() {
        Label deadlineLabel = new Label(DateFormat.getDateInstance(DateFormat.DEFAULT).format(activity.getDeadline()));
        if (activity.isAlertActive()) {
            deadlineLabel.getStyleClass().add("warningLabel");
        }
        return deadlineLabel;
    }

    Node createTags() {
        if (isEditable) {
            return createEditableTags();
        } else {
            return createUneditableTags();
        }
    }

    private Node createEditableTags() {
        Optional<String> reducedTags = activity.getTags().stream().reduce((s, s2) -> s + FIELD_SEPERATOR + " " + s2);
        tagsField = new AutocompleteTextField();
        reducedTags.ifPresent(s -> tagsField.setText(s));

        SortedSet<String> treeSetTags = new TreeSet<>();
        treeSetTags.addAll(application.getActivityController().getActivityManager().getExistingTags());
        tagsField.setSuggestions(treeSetTags);
        return tagsField;
    }

    private HBox createUneditableTags() {
        HBox tags = new HBox(5);
        tags.getChildren().addAll(activity.getTags().stream().map(tag -> {
            Button button = new Button(tag);
            button.setOnAction(e -> {
                application.setTagFilter(tag);
                application.reloadActivities();
            });
            return button;
        }).collect(Collectors.toList()));
        return tags;
    }

    Node createProjects() {
        if (isEditable) {
            return createEditableProjects();
        } else {
            return createUneditableProjects();
        }
    }

    private Node createEditableProjects() {
        Optional<String> reducedProjects = activity.getProjects().stream()
                .reduce((s, s2) -> s + FIELD_SEPERATOR + " " + s2);
        projectsField = new AutocompleteTextField();
        reducedProjects.ifPresent(s -> projectsField.setText(s));
        SortedSet<String> treeSetProjects = new TreeSet<>();
        treeSetProjects.addAll(application.getActivityController().getActivityManager().getExistingProjects());
        projectsField.setSuggestions(treeSetProjects);
        return projectsField;
    }

    private HBox createUneditableProjects() {
        HBox projecs = new HBox();
        projecs.getChildren().addAll(activity.getProjects().stream().map(project -> {
            Button button = new Button(project);
            button.setOnAction(e -> {
                application.setProjectFilter(project);
                application.reloadActivities();
            });
            return button;
        }).collect(Collectors.toList()));
        return projecs;
    }

    private GridPane createLogPoints() {
        List<TimeLog> logpoints = activityLog.getLogpoints();

        GridPane logpointGrid = new GridPane();
        logpointGrid.setVgap(4);
        logpointGrid.setPadding(new Insets(5, 5, 5, 5));
        int logRowIndex = 0;
        Label logpointInfo = new Label("There are " + logpoints.size() + " timelogs available.  ");
        logpointGrid.add(logpointInfo, 0, logRowIndex);
        if (!logpoints.isEmpty()) {
            logpointGrid.add(createOpenLogsButton(logpoints), 1, logRowIndex++);
        } else {
            logRowIndex++;
        }

        logpointGrid.add(new Label("Time spent on activity: "), 0, logRowIndex);
        logpointGrid.add(new Label(activityLog.getTimeSpentInHoursString()), 1, logRowIndex);
        return logpointGrid;
    }

    @NotNull private Button createOpenLogsButton(List<TimeLog> logpoints) {
        Button showLogs = new Button("Show timelogs");
        showLogs.setGraphic(DisplayUtils.createStyledIcon(FontAwesomeIcon.INFO_CIRCLE));
        showLogs.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_ACTIVITY_LOGPOINT_EXPAND));
        showLogs.setOnAction(event -> {
            this.overlay.setContent(createLogPointGrid(logpoints));
            this.overlay.refreshContent();
            this.overlay.setVisible(true);
        });
        return showLogs;
    }

    private GridPane createLogPointGrid(List<TimeLog> logpoints) {
        GridPane logpointGrid = new GridPane();
        logpointGrid.setVgap(4);
        logpointGrid.setPadding(new Insets(5, 5, 5, 5));
        int logRowIndex = 0;

        if (!logpoints.isEmpty()) {
            SimpleDateFormat dateFormat = TrackMeConstants.getDateFormat();
            logpointGrid.add(new Label("Logpoints: "), 0, logRowIndex++);
            for (TimeLog log : logpoints) {
                logpointGrid.add(new Label("from " + dateFormat.format(log.getStartTime()) + (log.getEndTime() == null?
                        "":
                        (" to " + dateFormat.format(log.getEndTime())))), 1, logRowIndex++);
            }
        } else {
            logpointGrid.add(new Label("No timelogs available for this activity"), 0, logRowIndex);
        }

        return logpointGrid;
    }

    private Button createNotes() {
        Button noteButton = new Button("View notes");
        noteButton.setGraphic(DisplayUtils.createStyledIcon(FontAwesomeIcon.STICKY_NOTE));
        noteButton.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_ACTIVITY_NOTE_EXPAND));
        noteButton.setOnAction(event -> {
            NoteManager noteManager = application.getActivityController().getNoteManager();
            try {
                Optional<Note> noteForActivity = noteManager.findNoteForActivity(activity.getId());
                Note note;
                if (noteForActivity.isPresent()) {
                    note = noteForActivity.get();
                } else {
                    note = noteManager.createNoteForActivity(activity.getId());
                }

                TextArea textField = new TextArea();
                textField.setPrefWidth(overlay.getWidth());
                textField.setPrefHeight(overlay.getHeight());
                textField.setText(note.getContent().stream().collect(Collectors.joining(System.lineSeparator())));
                textField.setWrapText(true);
                textField.setEditable(true);
                overlay.setContent(textField);
                overlay.setControlButtons(createNoteControlButtons(note, textField));
                overlay.refreshContent();
                overlay.setVisible(true);
            } catch (IOException e) {
                overlay.setContent(new Label("Error reading notes: " + e.getMessage()));
            }
        });
        return noteButton;
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

    private Node createParentSelector() {
        ObservableList<String> options = FXCollections
                .observableArrayList(application.getActivityController().getActivityManager().getAllActivityNames());
        final ComboBox<String> parent = new ComboBox<>(options);
        parent.valueProperty().addListener((ov, t, t1) -> {
            Optional<Activity> savedParent = application.getActivityController().getActivityManager()
                    .getSavedActivityByName(t1);
            if (savedParent.isPresent()) {
                application.getActivityController().getActivityManager().addActivityAsSub(activity, savedParent.get());
            }
            this.parentChanged = true;
        });
        return parent;
    }

    private Node createSubActivities() {
        return new ActivityAccordion(application, activity.getSubActivities());
    }

    private Button createDoneButton() {
        Button done = new Button(DisplayUtils.getDoneButtonText(activity));
        FontAwesomeIconView doneIcon = DisplayUtils.createStyledIcon(FontAwesomeIcon.REFRESH);
        done.setGraphic(doneIcon);
        done.setTooltip(getDoneTooltipText(activity));
        done.setOnAction(event -> {
            try {
                toggleCompleted();
                done.setText(DisplayUtils.getDoneButtonText(activity));
                done.setTooltip(getDoneTooltipText(activity));
                save();
            } catch (IOException | ParseException e) {
                LOG.error(DisplayConstants.ERROR_MESSAGE_ACTIVITY_SAVING + ": " + e.getMessage());
            }
        });

        return done;
    }

    private void toggleCompleted() {
        if (!activity.isCompleted() && !activity.isAllSubActivitiesCompleted()) {
            LOG.warn("Completing activity with incomplete subactivities");
        }

        this.activity.setCompleted(!activity.isCompleted());
        this.updateHeader();
    }

    private Button createEditButton() {
        Button edit = new Button(getEditButonText());
        FontAwesomeIconView editIcon = DisplayUtils.createStyledIcon(FontAwesomeIcon.EDIT);
        edit.setGraphic(editIcon);
        edit.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_ACTIVITY_EDIT));
        edit.setOnAction(event -> {
            try {
                if (isEditable) {
                    makeUneditable();
                    save();
                    setContent(createActivityContent());

                } else {
                    makeEditable();
                    setContent(createActivityContent());
                }
                updateHeader();
                edit.setText(getEditButonText());
            } catch (IOException | ParseException e) {
                LOG.error(DisplayConstants.ERROR_MESSAGE_ACTIVITY_SAVING + ": " + e.getMessage());
            }
        });
        return edit;
    }

    private String getEditButonText() {
        return this.isEditable?DisplayConstants.BUTTON_TEXT_SAVE:DisplayConstants.BUTTON_TEXT_EDIT;
    }

    private Node createDeleteButton() {
        Button delete = new Button(DisplayConstants.BUTTON_TEXT_DELETE);
        FontAwesomeIconView removeIcon = DisplayUtils.createStyledIcon(FontAwesomeIcon.REMOVE);
        delete.setGraphic(removeIcon);
        delete.setOnAction(event -> {
            try {
                this.toggleCompleted();
                application.getActivityController().getActivityManager().delete(this.activity);
                application.reloadActivities();
            } catch (IOException | ParseException e) {
                LOG.error(DisplayConstants.ERROR_MESSAGE_ACTIVITY_SAVING + ": " + e.getMessage());
            }
        });
        delete.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_ACTIVITY_DELETE));
        return delete;
    }

    HBox createTimingControls() {
        activityLog = application.getActivityController().getTimeTrackingManager()
                .getLogForActivityId(this.activity.getId());
        HBox timingControls = new HBox();

        Button startStopButton = new Button(getTimingButtonText());
        Optional<TimeLog> activeLog = activityLog.getActiveLog();
        startStopButton.setOnAction(event -> {
            if (activeLog.isPresent()) {
                activityLog.stopActiveLog();
            } else {
                activityLog.startLog();
            }
            startStopButton.setText(getTimingButtonText());
            startStopButton.setGraphic(getTimingButtonIcon());
            application.getActivityController().getTimeTrackingManager().save(activityLog);
            this.setContent(createActivityContent());
        });

        FontAwesomeIconView iconView = getTimingButtonIcon();
        startStopButton.setGraphic(iconView);
        startStopButton.setTooltip(getTimingButtonTooltipText());

        timingControls.getChildren().add(startStopButton);

        return timingControls;
    }

    private FontAwesomeIconView getTimingButtonIcon() {
        return DisplayUtils.createStyledIcon(
                activityLog.getActiveLog().isPresent()?FontAwesomeIcon.HOURGLASS_END:FontAwesomeIcon.HOURGLASS_START);
    }

    private String getTimingButtonText() {
        Optional<TimeLog> activeLog = getActiveLog();
        if (activeLog.isPresent()) {
            return DisplayConstants.BUTTON_TEXT_TIMER_STOP;
        } else {
            return DisplayConstants.BUTTON_TEXT_TIMER_START;
        }
    }

    private Optional<TimeLog> getActiveLog() {
        activityLog = application.getActivityController().getTimeTrackingManager()
                .getLogForActivityId(this.activity.getId());
        return activityLog.getActiveLog();
    }

    public Tooltip getTimingButtonTooltipText() {
        return DisplayUtils.createTooltip(getActiveLog().isPresent()?
                TooltipConstants.TOOLTIP_TEXT_ACTIVITY_TIMING_CONTROL_STOP:
                TooltipConstants.TOOLTIP_TEXT_ACTIVITY_TIMING_CONTROL_START);
    }

    private void save() throws IOException, ParseException {
        updateActivityFields();
        application.getActivityController().getActivityManager().save(getActivityToSave());
        if (!this.parentChanged) {
            this.refresh();
        } else {
            application.reloadActivities();
        }

    }

    private void updateActivityFields() {
        if (nameField != null) {
            activity.setName(nameField.getText());
        }
        if (locationField != null) {
            activity.setLocation(locationField.getText());
        }
        updateActivityProjects();
        updateActivityTags();
        updateActivityWarningPeriod();
    }

    private void updateActivityWarningPeriod() {
        if (warningPeriodInHours != null && StringUtils.isNotBlank(warningPeriodInHours.getText())
                && warningPeriodInHours.getText().matches(DisplayConstants.REGEX_WARNING_PERIOD)) {
            String warningTimeframe = warningPeriodInHours.getText();
            Duration timeFrame = Duration.ofHours(Long.parseLong(warningTimeframe));
            activity.setWarningTimeFrame(timeFrame);
        }
    }

    private void updateActivityProjects() {
        if (projectsField != null && StringUtils.isNotBlank(projectsField.getText())) {
            String conctatenatedProjects = projectsField.getText();
            List<String> newProjects = splitTextFieldValueOnSeperator(conctatenatedProjects, FIELD_SEPERATOR);
            activity.setProjects(newProjects);
        }
    }

    private void updateActivityTags() {
        if (tagsField != null && StringUtils.isNotBlank(tagsField.getText())) {
            String conctatenatedProjects = tagsField.getText();
            List<String> newTags = splitTextFieldValueOnSeperator(conctatenatedProjects, FIELD_SEPERATOR);
            activity.setTags(newTags);
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

    private Activity getActivityToSave() {
        Activity activityToSave = this.getActivity();
        while (StringUtils.isNotBlank(activityToSave.getParentActivity())) {
            Optional<Activity> savedParent = application.getActivityController().getActivityManager()
                    .getSavedActivityById(activityToSave.getParentActivity());
            if (savedParent.isPresent()) {
                activityToSave = savedParent.get();
            }
        }
        return activityToSave;
    }

    void makeEditable() {
        this.isEditable = true;
    }

    void makeUneditable() {
        this.isEditable = false;
    }

    public Activity getActivity() {
        return activity;
    }

    boolean isEditable() {
        return isEditable;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        LOG.debug("Making pane active: " + this.activity.getName());
        this.isActive = active;
    }

    public void refresh() {
        application.getActivityController().getActivityManager().getSavedActivityById(this.activity.getId().toString())
                .ifPresent(savedActivity -> this.activity = savedActivity);
        this.setContent(this.createContentContainer());
        this.updateHeader();
    }
}
