package be.doji.productivity.time;

public class TimeSlot {

  private final TimePoint start;
  private final TimePoint end;

  public TimeSlot(TimePoint plannedStart, TimePoint plannedEnd) {
    this.start = plannedStart;
    this.end = plannedEnd;
  }

  public TimePoint getStart() {
    return this.start;
  }

  public TimePoint getEnd() {
    return this.end;
  }

  public boolean startsOn(TimePoint reference) {
    return TimePoint.isSameDate(this.start, reference);
  }

  public boolean endsOn(TimePoint reference) {
    return TimePoint.isSameDate(this.end, reference);
  }

  public static TimeSlot between(TimePoint from, TimePoint to) {
    return TimePoint.isBefore(to, from) ? new TimeSlot(from, to) : new TimeSlot(to, from);
  }

  public boolean isInRange(TimePoint toCheck) {
    return TimePoint.isBefore(toCheck, end) && TimePoint.isBefore(start, toCheck);
  }
}
