package be.doji.productivity.trambuapp.components;

import be.doji.productivity.trackme.TrackMeConstants;
import be.doji.productivity.trackme.model.tasks.Activity;
import be.doji.productivity.trackme.model.tracker.ActivityLog;
import be.doji.productivity.trackme.model.tracker.TimeLog;
import be.doji.productivity.trambuapp.presentation.TrambuApplication;
import be.doji.productivity.trambuapp.presentation.util.DisplayUtils;
import be.doji.productivity.trambuapp.utils.TrambuApplicationConstants;
import de.jensd.fx.glyphs.GlyphsStyle;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActivityNode extends TitledPane {

    private TrambuApplication application;
    private boolean isEditable = false;
    private Activity activity;
    private TextField nameField;

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
        titleLabel.setGraphic(activity.isCompleted()?checkedCalendar:
                uncheckedCalendar);
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

        HBox tags = new HBox(5);
        tags.getChildren().addAll(activity.getTags().stream().map(tag -> {
            Button button = new Button(tag);
            button.setOnAction(e -> {
                application.setTagFilter(tag);
                application.updateActivities();
            });
            return button;
        }).collect(Collectors.toList()));
        content.add(new Label("Tags: "), 0, rowIndex);
        content.add(tags, 1, rowIndex++);

        HBox projecs = new HBox();
        projecs.getChildren().addAll(activity.getProjects().stream().map(project -> {
            Button button = new Button(project);
            button.setOnAction(e -> {
                application.setProjectFilter(project);
                application.updateActivities();
            });
            return button;
        }).collect(Collectors.toList()));
        content.add(new Label("Projects: "), 0, rowIndex);
        content.add(projecs, 1, rowIndex++);

        content.add(createLogPoints(), 0, rowIndex++, 2, 1);

        if (!activity.getSubActivities().isEmpty()) {
            Label subActivityTitle = new Label("Subactivities: ");
            subActivityTitle.getStyleClass().clear();
            subActivityTitle.getStyleClass().add("separator-label");
            content.add(subActivityTitle, 0, rowIndex++);
            content.add(createSubActivities(), 0, rowIndex++, 2, 1);
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
        content.setPadding(new Insets(5, 5, 5, 5));
        content.add(createDoneButton(), 0, 0);
        content.add(createEditButton(), 1, 0);
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

    LocalDate datePickerDate;

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
        logpointGrid.add(new Label(activityLog.getTimeSpent()), 1, logRowIndex++);
        return logpointGrid;
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

    private Button createEditButton() {
        Button edit = new Button(getEditButonText());
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

    private Button createDoneButton() {
        Button done = new Button(DisplayUtils.getDoneButtonText(activity));
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

    private void save() throws IOException, ParseException {
        if (nameField != null) {
            activity.setName(nameField.getText());
        }
        application.getActivityManager().save(getActivityToSave());
        application.updateActivities();
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

    public String getEditButonText() {
        return this.isEditable?"Save":"Edit";
    }

    public Activity getActivity() {
        return activity;
    }
}
