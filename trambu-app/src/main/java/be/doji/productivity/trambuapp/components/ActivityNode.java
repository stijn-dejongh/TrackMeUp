package be.doji.productivity.trambuapp.components;

import be.doji.productivity.trackme.TrackMeConstants;
import be.doji.productivity.trackme.model.tasks.Activity;
import be.doji.productivity.trackme.model.tracker.ActivityLog;
import be.doji.productivity.trackme.model.tracker.TimeLog;
import be.doji.productivity.trambuapp.presentation.TrambuApplication;
import be.doji.productivity.trambuapp.presentation.util.DisplayUtils;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class ActivityNode extends TitledPane {

    private TrambuApplication application;

    public ActivityNode(Activity activity, TrambuApplication trambuApplication) {
        super();
        this.application = trambuApplication;
        this.setText(activity.getName());
        Button titleLabel = AwesomeDude
                .createIconButton(activity.isCompleted()?AwesomeIcon.CHECK_SIGN:AwesomeIcon.CHECK_EMPTY);
        titleLabel.getStyleClass().clear();
        titleLabel.getStyleClass().add("icon-button");
        this.setGraphic(titleLabel);
        this.getStyleClass().clear();
        this.getStyleClass().add(activity.isCompleted()?"done":activity.isAlertActive()?"alert":"todo");
        this.setContent(createActivityContent(activity));
        this.setVisible(true);
    }

    private GridPane createActivityContent(Activity activity) {
        GridPane content = new GridPane();
        content.setVgap(4);
        content.setPadding(new Insets(5, 5, 5, 5));
        int rowIndex = 0;

        Button done = new Button(DisplayUtils.getDoneButtonText(activity));
        done.setOnAction(event -> {
            try {
                activity.setCompleted(!activity.isCompleted());
                done.setText(DisplayUtils.getDoneButtonText(activity));
                application.getActivityManager().save(activity);
                application.updateActivities();
            } catch (IOException | ParseException e) {
                System.out.println("Error while saving activity: " + e.getMessage());
            }
        });
        content.add(done, 0, rowIndex++);

        content.add(new Label("Priority: "), 0, rowIndex);
        content.add(new Label(activity.getPriority()), 1, rowIndex++);

        if (activity.isSetDeadline()) {
            content.add(new Label("Deadline: "), 0, rowIndex);
            Label deadlineLabel = new Label(
                    DateFormat.getDateInstance(DateFormat.DEFAULT).format(activity.getDeadline()));
            if (activity.isAlertActive()) {
                deadlineLabel.getStyleClass().add("warningLabel");
            }
            content.add(deadlineLabel, 1, rowIndex++);
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

}
