package be.doji.productivity.timelog;

import be.doji.productivity.activity.Activity;
import be.doji.productivity.time.TimeSlot;
import java.util.ArrayList;
import java.util.List;

public class TimeLog {


  private final Activity activity;
  private List<TimeSlot> logs;

  public TimeLog(Activity activity) {
    this.activity = activity;
    this.logs = new ArrayList<>();
  }

  public Activity getActivity() {
    return activity;
  }

  public void addLogPoint(TimeSlot logpoint) {
    logs.add(logpoint);
  }

  public List<TimeSlot> getSlots() {
    return new ArrayList<>(logs);
  }
}
