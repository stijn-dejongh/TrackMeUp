package be.doji.productivity.TrackMeUp.managers;

import be.doji.productivity.TrackMeUp.model.tasks.Activity;
import be.doji.productivity.TrackMeUp.model.tasks.Project;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created by Doji on 22/10/2017.
 */
public class ActivityManagerTest {

    private static final String ACTIVITY_DATA_LINE = "(A) 2017-10-21:14:13:0000 TaskTitle  +OverarchingProject @Tag @Tag2 due:2017-12-21:16:15:0000 index:0 blocksNext:yes skill:SkillName";

    @Test public void testReadAcitvities() throws IOException {
        ActivityManager am = new ActivityManager(getTestPath("data/testOneTask.txt"));
        am.readActivitiesFromFile();
        List<Activity> readActivities = am.getActivities();
        Assert.assertFalse(readActivities.isEmpty());
        Assert.assertEquals(1, readActivities.size());
    }

    @Test public void testmapStringToActivity() throws IOException {
        ActivityManager am = new ActivityManager(getTestPath("data/testOneTask.txt"));
        Activity activity = am.mapStringToActivity(ACTIVITY_DATA_LINE);
        Assert.assertNotNull(activity);

        Assert.assertEquals("A", activity.getPriority());
        Assert.assertEquals("TaskTitle", activity.getName());
        Assert.assertFalse(activity.isCompleted());

        List<String> tags = activity.getTags();
        Assert.assertEquals(2, tags.size());
        Assert.assertTrue(tags.contains("Tag"));
        Assert.assertTrue(tags.contains("Tag2"));

        List<Project> projects = activity.getProjects();
        Assert.assertEquals(1, projects.size());
        Assert.assertEquals("OverarchingProject", projects.get(0).getName());


    }

    public String getTestPath(String path) throws FileNotFoundException {
        File testFile = ResourceUtils
                .getFile(getClass().getClassLoader().getResource(path));
        Assert.assertTrue(testFile.exists());
        String testPath = testFile.getAbsolutePath();
        Assert.assertFalse(StringUtils.isBlank(testPath));
        return testPath;
    }
}
