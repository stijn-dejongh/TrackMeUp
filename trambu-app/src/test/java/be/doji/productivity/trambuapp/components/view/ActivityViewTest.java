package be.doji.productivity.trambuapp.components.view;

import be.doji.productivity.trambuapp.components.TrambuAppTest;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.testutil.ActivityTestData;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

public class ActivityViewTest extends TrambuAppTest {

    private static final String ACTIVITY_ONE_ID = "283b6271-b513-4e89-b757-10e98c9078ea";

    private static final String DEFAULT_WARNING_TIME_STRING = "PT24H";

    @Test public void testFieldCreation() throws ParseException {
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
    }

    @Test public void testFieldPopulation() throws ParseException {
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
    }

}
