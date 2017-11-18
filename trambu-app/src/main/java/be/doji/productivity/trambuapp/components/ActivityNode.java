package be.doji.productivity.trambuapp.components;

import be.doji.productivity.trackme.TrackMeConstants;
import be.doji.productivity.trackme.model.tasks.Activity;
import be.doji.productivity.trackme.model.tracker.ActivityLog;
import be.doji.productivity.trackme.model.tracker.TimeLog;
import be.doji.productivity.trambuapp.presentation.TrambuApplication;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambuapp.utils.TrambuApplicationConstants;
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

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActivityNode extends TitledPane {

    private static final String FIELD_SEPERATOR = ",";
    private TrambuApplication application;
    private boolean isEditable = false;
    private Activity activity;
    private TextField nameField;
    private TextField projectsField;
    private TextField tagsField;

    public ActivityNode(Activity activity, TrambuApplication trambuApplication) {
        super();
        this.activity = activity;
        this.application = trambuApplication;
        this.setText(activity.getName());
        Button titleLabel = new Button();
        FontAwesomeIconView checkedCalendar = new FontAwesomeIconView(FontAwesomeIcon.CALENDAR_CHECK_ALT);
        checkedCalendar.setGlyphStyle(TrambuApplicationConstants.GLYPH_DEFAULT_STYLE);
        FontAwesomeIconView uncheckedCalendar = new FontAwesomeIconView(FontAwesomeIcon.CALENDAR_ALT);
        uncheckedCalendar.setGlyphStyle(TrambuApplicationConstants.GLYPH_DEFAULT_STYLE);
        titleLabel.setGraphic(activity.isCompleted()?checkedCalendar:uncheckedCalendar);
        titleLabel.getStyleClass().clear();
        titleLabel.getStyleClass().add("icon-button");
        this.setGraphic(titleLabel);
        this.getStyleClass().clear();
        this.getStyleClass().add(activity.isCompleted()?"done":activity.isAlertActive()?"alert":"todo");
        this.setContent(createActivityContent());
        this.setVisible(true);
    }

    private GridPane createActivityContent() {
        GridPane content = new GridPane();
        content.setVgap(4);
        content.setPadding(new Insets(5, 5, 5, 5));
        int rowIndex = 0;

        content.add(createActvityControls(), 0, rowIndex++, 2, 1);

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

    private Node createActvityControls() {
        GridPane content = new GridPane();
        content.setVgap(4);
        content.setHgap(4);
        content.setPadding(new Insets(5, 5, 5, 5));
        content.add(createDoneButton(), 0, 0);
        content.add(createEditButton(), 1, 0);
        content.add(createDeleteButton(), 2, 0);
        return content;
    }

    private Node createPriority() {
        if (isEditable) {
            return createEditablePriority();
        } else {
            return createUneditablePriority();
        }
    }

    private Node createEditablePriority() {
        ObservableList<String> options = FXCollections.observableArrayList(TrackMeConstants.getPriorityList());
        final ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.setValue(activity.getPriority());
        comboBox.valueProperty().addListener((ov, t, t1) -> activity.setPriority(t1));
        return comboBox;
    }

    private Node createUneditablePriority() {
        return new Label(activity.getPriority());
    }

    private Node createDeadline() {
        if (isEditable) {
            return createEditableDeadline();
        } else {
            return createUneditableDeadline();
        }
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

    private Node createTags() {
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

    private Node createProjects() {
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
        ActivityLog activityLog = application.getTimeTrackingManager().getLogForActivityId(activity.getId());
        List<TimeLog> logpoints = activityLog.getLogpoints();

        GridPane logpointGrid = new GridPane();
        logpointGrid.setVgap(4);
        logpointGrid.setPadding(new Insets(5, 5, 5, 5));
        int logRowIndex = 0;
        if (!logpoints.isEmpty()) {
            SimpleDateFormat dateFormat = TrackMeConstants.getDateFormat();
            logpointGrid.add(new Label("Logpoints: "), 0, logRowIndex++);
            for (TimeLog log : logpoints) {
                logpointGrid.add(new Label(
                                "from " + dateFormat.format(log.getStartTime()) + " to " + dateFormat.format(log.getEndTime())),
                        1, logRowIndex++);
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
        doneIcon.setGlyphStyle(TrambuApplicationConstants.GLYPH_DEFAULT_STYLE);
        done.setGraphic(doneIcon);
        done.setOnAction(event -> {
            try {
                this.activity.setCompleted(!activity.isCompleted());
                done.setText(DisplayUtils.getDoneButtonText(activity));
                save();
            } catch (IOException | ParseException e) {
                System.out.println("Error while saving activity: " + e.getMessage());
            }
        });

        return done;
    }

    private Button createEditButton() {
        Button edit = new Button(getEditButonText());
        FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
        editIcon.setGlyphStyle(TrambuApplicationConstants.GLYPH_DEFAULT_STYLE);
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
                System.out.println("Error while saving activity: " + e.getMessage());
            }
        });
        return edit;
    }

    private Node createDeleteButton() {
        Button delete = new Button("Delete");
        FontAwesomeIconView removeIcon = new FontAwesomeIconView(FontAwesomeIcon.REMOVE);
        removeIcon.setGlyphStyle(TrambuApplicationConstants.GLYPH_DEFAULT_STYLE);
        delete.setGraphic(removeIcon);
        delete.setOnAction(event -> {
            try {
                this.activity.setCompleted(!activity.isCompleted());
                application.getActivityManager().delete(this.activity);
                application.updateActivities();
            } catch (IOException | ParseException e) {
                System.out.println("Error while saving activity: " + e.getMessage());
            }
        });
        delete.getStyleClass().clear();
        delete.getStyleClass().add("error-button");
        return delete;
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

    private void makeEditable() {
        this.isEditable = true;
    }

    private void makeUneditable() {
        this.isEditable = false;
    }

    private String getEditButonText() {
        return this.isEditable?"Save":"Edit";
    }

    public Activity getActivity() {
        return activity;
    }

}
