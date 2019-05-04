package be.doji.productivity.timelog;

import be.doji.productivity.activity.Activity;
import be.doji.productivity.time.TimeSlot;
import java.util.List;

public class TimeLog {


  private final Activity activity;

  public TimeLog(Activity activity) {
    this.activity = activity;
  }

  public void addLogPoint(TimeSlot logpoint) {

  }

  public List<TimeSlot> getSlots() {
    return null;
  }
}
