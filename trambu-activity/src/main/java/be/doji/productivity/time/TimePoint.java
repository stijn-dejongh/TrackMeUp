package be.doji.productivity.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * Wrapper class for date/time representation as I am very frustrated with Java's built in
 * representations... I hate Dates, and I hate the LocalDateTime shizzle. I will wrap this, so I can
 * express my frustration at a single point, without having to refactor my code for hours on
 * end....
 */
public class TimePoint {

  public static final String BASIC_DATE_PATTERN = "dd/MM/uuuu";
  public static final String BASIC_DATE_REGEX = "\\d\\d/\\d\\d/\\d\\d\\d\\d";

  public static final String BASIC_DATE_TIME_PATTERN = "dd/MM/uuuu HH:mm:ss";
  public static final String BASIC_DATE_TIME_REGEX = "\\d\\d/\\d\\d/\\d\\d\\d\\d \\d\\d:\\d\\d:\\d\\d";

  public static final String EXTENDED_DATE_TIME_PATTERN = "dd/MM/uuuu HH:mm:ss:SSS";
  public static final String EXTENDED_DATE_TIME_REGEX = "\\d\\d/\\d\\d/\\d\\d\\d\\d \\d\\d:\\d\\d:\\d\\d:\\d\\d\\d";

  private static final Map<Pattern, DateTimeFormatter> DATE_CONVERTERS = createDateConverters();
  private static final Map<Pattern, DateTimeFormatter> DATE_TIME_CONVERTERS = createDateTimeConverters();


  private final LocalDateTime internalRepresentation;


  public TimePoint(LocalDateTime dateTime) {
    this.internalRepresentation = dateTime;
  }

  private static Map<Pattern, DateTimeFormatter> createDateConverters() {
    Map<Pattern, DateTimeFormatter> converters = new HashMap<>();
    converters.put(Pattern.compile(BASIC_DATE_REGEX),
        DateTimeFormatter.ofPattern(BASIC_DATE_PATTERN, Locale.FRANCE));

    return converters;
  }

  private static Map<Pattern, DateTimeFormatter> createDateTimeConverters() {
    Map<Pattern, DateTimeFormatter> converters = new HashMap<>();
    converters.put(Pattern.compile(BASIC_DATE_TIME_REGEX),
        DateTimeFormatter.ofPattern(BASIC_DATE_TIME_PATTERN, Locale.FRANCE));
    converters.put(Pattern.compile(EXTENDED_DATE_TIME_REGEX),
        DateTimeFormatter.ofPattern(EXTENDED_DATE_TIME_PATTERN, Locale.FRANCE));

    return converters;
  }

  public LocalDateTime toLocalDateTime() {
    return this.internalRepresentation;
  }

  private LocalDate toLocalDate() {
    return this.internalRepresentation.toLocalDate();
  }


  /* Utility Methods */

  public static TimePoint fromString(String timeString) {

    for (Entry<Pattern, DateTimeFormatter> entry : DATE_TIME_CONVERTERS.entrySet()) {
      if (entry.getKey().matcher(timeString).matches()) {
        return new TimePoint(LocalDateTime.parse(timeString, entry.getValue()));
      }
    }

    for (Entry<Pattern, DateTimeFormatter> entry : DATE_CONVERTERS.entrySet()) {
      if (entry.getKey().matcher(timeString).matches()) {
        return new TimePoint(LocalDate.parse(timeString, entry.getValue()).atStartOfDay());
      }
    }
    throw new IllegalArgumentException(
        "Could not parse given Date string: No matching parsers found for string [" + timeString
            + "] ");
  }

  public static boolean isBefore(TimePoint plannedEnd, TimePoint plannedStart) {
    return plannedEnd != null &&
        plannedStart != null &&
        plannedEnd.toLocalDateTime().isBefore(plannedStart.toLocalDateTime());
  }

  public static boolean isSameDate(TimePoint start, TimePoint reference) {
    LocalDate o1 = start.toLocalDate();
    LocalDate o2 = reference.toLocalDate();
    return o1.isEqual(o2);
  }
}
