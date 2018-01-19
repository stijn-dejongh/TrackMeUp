package be.doji.productivity.trambucore.exporters;

import be.doji.productivity.trambucore.TrambuTest;
import be.doji.productivity.trambucore.managers.ActivityManager;
import be.doji.productivity.trambucore.managers.TimeTrackingManager;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.model.tracker.ActivityLog;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class TimesheetToCSVExporterTest extends TrambuTest {

    private static final String ACTIVITY_NAME = "TestAct";
    @Mock ActivityManager activityManagerMock;

    @Before public void init() {
        MockitoAnnotations.initMocks(this);
        Optional<Activity> activity = Optional.of(new Activity(ACTIVITY_NAME));
        Mockito.when(activityManagerMock.getSavedActivityById(Mockito.anyString())).thenReturn(activity);
    }

    @Test public void testExportFull() throws IOException {
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

        List<ActivityLog> allLogsBeforeStop = tm.getLogs();

        TimesheetToCSVExporter exporter = new TimesheetToCSVExporter(activityManagerMock);
        List<String> fileLines = exporter.createFileLines(allLogsBeforeStop);
        Assert.assertNotNull(fileLines);
        Assert.assertFalse(fileLines.isEmpty());
        Assert.assertEquals(3, fileLines.size());
        String csvOfFirstLog = fileLines.get(1);
        Assert.assertEquals("TestAct,\"4.0\",\"240.0\",\"14000.0\",TestAct", csvOfFirstLog);

    }
}
