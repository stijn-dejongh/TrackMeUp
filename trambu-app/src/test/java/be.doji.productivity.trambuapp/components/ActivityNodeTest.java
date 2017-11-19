package be.doji.productivity.trambuapp.components;

import be.doji.productivity.trambuapp.presentation.TrambuApplication;
import be.doji.productivity.trambucore.managers.ActivityManager;
import be.doji.productivity.trambucore.managers.TimeTrackingManager;
import be.doji.productivity.trambucore.testutil.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ActivityNodeTest {

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



    @After public void cleanUp() throws IOException {
        Files.delete(activityTestFile);
        Files.delete(timeTrackingTestFile);
    }

    private Path createTempFile() throws IOException {
        Path directoryPath = Paths.get(FileUtils.getTestPath("testDirectory/maynotbeempty.txt")).getParent();
        return Files.createTempFile(directoryPath, "temp", "txt");
    }

}