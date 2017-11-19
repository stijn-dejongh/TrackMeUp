package be.doji.productivity.trambuapp.components;

import be.doji.productivity.trambuapp.presentation.TrambuApplication;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambucore.TrackMeConstants;
import be.doji.productivity.trambucore.managers.ActivityManager;
import be.doji.productivity.trambucore.managers.TimeTrackingManager;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.testutil.FileUtils;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Date;
import java.util.List;

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

    @Test public void testCreateControlsUneditable() {
        Activity testActivity = new Activity("DefaultActivity");
        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);
        Assert.assertFalse(testNode.isEditable());
        GridPane createdContent = testNode.createActivityContent();
        ObservableList<Node> contentNodes = createdContent.getChildren();
        Assert.assertNotNull(contentNodes);
        Assert.assertFalse(contentNodes.isEmpty());
    }

    @Test public void testCreateActivityControlsDefaults() {
        Activity testActivity = new Activity("DefaultActivity");
        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);
        GridPane actvityControls = testNode.createActvityControls();
        ObservableList<Node> controls = actvityControls.getChildren();
        Assert.assertNotNull(controls);
        Assert.assertEquals(3, controls.size());

        Button shouldBeDoneButton = (Button) controls.get(0);
        Assert.assertEquals(DisplayConstants.BUTTON_TEXT_IS_DONE, shouldBeDoneButton.getText());

        Button shouldBeEditButton = (Button) controls.get(1);
        Assert.assertEquals(DisplayConstants.BUTTON_TEXT_EDIT, shouldBeEditButton.getText());

        Button shouldBeDeleteButton = (Button) controls.get(2);
        Assert.assertEquals(DisplayConstants.BUTTON_TEXT_DELETE, shouldBeDeleteButton.getText());
    }

    @Test public void testCreateActivityControlsActivityNodeEditable() {
        Activity testActivity = new Activity("DefaultActivity");
        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);
        testNode.makeEditable();
        Assert.assertTrue(testNode.isEditable());
        GridPane actvityControls = testNode.createActvityControls();
        ObservableList<Node> controls = actvityControls.getChildren();
        Assert.assertNotNull(controls);
        Assert.assertEquals(3, controls.size());

        Button shouldBeDoneButton = (Button) controls.get(0);
        Assert.assertEquals(DisplayConstants.BUTTON_TEXT_IS_DONE, shouldBeDoneButton.getText());

        Button shouldBeEditButton = (Button) controls.get(1);
        Assert.assertEquals(DisplayConstants.BUTTON_TEXT_SAVE, shouldBeEditButton.getText());

        Button shouldBeDeleteButton = (Button) controls.get(2);
        Assert.assertEquals(DisplayConstants.BUTTON_TEXT_DELETE, shouldBeDeleteButton.getText());
    }

    @Test public void testCreateActivityControlsActivityIsDone() {
        Activity testActivity = new Activity("DefaultActivity");
        testActivity.setCompleted(true);
        Assert.assertTrue(testActivity.isCompleted());
        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);
        GridPane actvityControls = testNode.createActvityControls();
        ObservableList<Node> controls = actvityControls.getChildren();
        Assert.assertNotNull(controls);
        Assert.assertEquals(3, controls.size());

        Button shouldBeDoneButton = (Button) controls.get(0);
        Assert.assertEquals(DisplayConstants.BUTTON_TEXT_IS_NOT_DONE, shouldBeDoneButton.getText());

        Button shouldBeEditButton = (Button) controls.get(1);
        Assert.assertEquals(DisplayConstants.BUTTON_TEXT_EDIT, shouldBeEditButton.getText());

        Button shouldBeDeleteButton = (Button) controls.get(2);
        Assert.assertEquals(DisplayConstants.BUTTON_TEXT_DELETE, shouldBeDeleteButton.getText());
    }

    @Test public void testCreatePriority() {
        Activity testActivity = new Activity("DefaultActivity");
        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);

        Assert.assertFalse(testNode.isEditable());
        Assert.assertEquals(Label.class, testNode.createPriority().getClass());
        testNode.makeEditable();
        Assert.assertTrue(testNode.isEditable());
        Assert.assertEquals(ComboBox.class, testNode.createPriority().getClass());
    }

    @Test public void testCreateEditablePriority() {
        Activity testActivity = new Activity("DefaultActivity");
        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);

        Node editableNode = testNode.createEditablePriority();
        Assert.assertEquals(ComboBox.class, editableNode.getClass());
        ComboBox editablePriority = (ComboBox) editableNode;
        ObservableList<String> options = editablePriority.getItems();
        List<String> constantPriorities = TrackMeConstants.getPriorityList();
        Assert.assertFalse(constantPriorities.isEmpty());
        Assert.assertEquals(constantPriorities.size(), options.size());
        for (String option : constantPriorities) {
            Assert.assertTrue(StringUtils.equals(option, options.get(constantPriorities.indexOf(option))));
        }
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