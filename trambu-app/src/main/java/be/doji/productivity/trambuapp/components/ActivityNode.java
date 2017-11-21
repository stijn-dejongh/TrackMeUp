package be.doji.productivity.trambuapp.components;

import be.doji.productivity.trambuapp.presentation.TrambuApplication;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambucore.TrackMeConstants;
import be.doji.productivity.trambucore.model.tasks.Activity;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class ActivityNode extends TitledPane {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityNode.class);

    private static final String FIELD_SEPERATOR = ",";
    private boolean isActive;
    private TrambuApplication application;
    private boolean isEditable = false;
    private Activity activity;
    private TextField nameField;
    private TextField projectsField;
    private TextField tagsField;
    private ActivityLog activityLog;

    public ActivityNode(Activity activity, TrambuApplication trambuApplication) {
        super();
        this.activity = activity;
        this.application = trambuApplication;
        this.activityLog = application.getTimeTrackingManager().getLogForActivityId(activity.getId());
        createHeader(activity);
        this.setContent(createActivityContent());
        this.setVisible(true);
        this.setOnMouseClicked(event -> this.setActive(!this.isActive));
    }

    private void createHeader(Activity activity) {
        this.setText(activity.getName());
        Button titleLabel = new Button();
        FontAwesomeIconView checkedCalendar = new FontAwesomeIconView(FontAwesomeIcon.CALENDAR_CHECK_ALT);
        checkedCalendar.setGlyphStyle(DisplayConstants.STYLE_GLYPH_DEFAULT);
        FontAwesomeIconView uncheckedCalendar = new FontAwesomeIconView(FontAwesomeIcon.CALENDAR_ALT);
        uncheckedCalendar.setGlyphStyle(DisplayConstants.STYLE_GLYPH_DEFAULT);
        titleLabel.setGraphic(activity.isCompleted()?checkedCalendar:uncheckedCalendar);
        titleLabel.getStyleClass().clear();
        titleLabel.getStyleClass().add("icon-button");
        this.setGraphic(titleLabel);
        this.getStyleClass().clear();
        this.getStyleClass().add(getActivityStyle());
    }

    String getActivityStyle() {
        if (this.activity.isCompleted()) {
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

        content.add(new Label("Tags: "), 0, rowIndex);
        content.add(createTags(), 1, rowIndex++);

        content.add(new Label("Projects: "), 0, rowIndex);
        content.add(createProjects(), 1, rowIndex++);

        content.add(createLogPoints(), 0, rowIndex++, 2, 1);

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

    protected GridPane createActvityControls() {
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

    Node createUneditablePriority() {
        return new Label(activity.getPriority());
    }

    Node createDeadline() {
        if (isEditable) {
            return createEditableDeadline();
        } else {
            return createUneditableDeadline();
        }
    }

    private LocalDate datePickerDate;

    Node createEditableDeadline() {
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

    Node createUneditableDeadline() {
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
        tagsField = new TextField();
        reducedTags.ifPresent(s -> tagsField.setText(s));
        return tagsField;
    }

    private HBox createUneditableTags() {
        HBox tags = new HBox(5);
        tags.getChildren().addAll(activity.getTags().stream().map(tag -> {
            Button button = new Button(tag);
            button.setOnAction(e -> {
                application.setTagFilter(tag);
                application.updateActivities();
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
        projectsField = new TextField();
        reducedProjects.ifPresent(s -> projectsField.setText(s));
        return projectsField;
    }

    private HBox createUneditableProjects() {
        HBox projecs = new HBox();
        projecs.getChildren().addAll(activity.getProjects().stream().map(project -> {
            Button button = new Button(project);
            button.setOnAction(e -> {
                application.setProjectFilter(project);
                application.updateActivities();
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
        if (!logpoints.isEmpty()) {
            SimpleDateFormat dateFormat = TrackMeConstants.getDateFormat();
            logpointGrid.add(new Label("Logpoints: "), 0, logRowIndex++);
            for (TimeLog log : logpoints) {
                logpointGrid.add(new Label("from " + dateFormat.format(log.getStartTime()) + (log.getEndTime() == null?
                        "":
                        (" to " + dateFormat.format(log.getEndTime())))), 1, logRowIndex++);
            }
        }

        logpointGrid.add(new Label("Time spent on activity: "), 0, logRowIndex);
        logpointGrid.add(new Label(activityLog.getTimeSpent()), 1, logRowIndex);
        return logpointGrid;
    }

    private Node createParentSelector() {
        ObservableList<String> options = FXCollections
                .observableArrayList(application.getActivityManager().getAllActivityNames());
        final ComboBox<String> parent = new ComboBox<>(options);
        parent.valueProperty().addListener((ov, t, t1) -> {
            Optional<Activity> savedParent = application.getActivityManager().getSavedActivityByName(t1);
            if (savedParent.isPresent()) {
                application.getActivityManager().addActivityAsSub(activity, savedParent.get());
            }
        });
        return parent;
    }

    private Node createSubActivities() {
        Accordion activityAcordeon = new Accordion();
        List<TitledPane> activityNodes = createSubActivityNodes();
        activityAcordeon.getPanes().addAll(activityNodes);
        return activityAcordeon;
    }

    private List<TitledPane> createSubActivityNodes() {
        return activity.getSubActivities().stream().map(sub -> new ActivityNode(sub, application))
                .collect(Collectors.toList());
    }

    private Button createDoneButton() {
        Button done = new Button(DisplayUtils.getDoneButtonText(activity));
        FontAwesomeIconView doneIcon = new FontAwesomeIconView(FontAwesomeIcon.REFRESH);
        doneIcon.setGlyphStyle(DisplayConstants.STYLE_GLYPH_DEFAULT);
        done.setGraphic(doneIcon);
        done.setOnAction(event -> {
            try {
                this.activity.setCompleted(!activity.isCompleted());
                done.setText(DisplayUtils.getDoneButtonText(activity));
                save();
            } catch (IOException | ParseException e) {
                LOG.error(DisplayConstants.ERROR_MESSAGE_ACTIVITY_SAVING + ": " + e.getMessage());
            }
        });

        return done;
    }

    private Button createEditButton() {
        Button edit = new Button(getEditButonText());
        FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
        editIcon.setGlyphStyle(DisplayConstants.STYLE_GLYPH_DEFAULT);
        edit.setGraphic(editIcon);
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
        FontAwesomeIconView removeIcon = new FontAwesomeIconView(FontAwesomeIcon.REMOVE);
        removeIcon.setGlyphStyle(DisplayConstants.STYLE_GLYPH_DEFAULT);
        delete.setGraphic(removeIcon);
        delete.setOnAction(event -> {
            try {
                this.activity.setCompleted(!activity.isCompleted());
                application.getActivityManager().delete(this.activity);
                application.updateActivities();
            } catch (IOException | ParseException e) {
                LOG.error(DisplayConstants.ERROR_MESSAGE_ACTIVITY_SAVING + ": " + e.getMessage());
            }
        });
        delete.getStyleClass().clear();
        delete.getStyleClass().add("error-button");
        return delete;
    }

    HBox createTimingControls() {
        activityLog = application.getTimeTrackingManager().getLogForActivityId(this.activity.getId());
        HBox timingControls = new HBox();

        Button startStopButton = new Button(getTimingButtonText());
        startStopButton.setOnAction(event -> {
            Optional<TimeLog> activeLog = activityLog.getActiveLog();
            if (activeLog.isPresent()) {
                activityLog.stopActiveLog();
            } else {
                activityLog.startLog();
            }
            startStopButton.setText(getEditButonText());
            startStopButton.setGraphic(getTimingButtonIcon());
            application.getTimeTrackingManager().save(activityLog);
            this.setContent(createActivityContent());
        });

        FontAwesomeIconView iconView = getTimingButtonIcon();
        startStopButton.setGraphic(iconView);

        timingControls.getChildren().add(startStopButton);

        return timingControls;
    }

    private FontAwesomeIconView getTimingButtonIcon() {
        FontAwesomeIcon icon = FontAwesomeIcon.HOURGLASS_START;
        if (activityLog.getActiveLog().isPresent()) {
            icon = FontAwesomeIcon.HOURGLASS_END;
        }

        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setGlyphStyle(DisplayConstants.STYLE_GLYPH_DEFAULT);
        return iconView;
    }

    private String getTimingButtonText() {
        activityLog = application.getTimeTrackingManager().getLogForActivityId(this.activity.getId());
        Optional<TimeLog> activeLog = activityLog.getActiveLog();
        if (activeLog.isPresent()) {
            return DisplayConstants.BUTTON_TEXT_TIMER_STOP;
        } else {
            return DisplayConstants.BUTTON_TEXT_TIMER_START;
        }
    }

    private void save() throws IOException, ParseException {
        updateActivityFields();
        application.getActivityManager().save(getActivityToSave());
        application.updateActivities();
    }

    private void updateActivityFields() {
        if (nameField != null) {
            activity.setName(nameField.getText());
        }
        updateActivityProjects();
        updateActivityTags();

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
            Optional<Activity> savedParent = application.getActivityManager()
                    .getSavedActivityById(activityToSave.getParentActivity());
            if (savedParent.isPresent()) {
                activityToSave = savedParent.get();
            }
        }
        return activityToSave;
    }

    public UUID getActivityId() {
        return this.activity.getId();
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
        this.isActive = active;
        if (active) {
            application.setActivePane(this);
        } else {
            application.ressetActiveActivityId();
        }
    }
}
