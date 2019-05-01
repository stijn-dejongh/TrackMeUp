import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.time.LocalDateTime;
import org.junit.Test;

public class ActivityTest {

  @Test
  public void activity_builder_creates_object() throws ParseException {
    Activity activity = Activity.builder()
        .name("Start design practise")
        .plannedStartAt(LocalDateTime.of(2019, 05, 01, 11, 00))
        .plannedEndAt(LocalDateTime.of(2019, 05, 31, 18, 00))
        .build();

    assertThat(activity).isNotNull();
  }

}