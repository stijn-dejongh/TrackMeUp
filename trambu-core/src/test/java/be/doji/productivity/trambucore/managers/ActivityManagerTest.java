package be.doji.productivity.trambucore.managers;

import be.doji.productivity.trambucore.TrackMeConstants;
import be.doji.productivity.trambucore.TrambuTest;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.testutil.ActivityTestData;
import be.doji.productivity.trambucore.testutil.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Doji on 22/10/2017.
 */
public class ActivityManagerTest extends TrambuTest {

    public static final String DATA_TEST_ONE_TASK_TXT = "data/testOneTask.txt";

    @Test public void testReadAcitvities() throws IOException, ParseException {
        ActivityManager am = new ActivityManager(
                FileUtils.getTestPath(DATA_TEST_ONE_TASK_TXT, this.getClass().getClassLoader()));
        am.readActivitiesFromFile();
        List<Activity> readActivities = am.getActivities();
        Assert.assertFalse(readActivities.isEmpty());
        Assert.assertEquals(1, readActivities.size());
    }

    @Test public void testGetActivitiesByTag() throws IOException, ParseException {
        Path tempFilePath = createTempFile();
        ActivityManager am = new ActivityManager(tempFilePath.toString());
        am.addActivity(ActivityTestData.ACTIVITY_DATA_LINE);
        am.addActivity(ActivityTestData.ACTIVITY_DATA_LINE_CLONE);
        Assert.assertEquals(2, am.getActivities().size());
        Map<Date, List<Activity>> activitiesByTag = am.getActivitiesByTag("Tag");
        Assert.assertNotNull(activitiesByTag);
        Assert.assertFalse(activitiesByTag.isEmpty());
        Assert.assertEquals(2, activitiesByTag.values().iterator().next().size());

        activitiesByTag = am.getActivitiesByTag("Tag3");
        Assert.assertNotNull(activitiesByTag);
        Assert.assertFalse(activitiesByTag.isEmpty());
        Assert.assertEquals(1, activitiesByTag.size());

        Files.delete(tempFilePath);
    }

    @Test public void testGetActivitiesByProject() throws IOException, ParseException {
        Path tempFilePath = createTempFile();
        ActivityManager am = new ActivityManager(tempFilePath.toString());
        am.addActivity(ActivityTestData.ACTIVITY_DATA_LINE);
        am.addActivity(ActivityTestData.ACTIVITY_DATA_LINE_CLONE);
        Assert.assertEquals(2, am.getActivities().size());
        Map<Date, List<Activity>> activitiesByProject = am.getActivitiesByProject("OverarchingProject");
        Assert.assertNotNull(activitiesByProject);
        Assert.assertFalse(activitiesByProject.isEmpty());
        Assert.assertEquals(2, activitiesByProject.values().iterator().next().size());
        Files.delete(tempFilePath);
    }

    @Test public void testGetActivitiesWithDateHeader() throws IOException, ParseException {
        Path tempFilePath = createTempFile();
        ActivityManager am = new ActivityManager(tempFilePath.toString());
        am.addActivity(ActivityTestData.ACTIVITY_DATA_LINE);
        am.addActivity(ActivityTestData.ACTIVITY_DATA_LINE_CLONE);
        am.addActivity(ActivityTestData.COMPLETED_ACTIVITY);
        Map<Date, List<Activity>> activitiesWithDateHeader = am.getActivitiesWithDateHeader();
        Assert.assertNotNull(activitiesWithDateHeader);
        Assert.assertFalse(activitiesWithDateHeader.isEmpty());
        Assert.assertEquals(2, activitiesWithDateHeader.size());
        Date deadLineDate = TrackMeConstants.getDateFormat().parse("2017-12-21:16:15:00.000");
        List<Activity> activitiesWithDeadline = activitiesWithDateHeader.get(deadLineDate);
        Assert.assertNotNull(activitiesWithDeadline);
        Assert.assertEquals(2, activitiesWithDeadline.size());
        Assert.assertNotNull(activitiesWithDateHeader.get(TrackMeConstants.getDefaultDateHeader()));
        Assert.assertEquals(1, activitiesWithDateHeader.get(TrackMeConstants.getDefaultDateHeader()).size());
        Files.delete(tempFilePath);
    }

    @Test public void testSaveManagedActivity() throws ParseException, IOException {
        Path tempFilePath = createTempFile();
        ActivityManager am = new ActivityManager(tempFilePath.toString());
        am.addActivity(ActivityTestData.ACTIVITY_DATA_LINE);
        List<Activity> savedActivities = am.getActivities();
        Assert.assertNotNull(savedActivities);
        Assert.assertEquals(1, savedActivities.size());
        Activity savedActivity = savedActivities.get(0);
        Assert.assertEquals("A", savedActivity.getPriority());
        savedActivity.setPriority("Z");
        am.save(savedActivity);

        savedActivities = am.getActivities();
        Assert.assertNotNull(savedActivities);
        Assert.assertEquals(1, savedActivities.size());
        savedActivity = savedActivities.get(0);
        Assert.assertEquals("Z", savedActivity.getPriority());

        Files.delete(tempFilePath);
    }

    @Test public void testSaveNonManagedActivity() throws IOException, ParseException {
        Path tempFilePath = createTempFile();
        ActivityManager am = new ActivityManager(tempFilePath.toString());
        Assert.assertTrue(am.getActivities().isEmpty());
        Activity testAc = new Activity();
        testAc.setName("TestActivity");
        am.save(testAc);

        List<Activity> savedActivities = am.getActivities();
        Assert.assertFalse(savedActivities.isEmpty());
        Assert.assertEquals(1, savedActivities.size());
        Assert.assertEquals("TestActivity", savedActivities.get(0).getName());

        Files.delete(tempFilePath);
    }

    @Test public void testManageSubProjects() throws IOException, ParseException {
        Path tempFilePath = createTempFile();
        ActivityManager am = new ActivityManager(tempFilePath.toString());
        am.addActivity(ActivityTestData.SUPER_ACTIVITY);
        am.addActivity(ActivityTestData.SUB_ACTIVITY_ONE);
        am.addActivity(ActivityTestData.SUB_ACTIVITY_TWO);
        List<Activity> savedActivities = am.getActivities();
        Assert.assertNotNull(savedActivities);
        Assert.assertEquals(1, savedActivities.size());
        Activity superActivity = savedActivities.get(0);
        Assert.assertEquals("Implement new project", superActivity.getName());
        List<Activity> subActivities = superActivity.getSubActivities();
        Assert.assertNotNull(subActivities);
        Assert.assertEquals(2, subActivities.size());
        Assert.assertEquals("Set up IDE", subActivities.get(0).getName());
        Assert.assertEquals("Read analisis", subActivities.get(1).getName());
        Files.delete(tempFilePath);
    }

    @Test public void testManageDeleteSubProjects() throws IOException, ParseException {
        Path tempFilePath = createTempFile();
        ActivityManager am = new ActivityManager(tempFilePath.toString());
        am.addActivity(ActivityTestData.SUPER_ACTIVITY);
        am.addActivity(ActivityTestData.SUB_ACTIVITY_ONE);
        am.addActivity(ActivityTestData.SUB_ACTIVITY_TWO);
        List<Activity> savedActivities = am.getActivities();
        Assert.assertNotNull(savedActivities);
        Assert.assertEquals(1, savedActivities.size());
        Activity superActivity = savedActivities.get(0);
        Assert.assertEquals("Implement new project", superActivity.getName());
        List<Activity> subActivities = superActivity.getSubActivities();
        Assert.assertNotNull(subActivities);
        Assert.assertEquals(2, subActivities.size());
        Assert.assertEquals("Set up IDE", subActivities.get(0).getName());
        Activity subActivity = subActivities.get(1);
        Assert.assertEquals("Read analisis", subActivity.getName());

        am.delete(subActivity);

        savedActivities = am.getActivities();
        Assert.assertNotNull(savedActivities);
        Assert.assertEquals(1, savedActivities.size());
        superActivity = savedActivities.get(0);
        Assert.assertEquals("Implement new project", superActivity.getName());
        subActivities = superActivity.getSubActivities();
        Assert.assertNotNull(subActivities);
        Assert.assertEquals(1, subActivities.size());
        Assert.assertEquals("Set up IDE", subActivities.get(0).getName());

        Files.delete(tempFilePath);
    }

    @Test public void testAddSubProjectsToProject() throws IOException, ParseException {
        Path tempFilePath = createTempFile();
        ActivityManager am = new ActivityManager(tempFilePath.toString());
        am.addActivity(ActivityTestData.SUPER_ACTIVITY);
        am.addActivity(ActivityTestData.NO_PREFIX_DATA_LINE);
        List<Activity> savedActivities = am.getActivities();
        Assert.assertNotNull(savedActivities);
        Assert.assertEquals(2, savedActivities.size());
        Activity superActivity = savedActivities.get(0);
        Assert.assertEquals("Implement new project", superActivity.getName());
        Activity toBeSub = savedActivities.get(1);
        Assert.assertEquals("Write my own todo.txt webapp", toBeSub.getName());

        am.addActivityAsSub(toBeSub, superActivity);

        savedActivities = am.getActivities();
        Assert.assertEquals(1, savedActivities.size());
        superActivity = savedActivities.get(0);
        Assert.assertEquals("Implement new project", superActivity.getName());
        List<Activity> subActivities = superActivity.getSubActivities();
        Assert.assertFalse(subActivities.isEmpty());
        Assert.assertEquals("Write my own todo.txt webapp", subActivities.get(0).getName());

        Files.delete(tempFilePath);
    }

    @Test public void testGetActivitiesAndSubActivitiesWithTagSharedTag() throws IOException, ParseException {
        Path tempFilePath = createTempFile();
        ActivityManager am = new ActivityManager(tempFilePath.toString());
        am.addActivity(ActivityTestData.SUPER_ACTIVITY);
        am.addActivity(ActivityTestData.SUB_ACTIVITY_WIITH_TAGS_ONE);
        am.addActivity(ActivityTestData.SUB_ACTIVITY_WIITH_TAGS_TWO);

        List<Activity> activities = am.getActivities();
        Assert.assertNotNull(activities);
        Assert.assertEquals(1, activities.size());

        Map<Date, List<Activity>> activitiesByTag = am.getActivitiesByTag(ActivityTestData.SUB_ACTIVITY_TAG_ONE);
        List<Activity> flatActivities = activitiesByTag.entrySet().stream().flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());
        Assert.assertNotNull(flatActivities);
        Assert.assertEquals(2, flatActivities.size());
        for (Activity activity : flatActivities) {
            Assert.assertTrue(activity.getTags().contains(ActivityTestData.SUB_ACTIVITY_TAG_ONE));
        }

        Files.delete(tempFilePath);
    }

    @Test public void testGetActivitiesAndSubActivitiesWithTagUniqueTag() throws IOException, ParseException {
        Path tempFilePath = createTempFile();
        ActivityManager am = new ActivityManager(tempFilePath.toString());
        am.addActivity(ActivityTestData.SUPER_ACTIVITY);
        am.addActivity(ActivityTestData.SUB_ACTIVITY_WIITH_TAGS_ONE);
        am.addActivity(ActivityTestData.SUB_ACTIVITY_WIITH_TAGS_TWO);

        List<Activity> activities = am.getActivities();
        Assert.assertNotNull(activities);
        Assert.assertEquals(1, activities.size());

        Map<Date, List<Activity>> activitiesByTag = am.getActivitiesByTag(ActivityTestData.SUB_ACTIVITY_TAG_TWO);
        List<Activity> flatActivities = activitiesByTag.entrySet().stream().flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());
        Assert.assertNotNull(flatActivities);
        Assert.assertEquals(1, flatActivities.size());
        Assert.assertTrue(flatActivities.get(0).getTags().contains(ActivityTestData.SUB_ACTIVITY_TAG_TWO));

        Files.delete(tempFilePath);
    }

    @Test public void testGetActivitiesAndSubActivitiesWithProjectSharedProject() throws IOException, ParseException {
        Path tempFilePath = createTempFile();
        ActivityManager am = new ActivityManager(tempFilePath.toString());
        am.addActivity(ActivityTestData.SUPER_ACTIVITY);
        am.addActivity(ActivityTestData.SUB_ACTIVITY_WIITH_PROJECTS_ONE);
        am.addActivity(ActivityTestData.SUB_ACTIVITY_WIITH_PROJECTS_TWO);

        List<Activity> activities = am.getActivities();
        Assert.assertNotNull(activities);
        Assert.assertEquals(1, activities.size());

        Map<Date, List<Activity>> activitiesByProject = am
                .getActivitiesByProject(ActivityTestData.SUB_ACTIVITY_PROJECT_ONE);
        List<Activity> flatActivities = activitiesByProject.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()).collect(Collectors.toList());
        Assert.assertNotNull(flatActivities);
        Assert.assertEquals(2, flatActivities.size());
        for (Activity activity : flatActivities) {
            Assert.assertTrue(activity.getProjects().contains(ActivityTestData.SUB_ACTIVITY_PROJECT_ONE));
        }
        Files.delete(tempFilePath);
    }

    @Test public void testGetActivitiesAndSubActivitiesWithProjectsUniqueProject() throws IOException, ParseException {
        Path tempFilePath = createTempFile();
        ActivityManager am = new ActivityManager(tempFilePath.toString());
        am.addActivity(ActivityTestData.SUPER_ACTIVITY);
        am.addActivity(ActivityTestData.SUB_ACTIVITY_WIITH_PROJECTS_ONE);
        am.addActivity(ActivityTestData.SUB_ACTIVITY_WIITH_PROJECTS_TWO);

        List<Activity> activities = am.getActivities();
        Assert.assertNotNull(activities);
        Assert.assertEquals(1, activities.size());

        Map<Date, List<Activity>> activitiesByProjecct = am
                .getActivitiesByProject(ActivityTestData.SUB_ACTIVITY_PROJECT_TWO);
        List<Activity> flatActivities = activitiesByProjecct.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()).collect(Collectors.toList());
        Assert.assertNotNull(flatActivities);
        Assert.assertEquals(1, flatActivities.size());
        Assert.assertTrue(flatActivities.get(0).getProjects().contains(ActivityTestData.SUB_ACTIVITY_PROJECT_TWO));

        Files.delete(tempFilePath);
    }
}
