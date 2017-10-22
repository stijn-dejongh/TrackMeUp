package be.doji.productivity.TrackMeUp.managers;

import be.doji.productivity.TrackMeUp.model.tasks.Activity;
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

    @Test public void testReadAcitvities() throws IOException {
        ActivityManager am = new ActivityManager();
        am.readActivitiesFromFile(getTestPath("data/testOneTask.txt"));
        List<Activity> readActivities = am.getActivities();
        Assert.assertFalse(readActivities.isEmpty());
        Assert.assertEquals(1, readActivities.size());
    }

    @Test public void testReadAcitvityContent() throws IOException {
        ActivityManager am = new ActivityManager();
        am.readActivitiesFromFile(getTestPath("data/testOneTask.txt"));
        List<Activity> readActivities = am.getActivities();
        Activity activity = readActivities.get(0);
        Assert.assertNotNull(activity);
        Assert.assertEquals("A", activity.getPriority());
        Assert.assertEquals("TaskTitle", activity.getName());
        Assert.assertFalse(activity.isCompleted());

        List<String> tags = activity.getTags();
        Assert.assertEquals(2, tags.size());
        Assert.assertTrue(tags.contains("Tag"));
        Assert.assertTrue(tags.contains("Tag2"));

        
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
