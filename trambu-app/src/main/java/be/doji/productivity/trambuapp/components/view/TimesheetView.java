package be.doji.productivity.trambuapp.components.view;

import be.doji.productivity.trambuapp.components.presenter.TimesheetPresenter;
import be.doji.productivity.trambuapp.controls.MainMenuBar;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambuapp.utils.TooltipConstants;
import be.doji.productivity.trambucore.TrackMeConstants;
import be.doji.productivity.trambucore.model.tracker.ActivityLog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.util.List;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;
import tornadofx.View;

public class TimesheetView extends View {

  private BorderPane root;
  private TimesheetPresenter presenter;
  private DatePicker endDatePicker;
  private DatePicker startDatePicker;
  private FileChooser exportFileChooser;
  private GridPane timeLogGrid;


  public TimesheetView() {
    super();
    this.presenter = new TimesheetPresenter(this);
    initContent();
  }

  private void initContent() {
    this.setTitle(DisplayConstants.TITLE_APPLICATION + " - " + DisplayConstants.TITLE_TIMESHEET);

    root = new BorderPane();
    root.setPrefHeight(DisplayConstants.UI_DEFAULT_WINDOW_HEIGHT);
    root.setPrefWidth(DisplayConstants.UI_DEFAULT_WINDOW_WIDTH);
    root.setCenter(createTimesheetPane());
    root.setBottom(new MainMenuBar(this).getRoot());

    this.timeLogGrid = initTimeLogGrid(presenter.getLogs());
  }

  private SplitPane createTimesheetPane() {
    SplitPane splitPane = new SplitPane();
    splitPane.setPrefHeight(DisplayConstants.UI_DEFAULT_WINDOW_HEIGHT);
    splitPane.setPrefWidth(DisplayConstants.UI_DEFAULT_WINDOW_WIDTH);
    splitPane.setOrientation(Orientation.HORIZONTAL);
    splitPane.setDividerPosition(0, 0.65);

    splitPane.getItems().add(this.timeLogGrid);
    splitPane.getItems().add(createTimesheetControls());
    return splitPane;
  }

  private GridPane createTimesheetControls() {

    GridPane controls = new GridPane();
    controls.setVgap(5);
    controls.setHgap(3);

    this.startDatePicker = createStartDatePicker();
    Label startDateLabel = new Label("Start Date");
    controls.add(startDateLabel, 0, 0);
    controls.add(this.startDatePicker, 1, 0);

    this.endDatePicker = initEndDatePicker();

    Label endDateLabel = new Label("End Date");
    controls.add(endDateLabel, 0, 1);
    controls.add(this.endDatePicker, 1, 1);

    controls.add(DisplayUtils.createHorizontalSpacer(), 0, 2, 2, 1);

    Button refreshTimeSheet = createRefreshButton();
    controls.add(refreshTimeSheet, 0, 3, 2, 1);

    Button exportTimeSheer = createExportButton();
    controls.add(exportTimeSheer, 0, 4, 2, 1);

    return controls;
  }

  @NotNull
  private DatePicker initEndDatePicker() {
    this.endDatePicker = new DatePicker();
    endDatePicker.setOnAction(event -> this.presenter.endDatePicked());
    return endDatePicker;
  }

  @NotNull
  private DatePicker createStartDatePicker() {
    this.startDatePicker = new DatePicker();
    startDatePicker.setOnAction(event -> this.presenter.startDatePicked());
    return startDatePicker;
  }

  @NotNull
  private Button createRefreshButton() {
    Button refreshTimeSheet = new Button("Get timesheet");
    refreshTimeSheet.setGraphic(DisplayUtils.createStyledIcon(FontAwesomeIcon.REFRESH));
    refreshTimeSheet.setOnAction(event -> refresh());
    refreshTimeSheet
        .setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_TIMESHEET_REFRESH));
    return refreshTimeSheet;
  }

  @NotNull
  private Button createExportButton() {
    initExportFileChooser();
    Button exportTimesheet = new Button("Export");
    exportTimesheet.setGraphic(DisplayUtils.createStyledIcon(FontAwesomeIcon.ARROW_DOWN));
    exportTimesheet.setOnAction(event -> presenter.exportClicked());
    exportTimesheet
        .setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_TIMESHEET_EXPORT));
    return exportTimesheet;
  }

  private void initExportFileChooser() {
    this.exportFileChooser = new FileChooser();
    exportFileChooser.setTitle("Select export file");
    exportFileChooser.setInitialFileName("Timesheet_export" + ".csv");
  }

  public void refresh() {
    this.timeLogGrid = initTimeLogGrid(presenter.getLogs());
    root.setCenter(createTimesheetPane());
  }

  private GridPane initTimeLogGrid(List<ActivityLog> activityLogsInInterval) {
    GridPane grid = new GridPane();
    grid.setHgap(5);
    grid.setVgap(5);

    int row = 0;
    Label entrieTitle = new Label(
        "Timesheet for " + TrackMeConstants.getDateFormat().format(presenter.getStartDate())
            + " to "
            + TrackMeConstants
            .getDateFormat().format(presenter.getEndDate()));
    entrieTitle.getStyleClass().add(DisplayConstants.STYLE_LABEL_SPECIAL);
    grid.add(entrieTitle, 0, row++);
    grid.add(DisplayUtils.createHorizontalSpacer(), 0, row++, 3, 1);

    for (ActivityLog log : activityLogsInInterval) {
      Label activityLabel = new Label();
      activityLabel.setText(this.presenter.getActivityName(log.getActivityId()));
      grid.add(activityLabel, 0, row);

      Label seperatorLabel = new Label(":");
      grid.add(seperatorLabel, 1, row);

      Label timeLabel = new Label(log.getTimeSpentInHoursString());
      grid.add(timeLabel, 2, row++);
    }
    return grid;
  }

  public DatePicker getEndDatePicker() {
    return endDatePicker;
  }

  public DatePicker getStartDatePicker() {
    return startDatePicker;
  }

  GridPane getTimeLogGrid() {
    return timeLogGrid;
  }

  @NotNull
  @Override
  public Parent getRoot() {
    return root;
  }

  @Override
  public void onDock() {
    this.refresh();
  }

  public FileChooser getExportFileChooser() {
    return this.exportFileChooser;
  }

  public TimesheetPresenter getPresenter() {
    return this.presenter;
  }
}
