package be.doji.productivity.time;

import static org.assertj.core.api.Java6Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.Month;
import org.junit.Test;

public class TimePointTest {


  @Test
  public void fromString_withHours() {
    TimePoint timePoint = TimePoint.fromString("18/12/1989 12:00:00");
    LocalDateTime converted = timePoint.toLocalDateTime();
    assertThat(converted.getYear()).isEqualTo(1989);
    assertThat(converted.getMonth()).isEqualTo(Month.DECEMBER);
    assertThat(converted.getDayOfMonth()).isEqualTo(18);
    assertThat(converted.getHour()).isEqualTo(12);
    assertThat(converted.getMinute()).isEqualTo(0);
    assertThat(converted.getSecond()).isEqualTo(0);
  }

  @Test
  public void isSameDate_same_dates_match() {
    TimePoint day1 = TimePoint.fromString("18/12/1989");
    TimePoint day2 = TimePoint.fromString("18/12/1989");
    assertThat(TimePoint.isSameDate(day1, day2)).isTrue();
  }

  @Test
  public void isSameDate_same_dates_different_hours_match() {
    TimePoint day1 = TimePoint.fromString("18/12/1989 12:00:00");
    TimePoint day2 = TimePoint.fromString("18/12/1989 18:30:00");
    assertThat(TimePoint.isSameDate(day1, day2)).isTrue();
  }

}