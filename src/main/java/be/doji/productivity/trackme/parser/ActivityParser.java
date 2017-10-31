package be.doji.productivity.trackme.parser;

import be.doji.productivity.trackme.TrackMeConstants;
import be.doji.productivity.trackme.model.tasks.Activity;
import be.doji.productivity.trackme.utils.TrackerUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Doji on 30/10/2017.
 */
public final class ActivityParser {

    private static final String DATE_REGEX = "[0-9\\-\\:\\.]*";
    private static final String COMPLETED_REGEX =
            "^[" + TrackMeConstants.INDICATOR_DONE + StringUtils.lowerCase(TrackMeConstants.INDICATOR_DONE) + "]";
    private static final String PRIORITY_REGEX = "\\([a-zA-Z]\\)";
    private static final String NAME_REGEX = "\\b[a-zA-Z]([\\w\\s\\.\\- && [^\\+]])*(\\s\\+|$|\\s\\@)";
    private static final String TAG_REGEX = "\\" + TrackMeConstants.INDICATOR_TAG + "([a-zA-Z0-9]*)(\\s|$)";
    private static final String PROJECT_REGEX = "\\" + TrackMeConstants.INDICATOR_PROJECT + "([a-zA-Z0-9]*)(\\s|$)";
    private static final String DUE_DATE_REGEX = TrackMeConstants.INDICATOR_DEADLINE + DATE_REGEX + "(\\s|$)";
    private static final String DURATION_REGEX = "P((0-9|.)+(T)*(D|H|M|S))*";
    private static final String WARNING_PERIOD_REGEX =
            TrackMeConstants.INDICATOR_WARNING_PERIOD + DURATION_REGEX + "(\\s|$)";

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
                    .replace(TrackMeConstants.INDICATOR_TAG, "").trim());
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
            activity.setDeadline(LocalDateTime.parse(dueDateString, TrackMeConstants.DATA_DATE_FORMAT));
        }

        List<String> warningPeriodMatches = TrackerUtils.findAllMatches(WARNING_PERIOD_REGEX, line);
        for (String warningMatch : warningPeriodMatches) {
            String warningMatchString = warningMatch.replace(TrackMeConstants.INDICATOR_WARNING_PERIOD, "").trim();
            activity.setWarningTimeFrame(Duration.parse(warningMatchString));
        }

        return activity;
    }
}
