package be.doji.productivity.trambuapp.components;

import be.doji.productivity.trambuapp.presentation.TrambuApplication;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambucore.managers.ActivityManager;
import be.doji.productivity.trambucore.managers.TimeTrackingManager;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.testutil.FileUtils;
import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ActivityNodeTest extends ApplicationTest {
    private static final Logger LOG = LoggerFactory.getLogger(ActivityNodeTest.class);

    @Mock private TrambuApplication mockApplication;
    private ActivityManager activityManager;
    private TimeTrackingManager timeTrackingManager;

    private Path activityTestFile;
    private Path timeTrackingTestFile;

    @Before public void setUp() throws Exception {
        activityTestFile = createTempFile();
        timeTrackingTestFile = createTempFile();
        this.activityManager = new ActivityManager(activityTestFile.toString());
        this.timeTrackingManager = new TimeTrackingManager(timeTrackingTestFile.toString());
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockApplication.getActivityManager()).thenReturn(activityManager);
        Mockito.when(mockApplication.getTimeTrackingManager()).thenReturn(timeTrackingManager);
    }

    @Test public void testGetActivityStyleTodo() {
        Activity testActivity = new Activity("DefaultActivity");
        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);
        Assert.assertEquals(DisplayConstants.STYLE_CLASS_ACTIVITY_TODO, testNode.getActivityStyle());
    }

    @Test public void testGetActivityStyleDone() {
        Activity testActivity = new Activity("DefaultActivity");
        testActivity.setCompleted(true);
        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);
        Assert.assertEquals(DisplayConstants.STYLE_CLASS_ACTIVITY_DONE, testNode.getActivityStyle());
    }

    @Test public void testGetActivityStyleAlert() {
        Activity testActivity = new Activity("DefaultActivity");
        Date referenceDate = new Date();
        Date passedDeadline = new Date(referenceDate.getTime() - 5000);
        testActivity.setDeadline(passedDeadline);
        Assert.assertTrue(testActivity.isAlertActive());
        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);
        Assert.assertEquals(DisplayConstants.STYLE_CLASS_ACTIVITY_ALERT, testNode.getActivityStyle());
    }

    @After public void cleanUp() throws IOException {
        if (Files.exists(activityTestFile)) {
            Files.delete(activityTestFile);
        }
        if (Files.exists(timeTrackingTestFile)) {
            Files.delete(timeTrackingTestFile);
        }
    }

    private Path createTempFile() throws IOException {
        Path directoryPath = Paths
                .get(FileUtils.getTestPath("tempDirectory/maynotbeempty.txt", this.getClass().getClassLoader()))
                .getParent();
        return Files.createTempFile(directoryPath, "temp", "txt");
    }


}