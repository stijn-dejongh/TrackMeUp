package be.doji.productivity.trambucore.parser;

import be.doji.productivity.trambucore.TrackMeConstants;
import be.doji.productivity.trambucore.model.tracker.TimeLog;
import be.doji.productivity.trambucore.utils.TrackerUtils;

import java.text.ParseException;
import java.util.List;

public final class TimeLogParser {

    private static final String REGEX_START_DATETIME =
            TrackMeConstants.INDICATOR_LOGPOINT_START + TrackMeConstants.REGEX_DATE + "(\\s|$)";
    private static final String REGEX_END_DATETIME =
            TrackMeConstants.INDICATOR_LOGPOINT_END + TrackMeConstants.REGEX_DATE + "(\\s|$)";

    /**
     * Utility classes should not have a public or default constructor
     */
    private TimeLogParser() {
    }

    public static TimeLog parseToTimeLog(String line) throws ParseException {
        TimeLog timeLog = new TimeLog();

        List<String> matches = TrackerUtils.findAllMatches(REGEX_START_DATETIME, line);
        if (!matches.isEmpty()) {
            String source = matches.get(0);
            source = source.replace(TrackMeConstants.INDICATOR_LOGPOINT_START, "").trim();
            timeLog.setStartTime(TrackMeConstants.getDateFormat().parse(source));
            timeLog.setActive(true);
        }

        matches = TrackerUtils.findAllMatches(REGEX_END_DATETIME, line);
        if (!matches.isEmpty()) {
            String source = matches.get(0);
            source = source.replace(TrackMeConstants.INDICATOR_LOGPOINT_END, "").trim();
            timeLog.setEndTime(TrackMeConstants.getDateFormat().parse(source));
            timeLog.setActive(false);
        }

        return timeLog;
    }
}
