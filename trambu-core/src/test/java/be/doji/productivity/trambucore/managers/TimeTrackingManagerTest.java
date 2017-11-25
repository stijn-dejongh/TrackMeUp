package be.doji.productivity.trambucore.managers;

import be.doji.productivity.trambucore.model.tracker.ActivityLog;
import be.doji.productivity.trambucore.model.tracker.TimeLog;
import be.doji.productivity.trambucore.testutil.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class TimeTrackingManagerTest {

    private static final String FILE_TIME_LOGS_TEST = "data/testTimeLog.txt";

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

}
