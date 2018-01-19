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

    public String getTimeSpentInHoursString() {
        double hours = getTimeSpentInHours();
        return String.valueOf(hours) + " hours";
    }

    public double getTimeSpentInHours() {
        double timeSpentInMilies = getTimeSpentInMilies();
        double timeSpentInHours = (timeSpentInMilies / (1000 * 60 * 60));
        return TrackerUtils.roundToSignificantNumbers(timeSpentInHours, 2);
    }

    public String getTimeSpentInMinutesString() {
        double minutes = getTimeSpentInMinutes();
        return String.valueOf(minutes) + " minutes";
    }

    public double getTimeSpentInMinutes() {
        double timeSpentInMilies = getTimeSpentInMilies();
        double timeSpentInMinutes = (timeSpentInMilies / (1000 * 60));
        return TrackerUtils.roundToSignificantNumbers(timeSpentInMinutes, 2);
    }

    public String getTimeSpentInSecondsString() {
        double seconds = getTimeSpentInSeconds();
        return String.valueOf(seconds) + " seconds";
    }

    public double getTimeSpentInSeconds() {
        double timeSpentInMilies = getTimeSpentInMilies();
        double timeSpentInSeconds = (timeSpentInMilies / (1000));
        return TrackerUtils.roundToSignificantNumbers(timeSpentInSeconds, 2);
    }

    private double getTimeSpentInMilies() {
        double timeSpentInMilies = 0;
        for (TimeLog log : this.logpoints) {
            if (log.isActive()) {
                timeSpentInMilies += new Date().getTime() - log.getStartTime().getTime();
            } else {
                timeSpentInMilies += log.getEndTime().getTime() - log.getStartTime().getTime();
            }
        }
        return timeSpentInMilies;
    }

    public List<TimeLog> getTimeLogsInInterval(Date intervalStartTime, Date intervalEndTime) {
        List<TimeLog> logsInInterval = new ArrayList<>();
        for (TimeLog log : this.logpoints) {
            if (isInInterval(log.getStartTime(), intervalStartTime, intervalEndTime) || (log.getEndTime() != null
                    && isInInterval(log.getEndTime(), intervalStartTime, intervalEndTime))) {
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
            if (isInInterval(log.getEndTime(), intervalStartTime, intervalEndTime)) {
                timeLogInInterval.setEndTime(log.getEndTime());
            } else {
                timeLogInInterval.setEndTime(getLogEndtimeReplacement(intervalEndTime));
            }
        } else {
            timeLogInInterval.setEndTime(getLogEndtimeReplacement(intervalEndTime));
        }
        return timeLogInInterval;
    }

    private Date getLogEndtimeReplacement(Date intervalEndTime) {
        Date today = new Date();
        return today.before(intervalEndTime)?today:intervalEndTime;
    }

    private boolean isInInterval(Date dateToCheck, Date intervalStartTime, Date intervalEndTime) {
        return dateToCheck.compareTo(intervalStartTime) >= 0 && dateToCheck.compareTo(intervalEndTime) <= 0;
    }
}
