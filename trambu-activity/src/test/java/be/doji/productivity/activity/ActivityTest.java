package be.doji.productivity.activity;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

  @Test
  public void builder_noParameters() {
    assertThatThrownBy(() -> Activity.builder().build())
        .hasMessage("The activity name can not be empty");
  }

  @Test
  public void builder_noName() {
    assertThatThrownBy(
        () -> Activity.builder().plannedStartAt(TimePoint.fromString("01/05/2019")).build())
        .hasMessage("The activity name can not be empty");
  }

  @Test
  public void builder_endBeforeStart() {
    assertThatThrownBy(
        () -> Activity.builder()
            .name("Invalid start/end combination")
            .plannedStartAt(TimePoint.fromString("01/05/2019"))
            .plannedEndAt(TimePoint.fromString("01/01/2018"))
            .build())
        .hasMessage("The activity end date must be after the start date");
  }

  /**
   * As a user, I want to be able to know the priority of my activities
   */
  @Test
  public void builder_has_defaultPriority() {
    Activity activity = Activity.builder()
        .name("Start design practise")
        .build();
    assertThat(activity.getPriority()).isNotNull();
  }

  /**
   * As a user, I want to be able to know the priority of my activities
   */
  @Test
  public void builder_has_customPriority() {
    Activity activity = Activity.builder()
        .name("Start design practise")
        .priority(Priority.NORMAL)
        .build();
    assertThat(activity.getPriority()).isNotNull();
    assertThat(activity.getPriority()).isEqualTo(Priority.NORMAL);
  }

}