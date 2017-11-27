package be.doji.productivity.trambucore.managers;

import be.doji.productivity.trambucore.TrambuTest;
import be.doji.productivity.trambucore.model.tracker.ActivityLog;
import be.doji.productivity.trambucore.model.tracker.TimeLog;
import be.doji.productivity.trambucore.testutil.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;

public class TimeTrackingManagerTest extends TrambuTest {

    private static final String FILE_TIME_LOGS_TEST = "data/testTimeLog.txt";

    /* Unit tests for basic functionality */

    @Test public void testReadLogs() throws IOException, ParseException {
        String testPath = FileUtils.getTestPath(FILE_TIME_LOGS_TEST, this.getClass().getClassLoader());
        TimeTrackingManager tm = new TimeTrackingManager(testPath);
        tm.readLogs();
        ActivityLog parsedActivityLog = tm.getLogForActivityId("fa183c05-fb22-4411-8f94-12c954484f22");
        Assert.assertNotNull(parsedActivityLog);
        Assert.assertEquals("fa183c05-fb22-4411-8f94-12c954484f22", parsedActivityLog.getActivityId().toString());
        List<TimeLog> parsedLogPoints = parsedActivityLog.getLogpoints();
        Assert.assertNotNull(parsedLogPoints);
        Assert.assertEquals(1, parsedLogPoints.size());
    }

    @Test public void testUpdateFileEmptyFile() throws IOException, ParseException {
        String testPath = FileUtils.getTestPath(FILE_TIME_LOGS_TEST, this.getClass().getClassLoader());
        TimeTrackingManager tm = new TimeTrackingManager(testPath);
        tm.readLogs();
        Assert.assertFalse(tm.getLogs().isEmpty());
        Assert.assertEquals(1, tm.getLogs().size());

        Path tempFile = createTempFile();
        tm.updateFileLocation(tempFile.toString());
        Assert.assertTrue(tm.getLogs().isEmpty());

        Files.delete(tempFile);
    }

    @Test public void testWriteLogs() throws IOException {
        Path tempFile = createTempFile();
        TimeTrackingManager tm = new TimeTrackingManager(tempFile.toString());
        Assert.assertTrue(tm.getLogs().isEmpty());
        tm.writeLogs();
        Assert.assertTrue(Files.readAllLines(tempFile).isEmpty());
        ActivityLog testLog = new ActivityLog(UUID.randomUUID());
        tm.save(testLog);
        tm.writeLogs();
        List<String> linesAfterWrite = Files.readAllLines(tempFile);
        Assert.assertFalse(linesAfterWrite.isEmpty());
        Assert.assertEquals(2, linesAfterWrite.size());

        Files.delete(tempFile);
    }

    @Test public void testStopAll() throws IOException {
        Path tempFile = createTempFile();
        TimeTrackingManager tm = new TimeTrackingManager(tempFile.toString());
        ActivityLog testLogActiveOne = new ActivityLog(UUID.randomUUID());
        testLogActiveOne.startLog();
        Assert.assertTrue(testLogActiveOne.getActiveLog().isPresent());
        tm.save(testLogActiveOne);

        ActivityLog testLogActiveTwo = new ActivityLog(UUID.randomUUID());
        testLogActiveTwo.startLog();
        Assert.assertTrue(testLogActiveTwo.getActiveLog().isPresent());
        tm.save(testLogActiveTwo);

        List<ActivityLog> allLogsBeforeStop = tm.getLogs();
        Assert.assertFalse(allLogsBeforeStop.isEmpty());
        Assert.assertEquals(2, allLogsBeforeStop.size());
        for (ActivityLog log : allLogsBeforeStop) {
            Assert.assertTrue(log.getActiveLog().isPresent());
        }

        tm.stopAll();
        List<ActivityLog> allLogsAfterStop = tm.getLogs();
        Assert.assertFalse(allLogsAfterStop.isEmpty());
        Assert.assertEquals(2, allLogsAfterStop.size());
        for (ActivityLog log : allLogsAfterStop) {
            Assert.assertFalse(log.getActiveLog().isPresent());
        }

        Files.delete(tempFile);
    }

    /* Unit tests for overview functionality */

    @Test public void getActivityForIntervalOneFits() throws IOException {
        Path tempFile = createTempFile();
        TimeTrackingManager tm = new TimeTrackingManager(tempFile.toString());

        UUID activityOneId = UUID.randomUUID();
        ActivityLog logActivityOne = new ActivityLog(activityOneId);
        Calendar logOneStart = new GregorianCalendar(2017, Calendar.DECEMBER, 1, 14, 0, 0);
        Calendar logOneEnd = new GregorianCalendar(2017, Calendar.DECEMBER, 1, 18, 0, 0);
        logActivityOne.addLogPoint(createTimeLog(logOneStart.getTime(), logOneEnd.getTime()));

        UUID activityTwoId = UUID.randomUUID();
        ActivityLog logActivityTwo = new ActivityLog(activityTwoId);
        Calendar logTwoStart = new GregorianCalendar(2017, Calendar.DECEMBER, 4, 14, 0, 0);
        Calendar logTwoEnd = new GregorianCalendar(2017, Calendar.DECEMBER, 4, 19, 0, 0);
        logActivityTwo.addLogPoint(createTimeLog(logTwoStart.getTime(), logTwoEnd.getTime()));

        tm.save(logActivityOne);
        tm.save(logActivityTwo);

        Calendar overviewStartDate = new GregorianCalendar(2017, Calendar.DECEMBER, 1);
        Calendar overviewEndDate = new GregorianCalendar(2017, Calendar.DECEMBER, 2);
        List<ActivityLog> overviewLogs = tm
                .getActivityLogsInInterval(overviewStartDate.getTime(), overviewEndDate.getTime());

        Assert.assertNotNull(overviewLogs);
        Assert.assertEquals(1, overviewLogs.size());
        Assert.assertEquals(activityOneId, overviewLogs.get(0).getActivityId());

        Files.delete(tempFile);
    }

    @Test public void getActivityForIntervalAllFit() throws IOException {
        Path tempFile = createTempFile();
        TimeTrackingManager tm = new TimeTrackingManager(tempFile.toString());

        UUID activityOneId = UUID.randomUUID();
        ActivityLog logActivityOne = new ActivityLog(activityOneId);
        Calendar logOneStart = new GregorianCalendar(2017, Calendar.DECEMBER, 1, 14, 0, 0);
        Calendar logOneEnd = new GregorianCalendar(2017, Calendar.DECEMBER, 1, 18, 0, 0);
        logActivityOne.addLogPoint(createTimeLog(logOneStart.getTime(), logOneEnd.getTime()));

        UUID activityTwoId = UUID.randomUUID();
        ActivityLog logActivityTwo = new ActivityLog(activityTwoId);
        Calendar logTwoStart = new GregorianCalendar(2017, Calendar.DECEMBER, 4, 14, 0, 0);
        Calendar logTwoEnd = new GregorianCalendar(2017, Calendar.DECEMBER, 4, 19, 0, 0);
        logActivityTwo.addLogPoint(createTimeLog(logTwoStart.getTime(), logTwoEnd.getTime()));

        tm.save(logActivityOne);
        tm.save(logActivityTwo);

        Calendar overviewStartDate = new GregorianCalendar(2017, Calendar.DECEMBER, 1);
        Calendar overviewEndDate = new GregorianCalendar(2017, Calendar.DECEMBER, 7);
        List<ActivityLog> overviewLogs = tm
                .getActivityLogsInInterval(overviewStartDate.getTime(), overviewEndDate.getTime());

        Assert.assertNotNull(overviewLogs);
        Assert.assertEquals(2, overviewLogs.size());
        Assert.assertEquals(activityOneId, overviewLogs.get(0).getActivityId());
        Assert.assertEquals(activityTwoId, overviewLogs.get(1).getActivityId());

        Files.delete(tempFile);
    }

    @Test public void getActivityForIntervalNoneFit() throws IOException {
        Path tempFile = createTempFile();
        TimeTrackingManager tm = new TimeTrackingManager(tempFile.toString());

        UUID activityOneId = UUID.randomUUID();
        ActivityLog logActivityOne = new ActivityLog(activityOneId);
        Calendar logOneStart = new GregorianCalendar(2017, Calendar.DECEMBER, 1, 14, 0, 0);
        Calendar logOneEnd = new GregorianCalendar(2017, Calendar.DECEMBER, 1, 18, 0, 0);
        logActivityOne.addLogPoint(createTimeLog(logOneStart.getTime(), logOneEnd.getTime()));

        UUID activityTwoId = UUID.randomUUID();
        ActivityLog logActivityTwo = new ActivityLog(activityTwoId);
        Calendar logTwoStart = new GregorianCalendar(2017, Calendar.DECEMBER, 4, 14, 0, 0);
        Calendar logTwoEnd = new GregorianCalendar(2017, Calendar.DECEMBER, 4, 19, 0, 0);
        logActivityTwo.addLogPoint(createTimeLog(logTwoStart.getTime(), logTwoEnd.getTime()));

        tm.save(logActivityOne);
        tm.save(logActivityTwo);

        Calendar overviewStartDate = new GregorianCalendar(2017, Calendar.DECEMBER, 20);
        Calendar overviewEndDate = new GregorianCalendar(2017, Calendar.DECEMBER, 30);
        List<ActivityLog> overviewLogs = tm
                .getActivityLogsInInterval(overviewStartDate.getTime(), overviewEndDate.getTime());

        Assert.assertNotNull(overviewLogs);
        Assert.assertTrue(overviewLogs.isEmpty());

        Files.delete(tempFile);
    }

    @Test public void getActivityForIntervalActiveItemIssueCurrentDate() throws IOException {
        Path tempFile = createTempFile();
        TimeTrackingManager tm = new TimeTrackingManager(tempFile.toString());

        Calendar today = new GregorianCalendar();


        UUID activityOneId = UUID.randomUUID();
        ActivityLog logActivityOne = new ActivityLog(activityOneId);
        Calendar logOneStart = new GregorianCalendar(1999, Calendar.DECEMBER, 1, 14, 0, 0);
        Calendar logOneEnd = new GregorianCalendar(1999, Calendar.DECEMBER, 1, 18, 0, 0);
        logActivityOne.addLogPoint(createTimeLog(logOneStart.getTime(), logOneEnd.getTime()));

        UUID activityTwoId = UUID.randomUUID();
        ActivityLog logActivityTwo = new ActivityLog(activityTwoId);
        Calendar logTwoStart = new GregorianCalendar(1999, Calendar.DECEMBER, 4, 14, 0, 0);
        TimeLog timelogTwo = new TimeLog();
        timelogTwo.setActive(true);
        timelogTwo.setStartTime(logTwoStart.getTime());
        logActivityTwo.addLogPoint(timelogTwo);

        tm.save(logActivityOne);
        tm.save(logActivityTwo);

        Calendar overviewStart = new GregorianCalendar(1900, Calendar.OCTOBER, 1);
        Calendar overViewEnd = new GregorianCalendar();
        overViewEnd.setTime(today.getTime());
        overViewEnd.add(Calendar.HOUR, 100);

        List<ActivityLog> activityLogsInInterval = tm
                .getActivityLogsInInterval(overviewStart.getTime(), overViewEnd.getTime());
        Assert.assertNotNull(activityLogsInInterval);
        Assert.assertEquals(2, activityLogsInInterval.size());
        ActivityLog savedActivityLogTwo = activityLogsInInterval.get(1);
        Assert.assertFalse(savedActivityLogTwo.getActiveLog().isPresent());
        List<TimeLog> savedLogPoints = savedActivityLogTwo.getLogpoints();
        Assert.assertNotNull(savedLogPoints);
        Assert.assertEquals(1, savedLogPoints.size());
        TimeLog savedLogPoint = savedLogPoints.get(0);
        Assert.assertNotNull(savedLogPoint);
        Date retrievedEndTime = savedLogPoint.getEndTime();
        Assert.assertNotNull(retrievedEndTime);
        Calendar compareObject = new GregorianCalendar();
        compareObject.setTime(retrievedEndTime);

        Assert.assertEquals(today.get(Calendar.YEAR),compareObject.get(Calendar.YEAR));
        Assert.assertEquals(today.get(Calendar.MONTH),compareObject.get(Calendar.MONTH));
        Assert.assertEquals(today.get(Calendar.DAY_OF_MONTH),compareObject.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(today.get(Calendar.HOUR),compareObject.get(Calendar.HOUR));


        Files.delete(tempFile);
    }

}
