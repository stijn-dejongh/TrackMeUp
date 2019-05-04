package be.doji.productivity.priority;

import be.doji.productivity.activity.Activity;
import be.doji.productivity.time.TimePoint;
import java.time.temporal.ChronoUnit;

public class ExpectedEndPriorityCalculator extends PriorityCalculator {

  private static final int OFFSET_IN_DAYS = 2;
  private static final int RANGE_MAX = 150;
  private static final double INCREMENT = -((double)RANGE_MAX / Priority.values().length);


  @Override
  public Priority calculatePriority(Activity activity) {
    double priorityCalculator = RANGE_MAX;
    TimePoint addedOffset = TimePoint.now();

    while (activity.getAssignedTimeSlot().isInRange(addedOffset) && priorityCalculator > 0) {
      addedOffset = addedOffset.add(OFFSET_IN_DAYS, ChronoUnit.DAYS);
      priorityCalculator += INCREMENT;
    }

    priorityCalculator = Math.abs(priorityCalculator);
    priorityCalculator = Math.max(priorityCalculator, 0);
    int index = (int) (priorityCalculator / INCREMENT);

    return index < Priority.values().length ? Priority.values()[index]
        : Priority.values()[Priority.values().length - 1];
  }
}
