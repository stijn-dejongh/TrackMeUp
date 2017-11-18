package be.doji.productivity.trambuapp.components;

import be.doji.productivity.trackme.TrackMeConstants;
import be.doji.productivity.trackme.model.tasks.Activity;
import be.doji.productivity.trackme.model.tracker.ActivityLog;
import be.doji.productivity.trackme.model.tracker.TimeLog;
import be.doji.productivity.trambuapp.presentation.TrambuApplication;
import be.doji.productivity.trambuapp.presentation.util.DisplayUtils;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ActivityNode extends TitledPane {

    private TrambuApplication application;
    private boolean isEditable = false;
    private Activity activity;

    public ActivityNode(Activity activity, TrambuApplication trambuApplication) {
        super();
        this.activity = activity;
        this.application = trambuApplication;
        this.setText(activity.getName());
        Button titleLabel = AwesomeDude
                .createIconButton(activity.isCompleted()?AwesomeIcon.CHECK_SIGN:AwesomeIcon.CHECK_EMPTY);
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

        content.add(createDoneButton(), 0, rowIndex);
        content.add(createEditButton(), 1, rowIndex++);

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

        ActivityLog activityLog = application.getTimeTrackingManager().getLogForActivityId(activity.getId());
        System.out.println(activity.getId());
        List<TimeLog> logpoints = activityLog.getLogpoints();

        SimpleDateFormat dateFormat = TrackMeConstants.getDateFormat();
        content.add(new Label("Logpoints: "), 0, rowIndex++);
        for (TimeLog log : logpoints) {
            content.add(new Label(
                            "from " + dateFormat.format(log.getStartTime()) + " to " + dateFormat.format(log.getEndTime())), 1,
                    rowIndex++);
        }
        content.add(new Label("Time spent on activity: "), 0, rowIndex);
        content.add(new Label(activityLog.getTimeSpent()), 1, rowIndex++);

        content.setVisible(true);
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
        application.getActivityManager().save(this.getActivity());
        application.updateActivities();
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
