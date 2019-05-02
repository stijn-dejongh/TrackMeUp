package be.doji.productivity.activity;

import static org.assertj.core.api.Assertions.assertThat;

import be.doji.productivity.time.TimePoint;
import org.junit.Test;

public class ActivityTest {

  @Test
  public void activity_basic_construction_test() {
    Activity activity = Activity.builder()
        .name("Start design practise")
        .plannedStartAt(TimePoint.fromString("01/05/2019"))
        .plannedEndAt(TimePoint.fromString("31/05/2019"))
        .build();

    assertThat(activity).isNotNull().as("Activity after creation was null");
    assertThat(activity.getAssignedTimeSlot().startsOn(TimePoint.fromString("01/05/2019"))).isTrue()
        .as("Created activity has wrong start date");
    assertThat(activity.getAssignedTimeSlot().endsOn(TimePoint.fromString("31/05/2019"))).isTrue()
        .as("Created activity has wrong start date");
    assertThat(activity.getName()).isEqualTo("Start design practise")
        .as("Created activity has wrong title");
  }

}