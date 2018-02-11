package be.doji.productivity.trambuapp.components.presenter;

import be.doji.productivity.trambuapp.components.view.TimesheetView;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambucore.exporters.TimesheetToCSVExporter;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.model.tracker.ActivityLog;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimesheetPresenter extends Presenter {

  private static final Logger LOG = LoggerFactory.getLogger(TimesheetPresenter.class);
  private final TimesheetView view;
  private final ManagerContainer managers;
  private Date startDate;
  private Date endDate;
  private List<ActivityLog> logs;

  public TimesheetPresenter(TimesheetView view) {
    this.view = view;
    this.managers = ManagerContainer.Factory.getInstance();

    initDefaultTimeInterval();
  }

  private void initDefaultTimeInterval() {
    this.endDate = new Date();
    this.startDate = DateUtils.addDays(endDate, -7);
  }


  @Override
  void refresh() {
    this.logs = managers.getTimeTrackingManager()
        .getActivityLogsInInterval(startDate, endDate);
  }

  @Override
  void populate() {
    this.refresh();
    this.view.refresh();
  }

  /**
   * Updates the logs based on the configured start and end date.
   *
   * @return A {@link ActivityLog} object containing entries that fall between getStartDate() and
   * getEndDate
   */
  public List<ActivityLog> getLogs() {
    this.refresh();
    return this.logs;
  }

  public Date getStartDate() {
    return startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void endDatePicked() {
    this.endDate = Date
        .from(view.getEndDatePicker().getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
  }


  public void startDatePicked() {
    this.startDate = Date
        .from(
            view.getStartDatePicker().getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  public String getActivityName(UUID activityId) {
    Optional<Activity> savedActivityById = this.managers.getActivityManager()
        .getSavedActivityById(activityId.toString());
    return savedActivityById.isPresent() ? savedActivityById.get().getName()
        : DisplayConstants.TEXT_ACTIVITY_UNKNOWN;
  }

  public void exportClicked() {

    File file = view.getExportFileChooser().showSaveDialog(null);
    if (file != null) {

      try {
        TimesheetToCSVExporter exporter = new TimesheetToCSVExporter(
            managers.getActivityManager());
        List<String> convert = exporter.convert(logs);
        Files.write(Paths.get(file.getAbsolutePath()), convert);
        LOG.info("Export completed");
      } catch (IOException e) {
        LOG.error("Error while exporting timesheet");
      }
    }

  }
}
