package be.doji.productivity.trambucore.model.tracker;

import be.doji.productivity.trambucore.TrackMeConstants;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.utils.TrackerUtils;

import java.util.*;

/**
 * Created by Doji on 22/10/2017.
 */
public class ActivityLog {

    private UUID activityId;
    private List<TimeLog> logpoints = new ArrayList<>();

    public ActivityLog(Activity activity) {
        this.activityId = activity.getId();
    }

    public ActivityLog(UUID activityId) {
        this.activityId = activityId;
    }

    public UUID getActivityId() {
        return activityId;
    }

    public void setActivityId(UUID activityId) {
        this.activityId = activityId;
    }

    public List<TimeLog> getLogpoints() {
        return logpoints;
    }

    public void setLogpoints(List<TimeLog> logpoints) {
        this.logpoints = logpoints;
    }

    public String toString() {
        StringBuilder logLine = new StringBuilder();
        logLine.append(TrackMeConstants.INDICATOR_LOG_START);
        logLine.append(" ");
        logLine.append(activityId.toString());

        logLine.append(System.lineSeparator());
        for (TimeLog logPoint : logpoints) {
            logLine.append(logPoint.toString());
            logLine.append(System.lineSeparator());
        }
        logLine.append(TrackMeConstants.INDICATOR_LOG_END);
        return logLine.toString();
    }

    public void addLogPoint(TimeLog timeLog) {
        this.logpoints.add(timeLog);
    }

    public void startLog() {
        Optional<TimeLog> activeLog = getActiveLog();
        if (activeLog.isPresent()) {
            activeLog.get().stop();
        }
        TimeLog timeLog = new TimeLog();
        timeLog.start();
        this.addLogPoint(timeLog);
    }

    public Optional<TimeLog> getActiveLog() {
        for (TimeLog log : this.logpoints) {
            if (log.isActive()) {
                return Optional.of(log);
            }
        }
        return Optional.empty();
    }

    public void stopActiveLog() {
        Optional<TimeLog> activeLog = getActiveLog();
        if (activeLog.isPresent()) {
            activeLog.get().stop();
        }
    }

    public String getTimeSpent() {
        double timeSpentInMilies = 0;
        for (TimeLog log : this.logpoints) {
            if (log.isActive()) {
                timeSpentInMilies += new Date().getTime() - log.getStartTime().getTime();
            } else {
                timeSpentInMilies += log.getEndTime().getTime() - log.getStartTime().getTime();
            }
        }
        double timeSpentInHours = (timeSpentInMilies / (1000 * 60 * 60));

        return String.valueOf(TrackerUtils.roundToSignificantNumbers(timeSpentInHours, 2)) + " hours";
    }

    public List<TimeLog> getTimeLogsInInterval(Date intervalStartTime, Date intervalEndTime) {
        List<TimeLog> logsInInterval = new ArrayList<>();
        for (TimeLog log : this.logpoints) {
            if (isInInterval(log.getStartTime(), intervalStartTime, intervalEndTime) || isInInterval(log.getEndTime(),
                    intervalStartTime, intervalEndTime)) {
                TimeLog timeLogInInterval = getLogPartitionInInterval(intervalStartTime, intervalEndTime, log);
                logsInInterval.add(timeLogInInterval);
            }
        }
        return logsInInterval;
    }

    private TimeLog getLogPartitionInInterval(Date intervalStartTime, Date intervalEndTime, TimeLog log) {
        TimeLog timeLogInInterval = new TimeLog();
        timeLogInInterval.setStartTime(isInInterval(log.getStartTime(), intervalStartTime, intervalEndTime)?
                log.getStartTime():
                intervalStartTime);
        if (log.getEndTime() != null) {
            timeLogInInterval.setEndTime(isInInterval(log.getEndTime(), intervalStartTime, intervalEndTime)?
                    log.getEndTime():
                    intervalEndTime);
        } else {
            timeLogInInterval.setEndTime(intervalEndTime);
        }
        return timeLogInInterval;
    }

    private boolean isInInterval(Date startTime, Date intervalStartTime, Date intervalEndTime) {
        return startTime.compareTo(intervalStartTime) >= 0 && startTime.compareTo(intervalEndTime) <= 0;
    }
}
