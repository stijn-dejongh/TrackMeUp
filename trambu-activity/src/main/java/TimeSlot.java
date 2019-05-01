import java.time.LocalDateTime;

public class TimeSlot {

  private final LocalDateTime start;
  private final LocalDateTime end;

  public TimeSlot(LocalDateTime plannedStart, LocalDateTime plannedEnd) {
    this.start = plannedStart;
    this.end = plannedEnd;
  }

  public LocalDateTime getStart() {
    return this.start;
  }

  public LocalDateTime getEnd() {
    return this.end;
  }
}
