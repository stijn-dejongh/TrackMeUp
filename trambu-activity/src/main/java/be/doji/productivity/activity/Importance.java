package be.doji.productivity.activity;

public class Importance implements Comparable<Importance> {

  public static final Importance LOW = new Importance(10);
  public static final Importance NORMAL = new Importance(50);
  public static final Importance HIGH = new Importance(100);

  private static final int MAX_SCALE_VALUE = 120;
  private static final int MIN_SCALE_VALUE = 0;

  private int innerValue;

  public Importance(int relativeImportance) {

    this.innerValue = scaleImportance(relativeImportance);
  }

  private int scaleImportance(int toScale) {
    return Math.min(Math.max(toScale, MIN_SCALE_VALUE), MAX_SCALE_VALUE);
  }

  @Override
  public int compareTo(Importance o) {
    if (this == o) {
      return 0;
    }

    return Integer.compare(this.innerValue, o.innerValue);
  }

  @Override
  public boolean equals(Object obj) {
    if (super.equals(obj)) {
      return true;
    }

    if (obj instanceof Importance) {
      Importance casted = (Importance) obj;
      return this.compareTo(casted) == 0;
    } else {
      return false;
    }
  }
}
