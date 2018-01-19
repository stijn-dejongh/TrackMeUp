package be.doji.productivity.trambucore.parser;

import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.testutil.ActivityTestData;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Doji on 30/10/2017.
 */
public class ActivityParserTest {

    @Test public void testMapStringToActivityCompleted() throws IOException, ParseException {
        Activity activity = ActivityParser.mapStringToActivity(ActivityTestData.COMPLETED_ACTIVITY);
        Assert.assertNotNull(activity);

        Assert.assertEquals("B", activity.getPriority());
        Assert.assertEquals("Buy thunderbird plugin license", activity.getName());
        Assert.assertTrue(activity.isCompleted());
    }

    @Test public void testmapStringToActivity() throws IOException, ParseException {
        Activity activity = ActivityParser.mapStringToActivity(ActivityTestData.ACTIVITY_DATA_LINE);
        Assert.assertNotNull(activity);

        Assert.assertEquals("A", activity.getPriority());
        Assert.assertEquals("TaskTitle", activity.getName());
        Assert.assertFalse(activity.isCompleted());

        List<String> tags = activity.getTags();
        Assert.assertEquals(2, tags.size());
        Assert.assertTrue(tags.contains("Tag"));
        Assert.assertTrue(tags.contains("Tag2"));

        List<String> projects = activity.getProjects();
        Assert.assertEquals(1, projects.size());
        Assert.assertEquals("OverarchingProject", projects.get(0));

        Date deadline = activity.getDeadline();
        Assert.assertNotNull(deadline);
        Calendar parsedDeadline = new GregorianCalendar();
        parsedDeadline.setTime(deadline);
        Assert.assertEquals(21, parsedDeadline.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(11, parsedDeadline.get(Calendar.MONTH));
        Assert.assertEquals(2017, parsedDeadline.get(Calendar.YEAR));
    }

    @Test public void testMapNoPrefixLine() throws IOException, ParseException {
        Activity activity = ActivityParser.mapStringToActivity(ActivityTestData.NO_PREFIX_DATA_LINE);
        Assert.assertNotNull(activity);
        Assert.assertEquals("Write my own todo.txt webapp", activity.getName());
        Assert.assertNotNull(activity.getProjects());
        Assert.assertEquals(3, activity.getProjects().size());
        Assert.assertNotNull(activity.getTags());
        Assert.assertEquals(1, activity.getTags().size());
        Assert.assertEquals("development", activity.getTags().get(0));

    }

    @Test public void testMapNoPrefixLineWithNumbers() throws IOException, ParseException {
        Activity activity = ActivityParser.mapStringToActivity(ActivityTestData.NO_PREFIX_DATA_LINE_WITH_NUMBERS);
        Assert.assertNotNull(activity);
        Assert.assertEquals("Write my own 123-todo.txt webapp", activity.getName());
    }

    @Test public void testCreateActivityWithWarningTimeframe() throws ParseException, IOException {
        Activity activity = ActivityParser.mapStringToActivity(ActivityTestData.ACTIVITY_DATA_LINE_WITH_WARNING);
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getWarningTimeFrame());
        Assert.assertEquals(2, activity.getWarningTimeFrame().toDays());
        Assert.assertEquals((24 * 2) + 3, activity.getWarningTimeFrame().toHours());
        Assert.assertEquals(((24 * 2) + 3) * 60 + 4, activity.getWarningTimeFrame().toMinutes());
    }

    @Test public void testParseWithPeriod() throws ParseException {
        String testString = "(B) TestDeadline warningPeriod:PT24H";
        Activity activity = ActivityParser.mapStringToActivity(testString);
        Assert.assertEquals("TestDeadline", activity.getName());
    }

    @Test public void testParseWithParent() throws ParseException {
        Activity activity = ActivityParser.mapStringToActivity(ActivityTestData.SUB_ACTIVITY_ONE);
        Assert.assertNotNull(activity);
        Assert.assertNotNull(activity.getParentActivity());
        Assert.assertEquals("283b6271-b513-4e89-b757-10e98c9078ea", activity.getParentActivity());
        Assert.assertEquals("Set up IDE", activity.getName());
    }

    @Test public void testParseWithUUID() throws ParseException {
        Activity activity = ActivityParser.mapStringToActivity(ActivityTestData.ACTIVITY_WITH_UUID);
        Assert.assertNotNull(activity);
        Assert.assertEquals("283b6271-b513-4e89-b757-10e98c9078ea", activity.getId().toString());
        Assert.assertEquals("TaskTitle", activity.getName());
    }

    @Test public void testParseLocation() throws ParseException {
        Activity activity = ActivityParser.mapStringToActivity(ActivityTestData.ACTIVITY_NO_PREFIX_LOCATION_LINE);
        Assert.assertNotNull(activity);
        Assert.assertEquals("283b6298-b513-4e89-b757-10e98c9078ea", activity.getId().toString());
        Assert.assertFalse(activity.getTags().isEmpty());
        Assert.assertFalse(activity.getProjects().isEmpty());
        Assert.assertEquals("TestLocation", activity.getLocation());
    }

    @Test public void testParseLocationWithSpace() throws ParseException {
        Activity activity = ActivityParser.mapStringToActivity(ActivityTestData.ACTIVITY_NO_PREFIX_LOCATION_SPACE_LINE);
        Assert.assertNotNull(activity);
        Assert.assertEquals("283b6298-b513-4e89-b757-10e98c9078ea", activity.getId().toString());
        Assert.assertFalse(activity.getTags().isEmpty());
        Assert.assertFalse(activity.getProjects().isEmpty());
        Assert.assertEquals("Test Location", activity.getLocation());
    }

}
