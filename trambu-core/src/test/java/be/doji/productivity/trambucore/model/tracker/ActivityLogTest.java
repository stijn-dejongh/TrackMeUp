package be.doji.productivity.trambucore.model.tracker;

import be.doji.productivity.trambucore.TrambuTest;
import be.doji.productivity.trambucore.model.tasks.Activity;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class ActivityLogTest extends TrambuTest {

    @Test public void testStart() {
        Activity activity = new Activity("TestBug");
        ActivityLog testLog = new ActivityLog(activity);
        Assert.assertTrue(testLog.getLogpoints().isEmpty());
        testLog.startLog();
        Assert.assertFalse(testLog.getLogpoints().isEmpty());
        Assert.assertEquals(1, testLog.getLogpoints().size());
        Assert.assertTrue(testLog.getLogpoints().get(0).isActive());
    }

    @Test public void testStartWithActiveLog() {
        Activity activity = new Activity("TestBug");
        ActivityLog testLog = new ActivityLog(activity);
        Assert.assertTrue(testLog.getLogpoints().isEmpty());
        testLog.startLog();
        Assert.assertFalse(testLog.getLogpoints().isEmpty());
        Assert.assertEquals(1, testLog.getLogpoints().size());
        Assert.assertTrue(testLog.getLogpoints().get(0).isActive());
        testLog.startLog();
        Assert.assertEquals(2, testLog.getLogpoints().size());
        TimeLog originalActiveLog = testLog.getLogpoints().get(0);
        Assert.assertFalse(originalActiveLog.isActive());
        Assert.assertNotNull(originalActiveLog.getEndTime());
        Assert.assertTrue(testLog.getLogpoints().get(1).isActive());
    }

    @Test public void testStopAfterStart() {
        Activity activity = new Activity("TestBug");
        ActivityLog testLog = new ActivityLog(activity);
        Assert.assertTrue(testLog.getLogpoints().isEmpty());
        testLog.startLog();
        Assert.assertFalse(testLog.getLogpoints().isEmpty());
        testLog.stopActiveLog();
        Assert.assertEquals(1, testLog.getLogpoints().size());
        Assert.assertFalse(testLog.getLogpoints().get(0).isActive());
    }

    @Test public void testStopNoActiveLogs() {
        Activity activity = new Activity("TestBug");
        ActivityLog testLog = new ActivityLog(activity);
        Assert.assertTrue(testLog.getLogpoints().isEmpty());
        testLog.stopActiveLog();
        Assert.assertTrue(testLog.getLogpoints().isEmpty());
    }

    @Test public void testGetActiveLog() {
        Activity activity = new Activity("TestBug");
        ActivityLog testLog = new ActivityLog(activity);
        Assert.assertTrue(testLog.getLogpoints().isEmpty());
        testLog.startLog();
        Assert.assertFalse(testLog.getLogpoints().isEmpty());
        Assert.assertEquals(1, testLog.getLogpoints().size());
        TimeLog activeLog = testLog.getLogpoints().get(0);
        Assert.assertTrue(activeLog.isActive());
        Optional<TimeLog> activeLogFromGetter = testLog.getActiveLog();
        Assert.assertTrue(activeLogFromGetter.isPresent());
        Assert.assertEquals(activeLog, activeLogFromGetter.get());
    }

    @Test public void testGetActiveLogNoActiveLog() {
        Activity activity = new Activity("TestBug");
        ActivityLog testLog = new ActivityLog(activity);
        Assert.assertTrue(testLog.getLogpoints().isEmpty());
        Optional<TimeLog> activeLogFromGetter = testLog.getActiveLog();
        Assert.assertFalse(activeLogFromGetter.isPresent());
    }

    @Test public void testGetTimeSpent() {
        GregorianCalendar referenceDate = new GregorianCalendar();
        List<TimeLog> logPoints = new ArrayList<>();
        TimeLog logPointOne = new TimeLog();
        logPointOne.setStartTime(createDateWithPositiveHourOffset(referenceDate, 0));
        logPointOne.setEndTime(createDateWithPositiveHourOffset(referenceDate, 2));
        logPointOne.setActive(false);
        logPoints.add(logPointOne);

        TimeLog logPointTwo = new TimeLog();
        logPointTwo.setStartTime(createDateWithPositiveHourOffset(referenceDate, 6));
        logPointTwo.setEndTime(createDateWithPositiveHourOffset(referenceDate, 8));
        logPointTwo.setActive(false);
        logPoints.add(logPointTwo);

        ActivityLog testLog = new ActivityLog(new Activity("TestBug"));
        testLog.setLogpoints(logPoints);
        String timeString = testLog.getTimeSpentInHoursString();
        Assert.assertEquals("4.0 hours", timeString);
    }

    @Test public void testGetTimeSpentVerySmallTimeFrame() {
        GregorianCalendar referenceDate = new GregorianCalendar();
        List<TimeLog> logPoints = new ArrayList<>();
        TimeLog logPointOne = new TimeLog();
        logPointOne.setStartTime(referenceDate.getTime());
        GregorianCalendar endDate = new GregorianCalendar();
        endDate.setTime(DateUtils.addMinutes(referenceDate.getTime(), 1));
        logPointOne.setEndTime(endDate.getTime());
        logPointOne.setActive(false);
        logPoints.add(logPointOne);

        ActivityLog testLog = new ActivityLog(new Activity("TestBug"));
        testLog.setLogpoints(logPoints);
        String timeString = testLog.getTimeSpentInHoursString();
        Assert.assertEquals("0.017 hours", timeString);
    }

    /* Unit tests for overview functionality */

    @Test public void testGetLogsForIntervalFullyInScope() {
        UUID activityOneId = UUID.randomUUID();
        ActivityLog logActivityOne = new ActivityLog(activityOneId);
        Calendar logOneStart = new GregorianCalendar(2017, Calendar.DECEMBER, 1, 14, 0, 0);
        Calendar logOneEnd = new GregorianCalendar(2017, Calendar.DECEMBER, 1, 18, 0, 0);
        logActivityOne.addLogPoint(createTimeLog(logOneStart.getTime(), logOneEnd.getTime()));

        Calendar logTwoStart = new GregorianCalendar(2017, Calendar.DECEMBER, 4, 14, 0, 0);
        Calendar logTwoEnd = new GregorianCalendar(2017, Calendar.DECEMBER, 4, 19, 0, 0);
        logActivityOne.addLogPoint(createTimeLog(logTwoStart.getTime(), logTwoEnd.getTime()));

        Calendar overviewStartDate = new GregorianCalendar(2017, Calendar.DECEMBER, 1);
        Calendar overviewEndDate = new GregorianCalendar(2017, Calendar.DECEMBER, 2);

        List<TimeLog> timeLogsInInterval = logActivityOne
                .getTimeLogsInInterval(overviewStartDate.getTime(), overviewEndDate.getTime());
        Assert.assertNotNull(timeLogsInInterval);
        Assert.assertEquals(1, timeLogsInInterval.size());
        TimeLog timeLog = timeLogsInInterval.get(0);
        Assert.assertEquals(timeLog.getStartTime(), logOneStart.getTime());
        Assert.assertEquals(timeLog.getEndTime(), logOneEnd.getTime());
    }

    @Test public void testGetLogsForIntervalPartiallyInScopeEndDate() {
        UUID activityOneId = UUID.randomUUID();
        ActivityLog logActivityOne = new ActivityLog(activityOneId);
        Calendar logOneStart = new GregorianCalendar(1987, Calendar.DECEMBER, 1, 14, 0, 0);
        Calendar logOneEnd = new GregorianCalendar(1987, Calendar.DECEMBER, 1, 18, 0, 0);
        logActivityOne.addLogPoint(createTimeLog(logOneStart.getTime(), logOneEnd.getTime()));

        Calendar overviewStartDate = new GregorianCalendar(1987, Calendar.DECEMBER, 1);
        Calendar overviewEndDate = new GregorianCalendar(1987, Calendar.DECEMBER, 1, 17, 30, 00);

        List<TimeLog> timeLogsInInterval = logActivityOne
                .getTimeLogsInInterval(overviewStartDate.getTime(), overviewEndDate.getTime());
        Assert.assertNotNull(timeLogsInInterval);
        Assert.assertEquals(1, timeLogsInInterval.size());
        TimeLog timeLog = timeLogsInInterval.get(0);
        Assert.assertEquals(logOneStart.getTime(), timeLog.getStartTime());
        Assert.assertEquals(overviewEndDate.getTime(), timeLog.getEndTime());
    }

    @Test public void testGetLogsForIntervalPartiallyInScopeStartDate() {
        UUID activityOneId = UUID.randomUUID();
        ActivityLog logActivityOne = new ActivityLog(activityOneId);
        Calendar logOneStart = new GregorianCalendar(2017, Calendar.DECEMBER, 1, 14, 0, 0);
        Calendar logOneEnd = new GregorianCalendar(2017, Calendar.DECEMBER, 1, 18, 0, 0);
        logActivityOne.addLogPoint(createTimeLog(logOneStart.getTime(), logOneEnd.getTime()));

        Calendar overviewStartDate = new GregorianCalendar(2017, Calendar.DECEMBER, 1, 16, 0, 0);
        Calendar overviewEndDate = new GregorianCalendar(2017, Calendar.DECEMBER, 2, 17, 30, 00);

        List<TimeLog> timeLogsInInterval = logActivityOne
                .getTimeLogsInInterval(overviewStartDate.getTime(), overviewEndDate.getTime());
        Assert.assertNotNull(timeLogsInInterval);
        Assert.assertEquals(1, timeLogsInInterval.size());
        TimeLog timeLog = timeLogsInInterval.get(0);
        Assert.assertEquals(overviewStartDate.getTime(), timeLog.getStartTime());
        Assert.assertEquals(logOneEnd.getTime(), timeLog.getEndTime());
    }

    @Test public void testGetLogsForIntervalNoneInScope() {
        UUID activityOneId = UUID.randomUUID();
        ActivityLog logActivityOne = new ActivityLog(activityOneId);
        Calendar logOneStart = new GregorianCalendar(2017, Calendar.DECEMBER, 1, 14, 0, 0);
        Calendar logOneEnd = new GregorianCalendar(2017, Calendar.DECEMBER, 1, 18, 0, 0);
        logActivityOne.addLogPoint(createTimeLog(logOneStart.getTime(), logOneEnd.getTime()));

        Calendar logTwoStart = new GregorianCalendar(2017, Calendar.DECEMBER, 4, 14, 0, 0);
        Calendar logTwoEnd = new GregorianCalendar(2017, Calendar.DECEMBER, 4, 19, 0, 0);
        logActivityOne.addLogPoint(createTimeLog(logTwoStart.getTime(), logTwoEnd.getTime()));

        Calendar overviewStartDate = new GregorianCalendar(2017, Calendar.DECEMBER, 20);
        Calendar overviewEndDate = new GregorianCalendar(2017, Calendar.DECEMBER, 30);

        List<TimeLog> timeLogsInInterval = logActivityOne
                .getTimeLogsInInterval(overviewStartDate.getTime(), overviewEndDate.getTime());
        Assert.assertNotNull(timeLogsInInterval);
        Assert.assertTrue(timeLogsInInterval.isEmpty());
    }

    private Date createDateWithPositiveHourOffset(GregorianCalendar referenceDate, int offset) {
        return new GregorianCalendar(referenceDate.get(Calendar.YEAR), referenceDate.get(Calendar.MONTH),
                referenceDate.get(Calendar.DAY_OF_MONTH), referenceDate.get(Calendar.HOUR_OF_DAY) + offset,
                referenceDate.get(Calendar.MINUTE)).getTime();
    }

}
