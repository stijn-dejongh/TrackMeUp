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
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Doji on 22/10/2017.
 */
public class ActivityManagerTest {

    private static final String ACTIVITY_DATA_LINE = "(A) 2017-10-21:14:13.000 TaskTitle  +OverarchingProject @Tag @Tag2 due:2017-12-21:16:15:00.000 index:0 blocksNext:yes skill:SkillName";
    private static final String NO_PREFIX_DATA_LINE = "Write my own todo.txt webapp +imnu +java +programming @development";
    private static final String NO_PREFIX_DATA_LINE_WITH_NUMBERS = "Write my own 123-todo.txt webapp +imnu +java +programming @development";
    private static  final  String COMPLETED_ACTIVITY = "X (B) Buy thunderbird plugin license";

    @Test public void testReadAcitvities() throws IOException, ParseException {
        ActivityManager am = new ActivityManager(getTestPath("data/testOneTask.txt"));
        am.readActivitiesFromFile();
        List<Activity> readActivities = am.getActivities();
        Assert.assertFalse(readActivities.isEmpty());
        Assert.assertEquals(1, readActivities.size());
    }

    @Test public void testMapStringToActivityCompleted() throws IOException, ParseException {
        ActivityManager am = new ActivityManager(getTestPath("data/testOneTask.txt"));
        Activity activity = am.mapStringToActivity(COMPLETED_ACTIVITY);
        Assert.assertNotNull(activity);

        Assert.assertEquals("B", activity.getPriority());
        Assert.assertEquals("Buy thunderbird plugin license", activity.getName());
        Assert.assertTrue(activity.isCompleted());
    }



    @Test public void testmapStringToActivity() throws IOException, ParseException {
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

        Date deadline = activity.getDeadline();
        Assert.assertNotNull(deadline);
        Calendar calendarWrapper = new GregorianCalendar();
        calendarWrapper.setTime(deadline);
        Assert.assertEquals(21, calendarWrapper.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(11, calendarWrapper.get(Calendar.MONTH));
        Assert.assertEquals(2017, calendarWrapper.get(Calendar.YEAR));
    }

    @Test
    public void testMapNoPrefixLine() throws FileNotFoundException, ParseException {
        ActivityManager am = new ActivityManager(getTestPath("data/testOneTask.txt"));
        Activity activity = am.mapStringToActivity(NO_PREFIX_DATA_LINE);
        Assert.assertNotNull(activity);
        Assert.assertEquals("Write my own todo.txt webapp", activity.getName());
        Assert.assertNotNull(activity.getProjects());
        Assert.assertEquals(3, activity.getProjects().size());
        Assert.assertNotNull(activity.getTags());
        Assert.assertEquals(1, activity.getTags().size());
        Assert.assertEquals("development", activity.getTags().get(0));


    }

    @Test
    public void testMapNoPrefixLineWithNumbers() throws FileNotFoundException, ParseException {
        ActivityManager am = new ActivityManager(getTestPath("data/testOneTask.txt"));
        Activity activity = am.mapStringToActivity(NO_PREFIX_DATA_LINE_WITH_NUMBERS);
        Assert.assertNotNull(activity);
        Assert.assertEquals("Write my own 123-todo.txt webapp", activity.getName());
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
