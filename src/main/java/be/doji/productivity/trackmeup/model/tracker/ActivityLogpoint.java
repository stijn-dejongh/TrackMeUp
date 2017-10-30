package be.doji.productivity.TrackMeUp.model.tracker;

import be.doji.productivity.TrackMeUp.TrackMeConstants;
import be.doji.productivity.TrackMeUp.model.tasks.Activity;

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
        return logLine.toString();
    }

}
