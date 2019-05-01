import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javafx.util.converter.DateTimeStringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;
import javax.swing.text.DateFormatter;

/**
 * An activity is something you do at a certain time. Activities have a link to times spent on it
 *
 * An Activity has a configurable set of properties. These can be configured.
 */
public class Activity {

  private String name;
  private LocalDateTime plannedStart;
  private LocalDateTime plannedEnd;

  private void setName(String activityName) {
    this.name = activityName;
  }

  private void setPlannedStart(LocalDateTime plannedStart) {
    this.plannedStart = plannedStart;
  }

  private void setPlannedEnd(LocalDateTime plannedEnd) {
    this.plannedEnd = plannedEnd;
  }


  private Activity() {
  }

  public static ActivityBuilder builder() {
    return new ActivityBuilder();
  }

  public TimeSlot getAllocatedTimeslot() {
    return new TimeSlot(plannedStart, plannedEnd);
  }

  public static class ActivityBuilder {

    private String activityName;
    private LocalDateTime plannedStart;
    private LocalDateTime plannedEnd;

    public ActivityBuilder name(String activityName) {
      this.activityName = activityName;
      return this;
    }

    public ActivityBuilder plannedStartAt(LocalDateTime startDate) {
      this.plannedStart = startDate;
      return this;
    }

    public ActivityBuilder plannedEndAt(LocalDateTime plannedEnd) {
      this.plannedEnd = plannedEnd;
      return this;
    }

    public Activity build() {
      Activity result = new Activity();
      result.setName(this.activityName);
      result.setPlannedStart(this.plannedStart);
      result.setPlannedEnd(this.plannedEnd);
      return result;
    }
  }
}
