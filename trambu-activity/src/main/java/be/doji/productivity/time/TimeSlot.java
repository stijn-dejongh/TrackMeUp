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
    return TimePoint.sameDate(this.start, reference);
  }

  public boolean endsOn(TimePoint reference) {
    return TimePoint.sameDate(this.end, reference);
  }
}
