package be.doji.productivity.priority;

import be.doji.productivity.activity.Activity;

public interface PriorityCalculator {

  Priority calculatePriority(Activity activity);

}
