package be.doji.productivity.trambucore.model.tracker;

import be.doji.productivity.trambucore.model.tasks.Activity;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class ActivityLogTest {

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
        String timeString = testLog.getTimeSpent();
        Assert.assertEquals("4.0 hours", timeString);
    }

    private Date createDateWithPositiveHourOffset(GregorianCalendar referenceDate, int offset) {
        return new GregorianCalendar(referenceDate.get(Calendar.YEAR), referenceDate.get(Calendar.MONTH),
                referenceDate.get(Calendar.DAY_OF_MONTH), referenceDate.get(Calendar.HOUR_OF_DAY) + offset,
                referenceDate.get(Calendar.MINUTE)).getTime();
    }

}
