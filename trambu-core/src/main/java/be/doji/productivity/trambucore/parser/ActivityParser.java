package be.doji.productivity.trambucore.parser;

import be.doji.productivity.trambucore.TrackMeConstants;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.utils.TrackerUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.time.Duration;
import java.util.List;

/**
 * Created by Doji on 30/10/2017.
 */
public final class ActivityParser {

    private static final String REGEX_TERMINATOR = "(\\s|$)";
    public static final String REGEX_UUID = "([a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12})";
    private static final String COMPLETED_REGEX =
            "^[" + TrackMeConstants.INDICATOR_DONE + StringUtils.upperCase(TrackMeConstants.INDICATOR_DONE) + "]";
    private static final String PRIORITY_REGEX = "\\([a-zA-Z]\\)";
    private static final String NAME_REGEX =
            "\\b[a-zA-Z]([\\w\\s\\.\\- && [^\\+]])*(\\s\\+|$|\\s\\@|\\s" + TrackMeConstants.INDICATOR_WARNING_PERIOD
                    + "|\\s" + TrackMeConstants.INDICATOR_DEADLINE + "|\\s" + TrackMeConstants.INDICATOR_PARENT_ACTIVITY
                    + "|\\s" + TrackMeConstants.INDICATOR_UUID + ")";
    private static final String TAG_REGEX = "\\" + TrackMeConstants.INDICATOR_TAG + "([a-zA-Z0-9]*)" + REGEX_TERMINATOR;
    private static final String PROJECT_REGEX =
            "\\" + TrackMeConstants.INDICATOR_PROJECT + "([a-zA-Z0-9]*)" + REGEX_TERMINATOR;
    private static final String DUE_DATE_REGEX =
            TrackMeConstants.INDICATOR_DEADLINE + TrackMeConstants.REGEX_DATE + REGEX_TERMINATOR;
    private static final String DURATION_REGEX = "P((0-9|.)+(T)*(D|H|M|S))*";

    private static final String WARNING_PERIOD_REGEX =
            TrackMeConstants.INDICATOR_WARNING_PERIOD + DURATION_REGEX + REGEX_TERMINATOR;
    private static final String LOCATION_REGEX =
            TrackMeConstants.INDICATOR_LOCATION + "([a-zA-Z0-9\\s]*)" + REGEX_TERMINATOR;

    private static final String SUPER_ACTIVITY_REGEX =
            TrackMeConstants.INDICATOR_PARENT_ACTIVITY + REGEX_UUID + "(\\s\\+|$|\\s\\@|\\s"
                    + TrackMeConstants.INDICATOR_WARNING_PERIOD + "|\\s" + TrackMeConstants.INDICATOR_DEADLINE + "|\\s"
                    + TrackMeConstants.INDICATOR_UUID + "|" + REGEX_TERMINATOR + ")";
    private static final String REGEX_ID = TrackMeConstants.INDICATOR_UUID + REGEX_UUID + REGEX_TERMINATOR;

    /**
     * Utility classes should not have a public or default constructor
     */
    private ActivityParser() {
    }

    public static Activity mapStringToActivity(String line) throws ParseException {
        Activity activity = new Activity();
        List<String> matchedCompleted = TrackerUtils.findAllMatches(COMPLETED_REGEX, line);
        if (!matchedCompleted.isEmpty()) {
            activity.setCompleted(true);
            line = line.replaceFirst(COMPLETED_REGEX + "\\s", "");
        }

        List<String> priorityMatches = TrackerUtils.findAllMatches(PRIORITY_REGEX, line);
        if (!priorityMatches.isEmpty()) {
            activity.setPriority(priorityMatches.get(0).replace("(", "").replace(")", "").trim());
        }

        List<String> nameMatches = TrackerUtils.findAllMatches(NAME_REGEX, line);
        if (!nameMatches.isEmpty()) {
            activity.setName(nameMatches.get(0).replace(TrackMeConstants.INDICATOR_PROJECT, "")
                    .replace(TrackMeConstants.INDICATOR_TAG, "").replace(TrackMeConstants.INDICATOR_DEADLINE, "")
                    .replace(TrackMeConstants.INDICATOR_WARNING_PERIOD, "")
                    .replace(TrackMeConstants.INDICATOR_PARENT_ACTIVITY, "")
                    .replace(TrackMeConstants.INDICATOR_UUID, "").trim());
        }

        List<String> tagMatches = TrackerUtils.findAllMatches(TAG_REGEX, line);
        for (String tag : tagMatches) {
            activity.addTag(tag.replace(TrackMeConstants.INDICATOR_TAG, "").trim());
        }

        List<String> projectMatches = TrackerUtils.findAllMatches(PROJECT_REGEX, line);
        for (String projectMatch : projectMatches) {
            String projectName = projectMatch.replace(TrackMeConstants.INDICATOR_PROJECT, "").trim();
            activity.addProject(projectName);
        }

        List<String> dueDateMatches = TrackerUtils.findAllMatches(DUE_DATE_REGEX, line);
        for (String dueDateMatch : dueDateMatches) {
            String dueDateString = dueDateMatch.replace(TrackMeConstants.INDICATOR_DEADLINE, "").trim();
            activity.setDeadline(TrackMeConstants.getDateFormat().parse(dueDateString));
        }

        List<String> warningPeriodMatches = TrackerUtils.findAllMatches(WARNING_PERIOD_REGEX, line);
        for (String warningMatch : warningPeriodMatches) {
            String warningMatchString = warningMatch.replace(TrackMeConstants.INDICATOR_WARNING_PERIOD, "").trim();
            activity.setWarningTimeFrame(Duration.parse(warningMatchString));
        }

        List<String> locationMatches = TrackerUtils.findAllMatches(LOCATION_REGEX, line);
        for (String locationMatch : locationMatches) {
            String locationMatchString = locationMatch.replace(TrackMeConstants.INDICATOR_LOCATION, "").trim();
            activity.setLocation(locationMatchString);
        }

        List<String> superActivityMatches = TrackerUtils.findAllMatches(SUPER_ACTIVITY_REGEX, line);
        for (String activityMatch : superActivityMatches) {
            String superActivityString = activityMatch.replace(TrackMeConstants.INDICATOR_PARENT_ACTIVITY, "")
                    .replace(TrackMeConstants.INDICATOR_UUID, "").trim();
            activity.setParentActivity(superActivityString);
        }

        List<String> uuidMatches = TrackerUtils.findAllMatches(REGEX_ID, line);
        for (String uuidMatch : uuidMatches) {
            String uuidString = uuidMatch.replace(TrackMeConstants.INDICATOR_UUID, "").trim();
            activity.setId(uuidString);
        }

        return activity;
    }
}
