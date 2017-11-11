package be.doji.productivity.trackme.model.tracker;

import be.doji.productivity.trackme.TrackMeConstants;
import be.doji.productivity.trackme.model.tasks.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
}
