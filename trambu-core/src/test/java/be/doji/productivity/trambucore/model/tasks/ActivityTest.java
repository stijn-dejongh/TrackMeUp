package be.doji.productivity.trambucore.model.tasks;

import be.doji.productivity.trambucore.TrackMeConstants;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class ActivityTest {

    private static final String ACT_DEFAULT_NAME = "TestName";
    private static final String ACT_PRIORITY = "D";

    @Test public void testToStringDefaultActivity() {
        Activity testActivity = new Activity(ACT_DEFAULT_NAME);
        String toString = testActivity.toString();
        Assert.assertTrue(toString.startsWith("(" + PriorityConstants.PRIORITY_MEDIUM + ")"));
        Assert.assertEquals(toString,
                "(" + PriorityConstants.PRIORITY_MEDIUM + ")" + " " + ACT_DEFAULT_NAME + " " + getDefaultFieldSuffix(
                        testActivity));
    }

    @Test public void testToStringCustomPriority() {
        Activity testActivity = new Activity(ACT_DEFAULT_NAME);
        testActivity.setPriority(ACT_PRIORITY);
        String toString = testActivity.toString();
        Assert.assertTrue(toString.startsWith("(" + ACT_PRIORITY + ")"));
    }

    @Test public void testToStringDone() {
        Activity testActivity = new Activity(ACT_DEFAULT_NAME);
        testActivity.setCompleted(true);
        String activityString = testActivity.toString();
        Assert.assertTrue(activityString.startsWith(TrackMeConstants.INDICATOR_DONE));
        Assert.assertTrue(activityString.startsWith(
                TrackMeConstants.INDICATOR_DONE + " (" + testActivity.getPriority() + ") " + ACT_DEFAULT_NAME));
    }

    @Test public void testToStringProjects() {
        Activity testActivity = new Activity(ACT_DEFAULT_NAME);
        String[] projectArray = { "projectOne", "projectTwo" };
        List<String> projects = Arrays.asList(projectArray);
        testActivity.setProjects(projects);
        String activityString = testActivity.toString();
        Assert.assertTrue(activityString.contains(TrackMeConstants.INDICATOR_PROJECT + "projectOne"));
        Assert.assertTrue(activityString.contains(TrackMeConstants.INDICATOR_PROJECT + "projectTwo"));
        Assert.assertTrue(activityString.endsWith(getDefaultFieldSuffix(testActivity)));
    }

    @Test public void testToStringTags() {
        Activity testActivity = new Activity(ACT_DEFAULT_NAME);
        String[] tagArray = { "tagOne", "tagTwo" };
        List<String> projects = Arrays.asList(tagArray);
        testActivity.setTags(projects);
        String activityString = testActivity.toString();
        Assert.assertTrue(activityString.contains(TrackMeConstants.INDICATOR_TAG + "tagOne"));
        Assert.assertTrue(activityString.contains(TrackMeConstants.INDICATOR_TAG + "tagTwo"));
        Assert.assertTrue(activityString.endsWith(getDefaultFieldSuffix(testActivity)));
    }

    @Test public void testToStringDeadline() {
        Activity testActivity = new Activity(ACT_DEFAULT_NAME);
        Calendar deadLine = new GregorianCalendar(2020, 11, 31);
        testActivity.setDeadline(deadLine.getTime());
        String toString = testActivity.toString();
        Assert.assertTrue(toString.contains(TrackMeConstants.INDICATOR_DEADLINE));
        Assert.assertTrue(toString.contains(
                TrackMeConstants.INDICATOR_DEADLINE + TrackMeConstants.getDateFormat().format(deadLine.getTime())));
        Assert.assertTrue(toString.endsWith(getDefaultFieldSuffix(testActivity)));
    }

    @Test public void testIsSetAlertActive() {
        Activity testActivity = new Activity(ACT_DEFAULT_NAME);
        Calendar referenceDate = new GregorianCalendar();
        Calendar deadLine = new GregorianCalendar(referenceDate.get(Calendar.YEAR), referenceDate.get(Calendar.MONTH),
                referenceDate.get(Calendar.DAY_OF_MONTH) + 2);
        testActivity.setDeadline(deadLine.getTime());
        testActivity.setWarningTimeFrame(Duration.ofDays(3));
        Assert.assertTrue(testActivity.isAlertActive());

    }

    @Test public void testLocationToString() {
        Activity testActivity = new Activity(ACT_DEFAULT_NAME);
        testActivity.setLocation("Blanden");
        String toString = testActivity.toString();
        Assert.assertTrue(StringUtils.isNotBlank(toString));
        Assert.assertTrue(toString.contains(TrackMeConstants.INDICATOR_LOCATION));
        Assert.assertTrue(toString.contains("Blanden"));
        Assert.assertTrue(toString.contains(TrackMeConstants.INDICATOR_LOCATION + "Blanden"));
    }

    @Test public void testIsSetAlertInactive() {
        Activity testActivity = new Activity(ACT_DEFAULT_NAME);
        Calendar referenceDate = new GregorianCalendar();
        Calendar deadLine = new GregorianCalendar(referenceDate.get(Calendar.YEAR), referenceDate.get(Calendar.MONTH),
                referenceDate.get(Calendar.DAY_OF_MONTH) + 2);
        testActivity.setDeadline(deadLine.getTime());
        testActivity.setWarningTimeFrame(Duration.ofDays(1));
        Assert.assertFalse(testActivity.isAlertActive());

    }

    private String getDefaultFieldSuffix(Activity testActivity) {
        return TrackMeConstants.INDICATOR_WARNING_PERIOD + testActivity.getWarningTimeFrame().toString() + " "
                + TrackMeConstants.INDICATOR_UUID + testActivity.getId();
    }

}
