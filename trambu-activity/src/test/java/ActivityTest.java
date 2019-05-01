import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import be.doji.productivity.activity.Activity;
import be.doji.productivity.time.TimePoint;
import org.junit.Assert;
import org.junit.Test;

public class ActivityTest {

  @Test
  public void activity_basic_construction_test() {
    Activity activity = Activity.builder()
        .name("Start design practise")
        .plannedStartAt(TimePoint.fromString("01/05/2019"))
        .plannedEndAt(TimePoint.fromString("31/05/2019"))
        .build();

    assertThat(activity).isNotNull();
    assertTrue(activity.getAssignedTimeSlot().startsOn(TimePoint.fromString("01/05/2019")));
    assertTrue(activity.getAssignedTimeSlot().endsOn(TimePoint.fromString("31/05/2019")));
    Assert.assertThat(activity.getName()).equals("Start design practise");
  }

}