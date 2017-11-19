package be.doji.productivity.trambucore.model.tracker;

import be.doji.productivity.trambucore.TrackMeConstants;

import java.util.Date;

public class TimeLog {

    private Date startTime;
    private Date endTime;
    private boolean isActive;

    public void start() {
        this.startTime = new Date();
        isActive = true;
    }

    public void stop() {
        this.endTime = new Date();
        isActive = false;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String toString() {
        StringBuilder logPointString = new StringBuilder();
        logPointString.append(TrackMeConstants.INDICATOR_LOGPOINT_START);
        logPointString.append(TrackMeConstants.getDateFormat().format(this.getStartTime()));
        logPointString.append(" ");
        if(this.getEndTime() != null) {
            logPointString.append(TrackMeConstants.INDICATOR_LOGPOINT_END);
            logPointString.append(TrackMeConstants.getDateFormat().format(this.getEndTime()));
        }
        return logPointString.toString();
    }
}
