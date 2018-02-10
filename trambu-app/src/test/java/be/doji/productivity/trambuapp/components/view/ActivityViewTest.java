package be.doji.productivity.trambuapp.components.view;

import be.doji.productivity.trambuapp.components.TrambuAppTest;
import be.doji.productivity.trambuapp.components.elements.OverlayPane;
import be.doji.productivity.trambuapp.components.elements.Switchable;
import be.doji.productivity.trambuapp.components.presenter.ActivityPagePresenter;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambucore.managers.NoteManager;
import be.doji.productivity.trambucore.managers.TimeTrackingManager;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.model.tasks.Note;
import be.doji.productivity.trambucore.model.tracker.ActivityLog;
import be.doji.productivity.trambucore.testutil.ActivityTestData;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Tests for the ActivityView MVP model. Testing both ActivityView and ActvitiyPresenter classes
 */
public class ActivityViewTest extends TrambuAppTest {

  private static final String ACTIVITY_ONE_ID = "283b6271-b513-4e89-b757-10e98c9078ea";
  private static final String SUPER_ACTIVITY_ID = "283b6271-b513-4e89-b757-10e98c9078ea";

  private static final String DEFAULT_WARNING_TIME_STRING = "PT24H";
  private static final String CHANGED_PROJECT_TEXT = "NewProject";
  private static final String TEST_LOCATION = "TestLocation";
  private static final String ACTIVITY_WITH_LOCATION_ID = "283b6298-b513-4e89-b757-10e98c9078ea";
  private static final String TEST_NOTE_STRING = "This is a note. Please respect my authority";

  @Mock
  private ActivityPagePresenter mockPagePresenter;

  @Test
  public void testFieldCreation() throws ParseException {
    getActivityManager().addActivity(ActivityTestData.ACTIVITY_DATA_LINE);
    Optional<Activity> activity = getActivityManager().getSavedActivityById(ACTIVITY_ONE_ID);
    Assert.assertTrue(activity.isPresent());

    ActivityView view = new ActivityView(activity.get());
    Assert.assertNotNull(view);
    Assert.assertNotNull(view.getNameField());
    Assert.assertNotNull(view.getPriorityField());
    Assert.assertNotNull(view.getLocationField());
    Assert.assertNotNull(view.getWarningPeriodField());
    Assert.assertNotNull(view.getTagsField());
    Assert.assertNotNull(view.getProjectsField());
    Assert.assertNotNull(view.getDeadlineField());
  }

  @Test
  public void testFieldPopulation() throws ParseException {
    getActivityManager().addActivity(ActivityTestData.ACTIVITY_DATA_LINE);
    Optional<Activity> activity = getActivityManager().getSavedActivityById(ACTIVITY_ONE_ID);
    Assert.assertTrue(activity.isPresent());

    ActivityView view = new ActivityView(activity.get());
    Assert.assertNotNull(view);

    Assert.assertTrue(view.getNameField().hasData());
    Assert.assertEquals("TaskTitle", view.getNameField().getData());

    Assert.assertTrue(view.getPriorityField().hasData());
    Assert.assertEquals("A", view.getPriorityField().getData());

    Assert.assertTrue(view.getWarningPeriodField().hasData());
    Assert.assertEquals(DEFAULT_WARNING_TIME_STRING, view.getWarningPeriodField().getData());

    Assert.assertTrue(view.getTagsField().hasData());
    List<String> tagData = view.getTagsField().getData();
    Assert.assertNotNull(tagData);
    Assert.assertFalse(tagData.isEmpty());
    Assert.assertEquals(2, tagData.size());
    Assert.assertEquals("Tag", tagData.get(0));
    Assert.assertEquals("Tag2", tagData.get(1));

    Assert.assertTrue(view.getProjectsField().hasData());
    List<String> projectData = view.getProjectsField().getData();
    Assert.assertNotNull(projectData);
    Assert.assertFalse(projectData.isEmpty());
    Assert.assertEquals(1, projectData.size());
    Assert.assertEquals("OverarchingProject", projectData.get(0));

    Assert.assertTrue(view.getDeadlineField().hasData());
    Date deadLine = view.getDeadlineField().getData();
    Calendar deadlineWrapper = new GregorianCalendar();
    deadlineWrapper.setTime(deadLine);
    Assert.assertEquals(2017, deadlineWrapper.get(Calendar.YEAR));
    Assert.assertEquals(Calendar.DECEMBER, deadlineWrapper.get(Calendar.MONTH));
    Assert.assertEquals(21, deadlineWrapper.get(Calendar.DAY_OF_MONTH));
  }

  @Test
  public void testCreateSubactivities() throws ParseException {
    getActivityManager().addActivity(ActivityTestData.SUPER_ACTIVITY);
    getActivityManager().addActivity(ActivityTestData.SUB_ACTIVITY_WIITH_PROJECTS_ONE);
    getActivityManager().addActivity(ActivityTestData.SUB_ACTIVITY_WIITH_PROJECTS_TWO);
    Optional<Activity> activity = getActivityManager().getSavedActivityById(SUPER_ACTIVITY_ID);
    Assert.assertTrue(activity.isPresent());

    ActivityView view = new ActivityView(activity.get());
    Assert.assertNotNull(view);
    Accordion subActivitiesAccordion = view.getSubActivitiesAccordion();
    Assert.assertNotNull(subActivitiesAccordion);
    ObservableList<TitledPane> subPanes = subActivitiesAccordion.getPanes();
    Assert.assertNotNull(subPanes);
    Assert.assertFalse(subPanes.isEmpty());
    Assert.assertEquals(2, subPanes.size());
  }

  @Test
  public void testUpdateProjectAndFilterBug() throws ParseException {
    getActivityManager().addActivity(ActivityTestData.ACTIVITY_DATA_LINE);
    Optional<Activity> activity = getActivityManager().getSavedActivityById(ACTIVITY_ONE_ID);
    Assert.assertTrue(activity.isPresent());

    ActivityView view = new ActivityView(activity.get());
    Assert.assertNotNull(view);

    Assert.assertTrue(view.getProjectsField().hasData());
    List<String> projectData = view.getProjectsField().getData();
    Assert.assertNotNull(projectData);
    Assert.assertFalse(projectData.isEmpty());
    Assert.assertEquals(1, projectData.size());
    Assert.assertEquals("OverarchingProject", projectData.get(0));

    view.getPresenter().makeAllFieldsEditableAndRefresh();
    view.getProjectsField().getEditable().getDisplayItem().setText(CHANGED_PROJECT_TEXT);
    Assert.assertEquals(1, view.getProjectsField().update().getData().size());
    Assert.assertEquals(CHANGED_PROJECT_TEXT, view.getProjectsField().update().getData().get(0));

    view.getPresenter().editButtonClicked();
    Assert.assertEquals(1, view.getProjectsField().getData().size());
    Assert.assertEquals(CHANGED_PROJECT_TEXT, view.getProjectsField().getData().get(0));
    List<String> activityProjects = activity.get().getProjects();
    Assert.assertEquals(1, activityProjects.size());
    Assert.assertEquals(CHANGED_PROJECT_TEXT, activityProjects.get(0));

    Optional<Activity> savedActivity = getActivityManager().getSavedActivityById(ACTIVITY_ONE_ID);
    List<String> savedActivityProjects = savedActivity.get().getProjects();
    Assert.assertEquals(1, savedActivityProjects.size());
    Assert.assertEquals(CHANGED_PROJECT_TEXT, savedActivityProjects.get(0));
  }

  @Test
  public void updateDateNullPointerWhenNoDeadlineSet() throws ParseException {
    getActivityManager().addActivity(ActivityTestData.DATA_LINE_NO_DEADLINE);
    Optional<Activity> activity = getActivityManager().getSavedActivityById(ACTIVITY_ONE_ID);
    Assert.assertTrue(activity.isPresent());
    ActivityView view = new ActivityView(activity.get());
    Assert.assertNotNull(view);

    view.getPresenter().makeAllFieldsEditableAndRefresh();
    view.getPresenter().editButtonClicked();

    Assert.assertTrue(true);
    Assert.assertFalse(view.getDeadlineField().hasData());
  }

  @Test
  public void failIfActivityStatusNotUpdated() throws ParseException {
    getActivityManager().addActivity(ActivityTestData.DATA_LINE_NO_DEADLINE);
    Optional<Activity> activity = getActivityManager().getSavedActivityById(ACTIVITY_ONE_ID);
    Assert.assertTrue(activity.isPresent());
    ActivityView view = new ActivityView(activity.get());
    Assert.assertNotNull(view);

    view.getPresenter().headerButtonClicked();

    Optional<Activity> activityAfterUpdate = getActivityManager()
        .getSavedActivityById(ACTIVITY_ONE_ID);
    Assert.assertTrue("The activity was no longer found after the update",
        activityAfterUpdate.isPresent());
    Activity activityToCheck = activityAfterUpdate.get();
    Assert.assertNotNull(activityToCheck);
    Assert
        .assertTrue("The activity should be completed after update", activityToCheck.isCompleted());

  }

  @Test
  public void failIfLocationNotSet() throws ParseException, IOException {
    getActivityManager().addActivity(ActivityTestData.ACTIVITY_NO_PREFIX_LOCATION_LINE);
    Optional<Activity> activity = getActivityManager()
        .getSavedActivityById(ACTIVITY_WITH_LOCATION_ID);
    Assert.assertTrue(activity.isPresent());
    Assert.assertEquals(TEST_LOCATION, activity.get().getLocation());
    ActivityView view = new ActivityView(activity.get());
    Assert.assertNotNull(view);

    view.getPresenter().refresh();

    Assert.assertNotNull(view.getLocationField());
    Assert.assertTrue("LocationField has data", view.getLocationField().hasData());
    Assert.assertEquals(TEST_LOCATION, view.getLocationField().getData());
  }

  @Test
  public void failIfWarningPeriodNotUpdated() throws ParseException {
    getActivityManager().addActivity(ActivityTestData.DATA_LINE_NO_DEADLINE);
    Optional<Activity> activity = getActivityManager().getSavedActivityById(ACTIVITY_ONE_ID);
    Assert.assertTrue(activity.isPresent());
    ActivityView view = new ActivityView(activity.get());
    Assert.assertNotNull(view);

    view.getPresenter().makeAllFieldsEditableAndRefresh();
    Switchable<Label, TextField, String> editableWarningField = view.getWarningPeriodField();
    Assert.assertTrue("Test period not valid", DisplayUtils.isValidWarningPeriodInput("14"));
    editableWarningField.getEditable().setData("14");
    view.getPresenter().editButtonClicked();

    Optional<Activity> activityAfterSave = getActivityManager()
        .getSavedActivityById(ACTIVITY_ONE_ID);
    Assert.assertTrue(activity.isPresent());
    Activity activityToCheck = activityAfterSave.get();
    Assert.assertNotNull(activityToCheck.getWarningTimeFrame());
    Assert.assertEquals("PT14H", activityToCheck.getWarningTimeFrame().toString());
  }

  @Test
  public void failIfParentChangeFailed() throws ParseException {
    getActivityManager().addActivity(ActivityTestData.DATA_LINE_NO_DEADLINE);
    Optional<Activity> activity = getActivityManager().getSavedActivityById(ACTIVITY_ONE_ID);
    Assert.assertTrue(activity.isPresent());
    getActivityManager().addActivity(ActivityTestData.ACTIVITY_NO_PREFIX_LOCATION_LINE);
    Optional<Activity> activityChildToBe = getActivityManager()
        .getSavedActivityById(ACTIVITY_WITH_LOCATION_ID);
    Assert.assertTrue(activityChildToBe.isPresent());
    Assert.assertEquals(2, getActivityManager().getAllActivityNames().size());
    ActivityView view = new ActivityView(activityChildToBe.get(), mockPagePresenter);
    Assert.assertNotNull(view);

    view.getPresenter().changeParent(activity.get().getName());

    activity = getActivityManager().getSavedActivityById(ACTIVITY_ONE_ID);
    Assert.assertTrue(activity.isPresent());
    Assert.assertFalse("Parent activity should have children",
        activity.get().getSubActivities().isEmpty());
    Assert.assertEquals(1, activity.get().getSubActivities().size());

    activityChildToBe = getActivityManager()
        .getSavedActivityById(ACTIVITY_WITH_LOCATION_ID);
    Assert.assertTrue(activityChildToBe.isPresent());
    Assert.assertEquals(ACTIVITY_ONE_ID, activityChildToBe.get().getParentActivity());

  }

  @Test
  public void failIfLogpointMissmatch() throws ParseException {
    getActivityManager().addActivity(ActivityTestData.DATA_LINE_NO_DEADLINE);
    Optional<Activity> activity = getActivityManager().getSavedActivityById(ACTIVITY_ONE_ID);
    Assert.assertTrue(activity.isPresent());
    TimeTrackingManager tm = getMockActController().getTimeTrackingManager();
    ActivityLog testLogActiveOne = new ActivityLog(UUID.fromString(ACTIVITY_ONE_ID));
    testLogActiveOne.startLog();
    testLogActiveOne.stopActiveLog();
    testLogActiveOne.startLog();
    testLogActiveOne.stopActiveLog();
    testLogActiveOne.startLog();
    testLogActiveOne.stopActiveLog();
    Assert.assertEquals(3, testLogActiveOne.getLogpoints().size());
    tm.save(testLogActiveOne);

    ActivityView view = new ActivityView(activity.get());
    GridPane logPoints = view.createLogPoints();

    Assert.assertFalse("Expected log point display items to be created",
        logPoints.getChildren().isEmpty());
    // 3 logpoints and the information header line => 4 children
    Assert.assertEquals(4, logPoints.getChildren().size());
  }

  @Test
  public void failIfLogpointNotInOverlay() throws ParseException {
    getActivityManager().addActivity(ActivityTestData.DATA_LINE_NO_DEADLINE);
    Optional<Activity> activity = getActivityManager().getSavedActivityById(ACTIVITY_ONE_ID);
    Assert.assertTrue(activity.isPresent());
    TimeTrackingManager tm = getMockActController().getTimeTrackingManager();
    ActivityLog testLogActiveOne = new ActivityLog(UUID.fromString(ACTIVITY_ONE_ID));
    testLogActiveOne.startLog();
    testLogActiveOne.stopActiveLog();
    testLogActiveOne.startLog();
    testLogActiveOne.stopActiveLog();
    testLogActiveOne.startLog();
    testLogActiveOne.stopActiveLog();
    Assert.assertEquals(3, testLogActiveOne.getLogpoints().size());
    tm.save(testLogActiveOne);

    ActivityView view = new ActivityView(activity.get());
    view.getPresenter().openLog();

    OverlayPane overlay = view.getOverlay();
    Node shouldBeLogGrid = overlay.getContent();
    Assert.assertTrue(shouldBeLogGrid instanceof GridPane);
    GridPane castedGrid = (GridPane) shouldBeLogGrid;
    Assert.assertFalse("Expected log point display items to be created",
        castedGrid.getChildren().isEmpty());
    // 3 logpoints and the information header line => 4 children
    Assert.assertEquals(4, castedGrid.getChildren().size());
  }

  @Test
  public void failIfNotesOverlayIsFaulty() throws ParseException {
    getActivityManager().addActivity(ActivityTestData.DATA_LINE_NO_DEADLINE);
    Optional<Activity> activity = getActivityManager().getSavedActivityById(ACTIVITY_ONE_ID);
    Assert.assertTrue(activity.isPresent());
    ActivityView view = new ActivityView(activity.get());

    view.getPresenter().openNotes();

    OverlayPane overlay = view.getOverlay();
    Assert.assertNotNull("Expect the overlay to exist", overlay);
    Node overlayContent = overlay.getContent();
    Assert.assertNotNull("Expect the overlay to be populated", overlayContent);
    Assert.assertTrue("Expect the overlay to contain a note grid",
        overlayContent instanceof TextArea);
    TextArea castedOverlayContent = (TextArea) overlayContent;
    Assert.assertTrue("Expect the initial note to be empty",
        StringUtils.isBlank(castedOverlayContent.getText()));
  }

  @Test
  public void failIfNotesOverlayIsEmpty() throws ParseException, IOException {
    getActivityManager().addActivity(ActivityTestData.DATA_LINE_NO_DEADLINE);
    Optional<Activity> activity = getActivityManager().getSavedActivityById(ACTIVITY_ONE_ID);
    Assert.assertTrue(activity.isPresent());
    ActivityView view = new ActivityView(activity.get());
    view.getPresenter().openNotes();
    NoteManager noteManager = getMockActController().getNoteManager();
    List<Note> allNotes = noteManager.getAllNotes();
    Assert.assertFalse("Expect there to be a note", allNotes.isEmpty());
    Assert.assertEquals(1, allNotes.size());
    Optional<Note> note = noteManager
        .findNoteForActivity(UUID.fromString("283b6271-b513-4e89-b757-10e98c9078ea"));
    Assert.assertTrue(note.isPresent());
    Node overlayContent = view.getOverlay().getContent();
    Assert.assertNotNull("Expect the overlay to be populated", overlayContent);
    TextArea castedOverlayContent = (TextArea) overlayContent;

    castedOverlayContent.setText(TEST_NOTE_STRING);
    view.getPresenter().saveNote(note.get(), (TextArea) view.getOverlay().getContent());

    view.getPresenter().openNotes();
    overlayContent = view.getOverlay().getContent();
    Assert.assertNotNull("Expect the overlay to be populated", overlayContent);
    castedOverlayContent = (TextArea) overlayContent;
    Assert.assertEquals(TEST_NOTE_STRING, castedOverlayContent.getText());
  }


}
