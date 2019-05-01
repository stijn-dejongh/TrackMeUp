import java.time.LocalDate;

public class ActivityTest {

  public static void plan_activity_at_certain_date() {
    Activity activity = Activity.builder()
        .name("Start design practise")
        .plannedStartAt("01-05-2019")
        .plannedEndAt("31-05-2019");

    assertThat(activity.getAllocatedTimeslot().getStart()).isSameDay(LocalDate.of(2019, 05, 01));
    assertThat(activity.getAllocatedTimeslot().getEnd()).isSameDay(LocalDate.of(2019, 05, 31));

  }

}