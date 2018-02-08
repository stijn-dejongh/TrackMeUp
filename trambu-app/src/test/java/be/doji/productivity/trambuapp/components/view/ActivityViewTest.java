package be.doji.productivity.trambuapp.components.view;

import be.doji.productivity.trambuapp.components.TrambuAppTest;
import be.doji.productivity.trambuapp.components.elements.Switchable;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.testutil.ActivityTestData;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import javafx.collections.ObservableList;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import org.junit.Assert;
import org.junit.Test;

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


}
