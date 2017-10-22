package main.java.be.doji.productivity.TrackMeUp.model.tracker;

import main.java.be.doji.productivity.trackmeup.TrackMeConstants;
import main.java.be.doji.productivity.TrackMeUp.model.tasks.Activity;

import java.util.Date;

/**
 * Created by Doji on 22/10/2017.
 */
public class ActivityLogpoint {

    private final Activity activity;
    private Date startTime;
    private Date endTime;

    public ActivityLogpoint(Activity activity) {
        this.activity = activity;
        this.startTime = new Date();
    }

    public void stop() {
        this.endTime = new Date();
    }

    public String toString() {
        StringBuilder logLine = new StringBuilder();
        logLine.append(activity.getName());
        logLine.append(" ");
        logLine.append(TrackMeConstants.DATA_DATE_FORMAT.format(startTime));
        logLine.append(" ");
        logLine.append(TrackMeConstants.DATA_DATE_FORMAT.format(endTime));
        return logLine.toString();
    }

}
