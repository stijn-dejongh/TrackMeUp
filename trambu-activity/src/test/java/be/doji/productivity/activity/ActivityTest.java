package be.doji.productivity.activity;


import be.doji.productivity.time.TimePoint;
import org.junit.Assert;
import org.junit.Test;

public class ActivityTest {

  @Test
  public void activityBuilderBasic() {
    Activity activity = Activity.builder()
        .name("Start design practise")
        .plannedStartAt(TimePoint.fromString("01/05/2019"))
        .plannedEndAt(TimePoint.fromString("31/05/2019"))
        .build();

    Assert.assertNotNull("Activity after creation was null", activity);
    Assert.assertTrue("Created activity has wrong start date",
        activity.getAssignedTimeSlot().startsOn(TimePoint.fromString("01/05/2019")));
    Assert.assertTrue("Created activity has wrong start date",
        activity.getAssignedTimeSlot().endsOn(TimePoint.fromString("31/05/2019")));
    Assert.assertEquals("Created activity has wrong title", "Start design practise",
        activity.getName());
  }

}