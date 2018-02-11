package be.doji.productivity.trambuapp.components.view;

import be.doji.productivity.trambuapp.components.TrambuAppTest;
import be.doji.productivity.trambucore.model.tracker.ActivityLog;
import be.doji.productivity.trambucore.model.tracker.TimeLog;
import be.doji.productivity.trambucore.testutil.ActivityTestData;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the TimesheetView MVP model. Testing both TimesheetView and TimesheetPresenter classes
 */
public class TimesheetViewTest extends TrambuAppTest {

  private static final String ACTIVITY_ID = "283b6271-b513-4e89-b757-10e98c9078ea";

  @Test
  public void failIfFaultyViewInitialization() {
    TimesheetView view = new TimesheetView();

    Assert.assertNotNull("Expect the timesheetPresenter to be created", view.getPresenter());
    Assert.assertNotNull("Expect the EndDatePicker to be created", view.getEndDatePicker());
    Assert.assertNotNull("Expect the StartDatePicker to be created", view.getStartDatePicker());
    Assert.assertNotNull("Expect the UI component to be created", view.getRoot());
    Assert.assertTrue("Expect the UI component to be a BorderPane",
        view.getRoot() instanceof BorderPane);
    BorderPane castedRoot = (BorderPane) view.getRoot();

    Assert.assertNotNull("Expect the UI component to contain a logdisplay object",
        castedRoot.getCenter());
    Assert.assertTrue("Expect the logdisplay object to be a SplitPane",
        castedRoot.getCenter() instanceof SplitPane);
    Assert.assertNotNull("Expect the UI component to contain a menubar container object",
        castedRoot.getBottom());
    Assert.assertTrue("Expect the menubar container object to be a HBox",
        castedRoot.getBottom() instanceof HBox);
    HBox castedBar = (HBox) castedRoot.getBottom();
    Assert.assertNotNull(castedBar);
    Assert.assertFalse("Expect menu bar to have content", castedBar.getChildren().isEmpty());
    Assert.assertEquals(3, castedBar.getChildren().size());
  }

  @Test
  public void failIfLogPointContentIsEmpty() throws ParseException {
    getActivityManager().addActivity(ActivityTestData.ACTIVITY_DATA_LINE);
    ActivityLog logActivityOne = new ActivityLog(UUID.fromString(ACTIVITY_ID));
    Calendar logOneStart = new GregorianCalendar(2017, Calendar.DECEMBER, 1, 14, 0, 0);
    Calendar logOneEnd = new GregorianCalendar(2017, Calendar.DECEMBER, 1, 18, 0, 0);
    logActivityOne.addLogPoint(createTimeLog(logOneStart.getTime(), logOneEnd.getTime()));
    Calendar logTwoStart = new GregorianCalendar(2017, Calendar.DECEMBER, 4, 14, 0, 0);
    Calendar logTwoEnd = new GregorianCalendar(2017, Calendar.DECEMBER, 4, 19, 0, 0);
    logActivityOne.addLogPoint(createTimeLog(logTwoStart.getTime(), logTwoEnd.getTime()));
    getMockActController().getTimeTrackingManager().save(logActivityOne);
    Assert.assertNotNull("Expect the timelog to exist",
        getMockActController().getTimeTrackingManager().getLogForActivityId(ACTIVITY_ID));
    Assert.assertEquals(2,
        getMockActController().getTimeTrackingManager().getLogForActivityId(ACTIVITY_ID)
            .getLogpoints().size());

    TimesheetView view = new TimesheetView();
    LocalDate startDate = LocalDate.of(2016, 1, 1);
    view.getStartDatePicker().setValue(startDate);
    view.getPresenter().startDatePicked();
    LocalDate endDate = LocalDate.of(2019, 12, 31);
    view.getEndDatePicker().setValue(endDate);
    view.getPresenter().endDatePicked();
    Assert.assertEquals(1, view.getPresenter().getLogs().size());
    Assert.assertEquals(2, view.getPresenter().getLogs().get(0).getLogpoints().size());
    view.refresh();

    GridPane timeLogGrid = view.getTimeLogGrid();
    Assert.assertNotNull("Expect the view to have a TimeLogGrid", timeLogGrid);
    Assert
        .assertFalse("Expect the timelogGrid to have content", timeLogGrid.getChildren().isEmpty());
    Assert.assertTrue("Expect the timelogGrid to contain at least the logs",
        timeLogGrid.getChildren().size() > view.getPresenter().getLogs().size()
            * 2); // One child for the log, one for the title label
  }

  private TimeLog createTimeLog(Date start, Date end) {
    TimeLog timeLog = new TimeLog();
    timeLog.setStartTime(start);
    timeLog.setEndTime(end);
    return timeLog;
  }

}
