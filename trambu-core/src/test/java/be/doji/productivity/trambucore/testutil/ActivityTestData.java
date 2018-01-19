package be.doji.productivity.trambucore.testutil;

/**
 * Created by Doji on 30/10/2017.
 */
public final class ActivityTestData {

    /**
     * Utility methods should not have a public or default constructor
     */
    private ActivityTestData() {
    }

    public static final String ACTIVITY_DATA_LINE = "(A) 2017-10-21:14:13.000 TaskTitle  +OverarchingProject @Tag @Tag2 due:2017-12-21:16:15:00.000 index:0 blocksNext:yes skill:SkillName uuid:283b6271-b513-4e89-b757-10e98c9078ea";
    public static final String ACTIVITY_DATA_LINE_CLONE = "(A) 2017-10-21:14:13.000 TaskTitle2  +OverarchingProject @Tag @Tag2 @Tag3 due:2017-12-21:16:15:00.000 index:0 blocksNext:yes skill:SkillName";
    public static final String NO_PREFIX_DATA_LINE = "Write my own todo.txt webapp +imnu +java +programming @development";
    public static final String NO_PREFIX_DATA_LINE_WITH_NUMBERS = "Write my own 123-todo.txt webapp +imnu +java +programming @development";
    public static final String COMPLETED_ACTIVITY = "X (B) Buy thunderbird plugin license";
    public static final String ACTIVITY_DATA_LINE_WITH_WARNING = "(A) 2017-10-21:14:13.000 TaskTitle  +OverarchingProject @Tag @Tag2 due:2017-12-21:16:15:00.000 index:0 blocksNext:yes skill:SkillName warningPeriod:P2DT3H4M";
    public static final String ACTIVITY_WITH_UUID = "(A) 2017-10-21:14:13.000 TaskTitle  +OverarchingProject @Tag @Tag2 due:2017-12-21:16:15:00.000 index:0 blocksNext:yes skill:SkillName warningPeriod:P2DT3H4M uuid:283b6271-b513-4e89-b757-10e98c9078ea";

    public static final String SUPER_ACTIVITY = "X (B) Implement new project uuid:283b6271-b513-4e89-b757-10e98c9078ea";
    public static final String SUB_ACTIVITY_ONE = "X (B) Set up IDE super:283b6271-b513-4e89-b757-10e98c9078ea";
    public static final String SUB_ACTIVITY_TWO = "X (B) Read analisis super:283b6271-b513-4e89-b757-10e98c9078ea";

    public static final String SUB_ACTIVITY_WIITH_TAGS_ONE = "X (B) Set up IDE @development super:283b6271-b513-4e89-b757-10e98c9078ea";
    public static final String SUB_ACTIVITY_WIITH_TAGS_TWO = "X (B) Read analisis @development @testing super:283b6271-b513-4e89-b757-10e98c9078ea";
    public static final String SUB_ACTIVITY_TAG_ONE = "development";
    public static final String SUB_ACTIVITY_TAG_TWO = "testing";

    public static final String SUB_ACTIVITY_WIITH_PROJECTS_ONE = "X (B) Set up IDE +development super:283b6271-b513-4e89-b757-10e98c9078ea";
    public static final String SUB_ACTIVITY_WIITH_PROJECTS_TWO = "X (B) Read analisis +development +testing super:283b6271-b513-4e89-b757-10e98c9078ea";
    public static final String SUB_ACTIVITY_PROJECT_ONE = "development";
    public static final String SUB_ACTIVITY_PROJECT_TWO = "testing";

    public static final String ACTIVITY_NO_PREFIX_LOCATION_LINE = "Write my own todo.txt webapp +imnu +java +programming @development loc:TestLocation uuid:283b6298-b513-4e89-b757-10e98c9078ea";
    public static final String ACTIVITY_NO_PREFIX_LOCATION_SPACE_LINE = "Write my own todo.txt webapp +imnu +java +programming @development loc:Test Location uuid:283b6298-b513-4e89-b757-10e98c9078ea";
}
