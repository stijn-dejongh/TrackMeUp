package be.doji.productivity.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Wrapper class for date/time representation as I am very frustrated with Java's built in
 * representations... I hate Dates, and I hate the LocalDateTime shizzle. I will wrap this, so I can
 * express my frustration at a single point, without having to refactor my code for hours on
 * end....
 */
public class TimePoint {

  public static final String BASIC_DATE_PATTERN = "dd/MM/uuuu";
  private static final Map<Matcher, DateTimeFormatter> CONVERTER_MAP = createConverters();

  private LocalDateTime internalRepresentation;


  public TimePoint(LocalDateTime dateTime) {
    this.internalRepresentation = dateTime;
  }

  private static Map<Matcher, DateTimeFormatter> createConverters() {
    Map<Matcher, DateTimeFormatter> converters = new HashMap<>();

    return converters;
  }
  
  /* Conversion magic happens here */
  public static TimePoint fromString(String timeString) {
    //TODO: switch between date only and time here
    // using converters

    DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern(BASIC_DATE_PATTERN, Locale.FRANCE);
    LocalDate dateOnly = LocalDate.parse(timeString, formatter);

    return new TimePoint(dateOnly.atStartOfDay());
  }

  public static boolean isSameDate(TimePoint start, TimePoint reference) {
    //TODO
    return false;
  }

  public LocalDateTime toLocalDateTime() {
    return this.internalRepresentation;
  }
}
