package be.doji.productivity.trackme.managers;

import be.doji.productivity.trackme.model.tasks.Activity;
import be.doji.productivity.trackme.testutil.ActivityTestData;
import be.doji.productivity.trackme.testutil.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Doji on 22/10/2017.
 */
public class ActivityManagerTest {

    public static final String DATA_TEST_ONE_TASK_TXT = "data/testOneTask.txt";

    @Test public void testReadAcitvities() throws IOException, ParseException {
        ActivityManager am = new ActivityManager(FileUtils.getTestPath(DATA_TEST_ONE_TASK_TXT));
        am.readActivitiesFromFile();
        List<Activity> readActivities = am.getActivities();
        Assert.assertFalse(readActivities.isEmpty());
        Assert.assertEquals(1, readActivities.size());
    }

    @Test public void testGetActivitiesByTag() throws IOException, ParseException {
        Path tempFilePath = createTempFile();
        ActivityManager am = new ActivityManager(tempFilePath.toString());
        am.addActivityAndSaveToFile(ActivityTestData.ACTIVITY_DATA_LINE);
        am.addActivityAndSaveToFile(ActivityTestData.ACTIVITY_DATA_LINE_CLONE);
        Assert.assertEquals(2, am.getActivities().size());
        List<Activity> activitiesByTag = am.getActivitiesByTag("Tag");
        Assert.assertNotNull(activitiesByTag);
        Assert.assertFalse(activitiesByTag.isEmpty());
        Assert.assertEquals(2, activitiesByTag.size());

        activitiesByTag = am.getActivitiesByTag("Tag3");
        Assert.assertNotNull(activitiesByTag);
        Assert.assertFalse(activitiesByTag.isEmpty());
        Assert.assertEquals(1, activitiesByTag.size());

        Files.delete(tempFilePath);
    }

    @Test public void testGetActivitiesByProject() throws IOException, ParseException {
        Path tempFilePath = createTempFile();
        ActivityManager am = new ActivityManager(tempFilePath.toString());
        am.addActivityAndSaveToFile(ActivityTestData.ACTIVITY_DATA_LINE);
        am.addActivityAndSaveToFile(ActivityTestData.ACTIVITY_DATA_LINE_CLONE);
        Assert.assertEquals(2, am.getActivities().size());
        List<Activity> activitiesByProject = am.getActivitiesByProject("OverarchingProject");
        Assert.assertNotNull(activitiesByProject);
        Assert.assertFalse(activitiesByProject.isEmpty());
        Assert.assertEquals(2, activitiesByProject.size());
        Files.delete(tempFilePath);
    }

    private Path createTempFile() throws IOException {
        Path directoryPath = Paths.get(FileUtils.getTestPath(DATA_TEST_ONE_TASK_TXT)).getParent();
        return Files.createTempFile(directoryPath, "temp", "txt");
    }
}
