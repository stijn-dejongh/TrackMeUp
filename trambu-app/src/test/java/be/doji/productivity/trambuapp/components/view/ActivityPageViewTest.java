package be.doji.productivity.trambuapp.components.view;

import be.doji.productivity.trambuapp.components.presenter.ActivityManagerContainer;
import be.doji.productivity.trambuapp.components.presenter.ActivityPagePresenter;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambucore.managers.ActivityManager;
import be.doji.productivity.trambucore.managers.TimeTrackingManager;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.testutil.FileUtils;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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

public class ActivityPageViewTest extends ApplicationTest {
    private static final Logger LOG = LoggerFactory.getLogger(ActivityPageViewTest.class);

    @Mock private ActivityPagePresenter mockPresenter;

    @Mock private ActivityManagerContainer mockActController;

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
        Mockito.when(mockPresenter.getActivityController()).thenReturn(mockActController);
        Mockito.when(mockActController.getActivityManager()).thenReturn(activityManager);
        Mockito.when(mockActController.getTimeTrackingManager()).thenReturn(timeTrackingManager);
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