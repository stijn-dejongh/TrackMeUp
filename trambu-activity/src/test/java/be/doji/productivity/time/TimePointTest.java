package be.doji.productivity.time;

import static org.assertj.core.api.Java6Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.Month;
import org.junit.Test;

public class TimePointTest {


  public static final String TEST_DATE = "18/12/1989";

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
  public void isSameDate_sameDates_match() {
    TimePoint day1 = TimePoint.fromString(TEST_DATE);
    TimePoint day2 = TimePoint.fromString(TEST_DATE);
    assertThat(TimePoint.isSameDate(day1, day2)).isTrue();
  }

  @Test
  public void isSameDate_differentDates() {
    TimePoint day1 = TimePoint.fromString(TEST_DATE);
    TimePoint day2 = TimePoint.fromString("19/12/1989");
    assertThat(TimePoint.isSameDate(day1, day2)).isFalse();
  }

  @Test
  public void isSameDate_differentDates_differentHours() {
    TimePoint day1 = TimePoint.fromString("18/12/1989 12:00:05");
    TimePoint day2 = TimePoint.fromString("19/12/1989 19:35:15");
    assertThat(TimePoint.isSameDate(day1, day2)).isFalse();
  }

  @Test
  public void isSameDate_sameDates_differentHours_match() {
    TimePoint day1 = TimePoint.fromString("18/12/1989 12:00:00");
    TimePoint day2 = TimePoint.fromString("18/12/1989 18:30:00");
    assertThat(TimePoint.isSameDate(day1, day2)).isTrue();
  }

}