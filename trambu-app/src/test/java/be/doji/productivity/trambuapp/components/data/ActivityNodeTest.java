package be.doji.productivity.trambuapp.components.data;

import be.doji.productivity.trambuapp.components.helper.AutocompleteTextField;
import be.doji.productivity.trambuapp.controllers.ActivityController;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.views.ActivityOverview;
import be.doji.productivity.trambucore.TrackMeConstants;
import be.doji.productivity.trambucore.managers.ActivityManager;
import be.doji.productivity.trambucore.managers.TimeTrackingManager;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.model.tracker.ActivityLog;
import be.doji.productivity.trambucore.testutil.FileUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
import java.util.UUID;

public class ActivityNodeTest extends ApplicationTest {
    private static final Logger LOG = LoggerFactory.getLogger(ActivityNodeTest.class);

    @Mock private ActivityOverview mockApplication;
    @Mock private ActivityController mockActController;

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
        Mockito.when(mockApplication.getActivityController()).thenReturn(mockActController);
        Mockito.when(mockActController.getActivityManager()).thenReturn(activityManager);
        Mockito.when(mockActController.getTimeTrackingManager()).thenReturn(timeTrackingManager);
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

    @Test public void testCreateTimerStartNoActiveTiming() {

        TimeTrackingManager mockTimeManager = Mockito.mock(TimeTrackingManager.class);
        Activity testActivity = new Activity("DefaultActivity");
        ActivityLog activityLog = new ActivityLog(testActivity);
        Mockito.when(mockTimeManager.getLogForActivityId(Mockito.any(UUID.class))).thenReturn(activityLog);

        Mockito.when(mockActController.getTimeTrackingManager()).thenReturn(mockTimeManager);

        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);
        HBox timingControls = testNode.createTimingControls();
        Assert.assertNotNull(timingControls.getChildren());
        Assert.assertEquals(1, timingControls.getChildren().size());
        Button timingControlButton = (Button) timingControls.getChildren().get(0);
        Assert.assertEquals(DisplayConstants.BUTTON_TEXT_TIMER_START, timingControlButton.getText());
        FontAwesomeIconView graphic = (FontAwesomeIconView) timingControlButton.getGraphic();
        Assert.assertEquals("HOURGLASS_START", graphic.getGlyphName());
    }

    @Test public void testCreateTimerStartWithActiveTiming() {
        TimeTrackingManager mockTimeManager = Mockito.mock(TimeTrackingManager.class);
        Activity testActivity = new Activity("DefaultActivity");
        ActivityLog activityLog = new ActivityLog(testActivity);
        activityLog.startLog();
        Mockito.when(mockTimeManager.getLogForActivityId(Mockito.any(UUID.class))).thenReturn(activityLog);

        Mockito.when(mockActController.getTimeTrackingManager()).thenReturn(mockTimeManager);

        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);
        HBox timingControls = testNode.createTimingControls();
        Assert.assertNotNull(timingControls.getChildren());
        Assert.assertEquals(1, timingControls.getChildren().size());
        Button timingControlButton = (Button) timingControls.getChildren().get(0);
        Assert.assertEquals(DisplayConstants.BUTTON_TEXT_TIMER_STOP, timingControlButton.getText());
        FontAwesomeIconView graphic = (FontAwesomeIconView) timingControlButton.getGraphic();
        Assert.assertEquals("HOURGLASS_END", graphic.getGlyphName());
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

    @Test public void testCreateDeadline() {
        Activity testActivity = new Activity("DefaultActivity");
        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);
        testActivity.setDeadline(new Date());
        Assert.assertFalse(testNode.isEditable());
        Assert.assertEquals(Label.class, testNode.createDeadline().getClass());
        testNode.makeEditable();
        Assert.assertTrue(testNode.isEditable());
        Assert.assertEquals(HBox.class, testNode.createDeadline().getClass());
    }

    @Test public void testCreateTags() {
        Activity testActivity = new Activity("DefaultActivity");
        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);
        Assert.assertFalse(testNode.isEditable());
        Node tags = testNode.createTags();
        Assert.assertEquals(HBox.class, tags.getClass());

        testNode.makeEditable();
        tags = testNode.createTags();
        Assert.assertEquals(AutocompleteTextField.class, tags.getClass());
    }

    @Test public void testCreateTagsEditableNoTags() {
        Activity testActivity = new Activity("DefaultActivity");
        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);
        testNode.makeEditable();
        Assert.assertTrue(testNode.isEditable());
        Node tags = testNode.createTags();
        Assert.assertEquals(AutocompleteTextField.class, tags.getClass());
        TextField castedTags = (TextField) tags;
        Assert.assertTrue(StringUtils.isBlank(castedTags.getText()));
    }

    @Test public void testCreateTagsEditableWithTags() {
        Activity testActivity = new Activity("DefaultActivity");
        testActivity.addTag("TagOne");
        testActivity.addTag("TagTwo");
        Assert.assertEquals(2, testActivity.getTags().size());
        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);
        testNode.makeEditable();
        Assert.assertTrue(testNode.isEditable());
        Node tags = testNode.createTags();
        Assert.assertEquals(AutocompleteTextField.class, tags.getClass());
        AutocompleteTextField castedTags = (AutocompleteTextField) tags;
        Assert.assertTrue(StringUtils.isNotBlank(castedTags.getText()));
        Assert.assertTrue(castedTags.getText().contains("TagOne"));
        Assert.assertTrue(castedTags.getText().contains("TagTwo"));
    }

    @Test public void testCreateTagsUneditableWithTags() {
        Activity testActivity = new Activity("DefaultActivity");
        testActivity.addTag("TagOne");
        testActivity.addTag("TagTwo");
        int initialTagSize = testActivity.getTags().size();
        Assert.assertEquals(2, initialTagSize);
        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);
        Assert.assertFalse(testNode.isEditable());
        Node tags = testNode.createTags();
        Assert.assertEquals(HBox.class, tags.getClass());
        HBox castedTags = (HBox) tags;
        ObservableList<Node> tagsChildren = castedTags.getChildren();
        Assert.assertNotNull(tagsChildren);
        Assert.assertFalse(tagsChildren.isEmpty());
        Assert.assertEquals(initialTagSize, tagsChildren.size());
    }

    @Test public void testCreateProjects() {
        Activity testActivity = new Activity("DefaultActivity");
        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);
        Assert.assertFalse(testNode.isEditable());
        Node projects = testNode.createProjects();
        Assert.assertEquals(HBox.class, projects.getClass());

        testNode.makeEditable();
        projects = testNode.createProjects();
        Assert.assertEquals(AutocompleteTextField.class, projects.getClass());
    }

    @Test public void testCreateProjectsEditableNoProjects() {
        Activity testActivity = new Activity("DefaultActivity");
        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);
        testNode.makeEditable();
        Assert.assertTrue(testNode.isEditable());
        Node projects = testNode.createProjects();
        Assert.assertEquals(AutocompleteTextField.class, projects.getClass());
        AutocompleteTextField castedProjects = (AutocompleteTextField) projects;
        Assert.assertTrue(StringUtils.isBlank(castedProjects.getText()));
    }

    @Test public void testCreateProjectsEditableWithProjects() {
        Activity testActivity = new Activity("DefaultActivity");
        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);
        testActivity.addProject("ProjectOne");
        testActivity.addProject("ProjectTwo");
        Assert.assertEquals(2, testActivity.getProjects().size());
        testNode.makeEditable();
        Assert.assertTrue(testNode.isEditable());
        Node projects = testNode.createProjects();
        Assert.assertEquals(AutocompleteTextField.class, projects.getClass());
        TextField castedProjects = (TextField) projects;
        Assert.assertTrue(StringUtils.isNotBlank(castedProjects.getText()));
        Assert.assertTrue(castedProjects.getText().contains("ProjectOne"));
        Assert.assertTrue(castedProjects.getText().contains("ProjectTwo"));
    }

    @Test public void testCreateProjectsUneditableWithProjects() {
        Activity testActivity = new Activity("DefaultActivity");
        testActivity.addProject("TagOne");
        testActivity.addProject("TagTwo");
        int initialProjectSize = testActivity.getProjects().size();
        Assert.assertEquals(2, initialProjectSize);
        ActivityNode testNode = new ActivityNode(testActivity, mockApplication);
        Assert.assertFalse(testNode.isEditable());
        Node tags = testNode.createProjects();
        Assert.assertEquals(HBox.class, tags.getClass());
        HBox castedProjects = (HBox) tags;
        ObservableList<Node> projectChildren = castedProjects.getChildren();
        Assert.assertNotNull(projectChildren);
        Assert.assertFalse(projectChildren.isEmpty());
        Assert.assertEquals(initialProjectSize, projectChildren.size());
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