package be.doji.productivity.priority;

import be.doji.productivity.activity.Activity;

public abstract class PriorityCalculator {

  public abstract Priority calculatePriority(Activity activity);

}
