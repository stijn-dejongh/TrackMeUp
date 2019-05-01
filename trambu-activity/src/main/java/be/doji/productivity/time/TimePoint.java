package be.doji.productivity.time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Wrapper class for date/time representation as I am very frustrated with Java's built in
 * representations... I hate Dates, and I hate the LocalDateTime shizzle. I will wrap this, so I can
 * express my frustration at a single point, without having to refactor my code for hours on
 * end....
 */
public class TimePoint {

  public static final String BASIC_DATE_PATTERN = "dd/MM/yyyy";
  private LocalDateTime internalRepresentation;

  public TimePoint(String timeString) {
    // Do fancy Stuff here
  }

  public TimePoint(LocalDateTime dateTime) {
    this.internalRepresentation = dateTime;
  }


  /* Conversion magic happens here */
  public static TimePoint fromString(String timeString) {
    DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern(BASIC_DATE_PATTERN, Locale.FRANCE);

    return new TimePoint(LocalDateTime.parse(timeString, formatter));
  }
}
