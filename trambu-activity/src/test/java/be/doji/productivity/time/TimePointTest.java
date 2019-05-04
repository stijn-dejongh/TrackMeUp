package be.doji.productivity.time;

import java.time.LocalDateTime;
import java.time.Month;
import org.junit.Assert;
import org.junit.Test;

public class TimePointTest {


  public static final String TEST_DATE = "18/12/1989";

  @Test
  public void fromString_toLocalDateTime_dateOnly() {
    TimePoint timePoint = TimePoint.fromString("18/12/1989");
    LocalDateTime converted = timePoint.toLocalDateTime();
    Assert.assertEquals(1989, converted.getYear());
    Assert.assertEquals(Month.DECEMBER, converted.getMonth());
    Assert.assertEquals(18, converted.getDayOfMonth());
    Assert.assertEquals(0, converted.getHour());
    Assert.assertEquals(0, converted.getMinute());
    Assert.assertEquals(0, converted.getSecond());
  }

  @Test
  public void fromString_toLocalDateTime_withHours() {
    TimePoint timePoint = TimePoint.fromString("18/12/1989 12:00:00");
    LocalDateTime converted = timePoint.toLocalDateTime();
    Assert.assertEquals(1989, converted.getYear());
    Assert.assertEquals(Month.DECEMBER, converted.getMonth());
    Assert.assertEquals(18, converted.getDayOfMonth());
    Assert.assertEquals(12, converted.getHour());
    Assert.assertEquals(0, converted.getMinute());
    Assert.assertEquals(0, converted.getSecond());
  }

  @Test
  public void fromString_toLocalDateTime_full() {
    TimePoint timePoint = TimePoint.fromString("18/12/1989 12:13:14");
    LocalDateTime converted = timePoint.toLocalDateTime();
    Assert.assertEquals(1989, converted.getYear());
    Assert.assertEquals(Month.DECEMBER, converted.getMonth());
    Assert.assertEquals(18, converted.getDayOfMonth());
    Assert.assertEquals(12, converted.getHour());
    Assert.assertEquals(13, converted.getMinute());
    Assert.assertEquals(14, converted.getSecond());
  }

  @Test
  public void isSameDate_sameDates_match() {
    TimePoint day1 = TimePoint.fromString(TEST_DATE);
    TimePoint day2 = TimePoint.fromString(TEST_DATE);
    Assert.assertTrue(TimePoint.isSameDate(day1, day2));
  }

  @Test
  public void isSameDate_differentDates() {
    TimePoint day1 = TimePoint.fromString(TEST_DATE);
    TimePoint day2 = TimePoint.fromString("19/12/1989");
    Assert.assertFalse(TimePoint.isSameDate(day1, day2));
  }

  @Test
  public void isSameDate_differentDates_differentHours() {
    TimePoint day1 = TimePoint.fromString("18/12/1989 12:00:05");
    TimePoint day2 = TimePoint.fromString("19/12/1989 19:35:15");
    Assert.assertFalse(TimePoint.isSameDate(day1, day2));
  }

  @Test
  public void isSameDate_sameDates_differentHours_match() {
    TimePoint day1 = TimePoint.fromString("18/12/1989 12:00:00");
    TimePoint day2 = TimePoint.fromString("18/12/1989 18:30:00");
    Assert.assertTrue(TimePoint.isSameDate(day1, day2));
  }

}