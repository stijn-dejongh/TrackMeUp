package be.doji.productivity.trambuapp.views;

import be.doji.productivity.trambuapp.controllers.ActivityController;
import be.doji.productivity.trambuapp.controls.MainMenuBar;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambuapp.utils.TooltipConstants;
import be.doji.productivity.trambucore.TrackMeConstants;
import be.doji.productivity.trambucore.exporters.TimesheetToCSVExporter;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.model.tracker.ActivityLog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.time.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tornadofx.View;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TimesheetView extends View {

    private static final Logger LOG = LoggerFactory.getLogger(TimesheetView.class);

    private final ActivityController activityController;
    private List<ActivityLog> logs;
    private BorderPane root;
    private Date startDate;
    private Date endDate;

    public TimesheetView() {
        super();
        this.setTitle(DisplayConstants.TITLE_APPLICATION + " - " + DisplayConstants.TITLE_TIMESHEET);
        this.activityController = find(ActivityController.class);

        endDate = new Date();
        startDate = DateUtils.addDays(endDate, -7);

        root = new BorderPane();
        root.setPrefHeight(DisplayConstants.UI_DEFAULT_WINDOW_HEIGHT);
        root.setPrefWidth(DisplayConstants.UI_DEFAULT_WINDOW_WIDTH);
        root.setCenter(createTimesheetPane());
        root.setBottom(new MainMenuBar(this).getRoot());
    }

    private SplitPane createTimesheetPane() {
        SplitPane splitPane = new SplitPane();
        splitPane.setPrefHeight(DisplayConstants.UI_DEFAULT_WINDOW_HEIGHT);
        splitPane.setPrefWidth(DisplayConstants.UI_DEFAULT_WINDOW_WIDTH);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPosition(0, 0.65);
        logs = activityController.getTimeTrackingManager().getActivityLogsInInterval(startDate, endDate);
        splitPane.getItems().add(createTimeLogGrid(logs));
        splitPane.getItems().add(createTimesheetControls());
        return splitPane;
    }

    private GridPane createTimesheetControls() {

        GridPane controls = new GridPane();
        controls.setVgap(5);
        controls.setHgap(3);

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setOnAction(event -> startDate = Date
                .from(startDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        Label startDateLabel = new Label("Start Date");
        controls.add(startDateLabel, 0, 0);
        controls.add(startDatePicker, 1, 0);

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setOnAction(event -> endDate = Date
                .from(endDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        Label endDateLabel = new Label("End Date");
        controls.add(endDateLabel, 0, 1);
        controls.add(endDatePicker, 1, 1);

        controls.add(DisplayUtils.createHorizontalSpacer(), 0, 2, 2, 1);

        Button refreshTimeSheet = createRefreshButton();
        controls.add(refreshTimeSheet, 0, 3, 2, 1);

        Button exportTimeSheer = createExportButton();
        controls.add(exportTimeSheer, 0, 4, 2, 1);

        return controls;
    }

    @NotNull private Button createRefreshButton() {
        Button refreshTimeSheet = new Button("Get timesheet");
        refreshTimeSheet.setGraphic(DisplayUtils.createStyledIcon(FontAwesomeIcon.REFRESH));
        refreshTimeSheet.setOnAction(event -> refresh());
        refreshTimeSheet.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_TIMESHEET_REFRESH));
        return refreshTimeSheet;
    }

    @NotNull private Button createExportButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select export file");
        fileChooser.setInitialFileName("Timesheet_export" + ".csv");

        Button exportTimesheet = new Button("Export");
        exportTimesheet.setGraphic(DisplayUtils.createStyledIcon(FontAwesomeIcon.ARROW_DOWN));
        exportTimesheet.setOnAction(event -> {
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {

                try {
                    TimesheetToCSVExporter exporter = new TimesheetToCSVExporter(
                            activityController.getActivityManager());
                    List<String> convert = exporter.convert(logs);
                    Files.write(Paths.get(file.getAbsolutePath()), convert);
                    LOG.info("Export completed");
                } catch (IOException e) {
                    LOG.error("Error while exporting timesheet");
                }
            }
        });
        exportTimesheet.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_TIMESHEET_EXPORT));
        return exportTimesheet;
    }

    private void refresh() {
        root.setCenter(createTimesheetPane());
    }

    private GridPane createTimeLogGrid(List<ActivityLog> activityLogsInInterval) {
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);

        int row = 0;
        Label entrieTitle = new Label(
                "Timesheet for " + TrackMeConstants.getDateFormat().format(startDate) + " to " + TrackMeConstants
                        .getDateFormat().format(endDate));
        entrieTitle.getStyleClass().add(DisplayConstants.STYLE_LABEL_SPECIAL);
        grid.add(entrieTitle, 0, row++);
        grid.add(DisplayUtils.createHorizontalSpacer(), 0, row++, 3, 1);

        LOG.debug("Found {0} timelog entries", activityLogsInInterval.size());
        for (ActivityLog log : activityLogsInInterval) {
            Label activityLabel = new Label();
            activityLabel.setText(getActivityName(log.getActivityId()));
            grid.add(activityLabel, 0, row);

            Label seperatorLabel = new Label(":");
            grid.add(seperatorLabel, 1, row);

            Label timeLabel = new Label(log.getTimeSpentInHoursString());
            grid.add(timeLabel, 2, row++);
        }
        return grid;
    }

    private String getActivityName(UUID activityId) {
        Optional<Activity> savedActivityById = this.activityController.getActivityManager()
                .getSavedActivityById(activityId.toString());
        return savedActivityById.isPresent()?savedActivityById.get().getName():DisplayConstants.TEXT_ACTIVITY_UNKNOWN;
    }

    @NotNull @Override public Parent getRoot() {
        return root;
    }

    @Override public void onDock() {
        this.refresh();
    }
}
