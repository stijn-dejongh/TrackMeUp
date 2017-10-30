package be.doji.productivity.TrackMeUp.testutil;

/**
 * Created by Doji on 30/10/2017.
 */
public final class ActivityTestData {

    /**
     * Utility methods should not have a public or default constructor
     */
    private ActivityTestData() {
    }

    public static final String ACTIVITY_DATA_LINE = "(A) 2017-10-21:14:13.000 TaskTitle  +OverarchingProject @Tag @Tag2 due:2017-12-21:16:15:00.000 index:0 blocksNext:yes skill:SkillName";
    public static final String ACTIVITY_DATA_LINE_CLONE = "(A) 2017-10-21:14:13.000 TaskTitle2  +OverarchingProject @Tag @Tag2 @Tag3 due:2017-12-21:16:15:00.000 index:0 blocksNext:yes skill:SkillName";
    public static final String NO_PREFIX_DATA_LINE = "Write my own todo.txt webapp +imnu +java +programming @development";
    public static final String NO_PREFIX_DATA_LINE_WITH_NUMBERS = "Write my own 123-todo.txt webapp +imnu +java +programming @development";
    public static final String COMPLETED_ACTIVITY = "X (B) Buy thunderbird plugin license";
    public static final String ACTIVITY_DATA_LINE_WITH_WARNING = "(A) 2017-10-21:14:13.000 TaskTitle  +OverarchingProject @Tag @Tag2 due:2017-12-21:16:15:00.000 index:0 blocksNext:yes skill:SkillName warningPeriod:P2DT3H4M";
}
