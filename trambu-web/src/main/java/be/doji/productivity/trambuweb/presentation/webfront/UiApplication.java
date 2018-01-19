package be.doji.productivity.trambuweb.presentation.webfront;

import be.doji.productivity.trackme.TrackMeConstants;
import be.doji.productivity.trackme.managers.ActivityManager;
import be.doji.productivity.trackme.managers.TimeTrackingManager;
import be.doji.productivity.trackme.model.tasks.Activity;
import be.doji.productivity.trackme.model.tracker.ActivityLog;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Doji on 23/10/2017.
 */
@Deprecated @SpringBootApplication @RestController @JsonIgnoreProperties(ignoreUnknown = true) public class UiApplication {

    private static final Logger LOG = LoggerFactory.getLogger(UiApplication.class);

    private ActivityManager am;
    private TimeTrackingManager tm;

    @RequestMapping("/initialize") public void initialize() throws IOException, ParseException {
        if (am == null) {
            am = new ActivityManager(TrackMeConstants.DEFAULT_TODO_FILE_LOCATION);
            am.readActivitiesFromFile();
        }
        if (tm == null) {
            tm = new TimeTrackingManager(TrackMeConstants.DEFAULT_TIMELOG_FILE_LOCATION);
            tm.readLogs();
        }
    }

    @RequestMapping("/getActivities") public List<Activity> getActivities() throws IOException, ParseException {
        if (am == null) {
            this.initialize();
        }

        return am.getActivities();
    }

    @RequestMapping(value = { "/getActivitiesByTag" }, method = {
            RequestMethod.POST }) public @ResponseBody Map<Date, List<Activity>> getActivitiesByTag(
            @RequestBody String tag) {
        LOG.debug("Loading activitiesWithHeader for tag: {0}", tag);
        return am.getActivitiesByTag(tag);

    }

    @RequestMapping(value = { "/updateFileLocation" }, method = {
            RequestMethod.POST }) public @ResponseBody boolean updateFileLocation(@RequestBody String location)
            throws IOException, ParseException {
        LOG.info("Setting todo file to: {0}", location);
        if (am == null) {
            am = new ActivityManager(location);
        }
        am.updateFileLocation(location);
        return true;
    }

    @RequestMapping(value = { "/updateLogFileLocation" }, method = {
            RequestMethod.POST }) public @ResponseBody boolean updateLogFileLocation(@RequestBody String location)
            throws IOException, ParseException {
        LOG.info("Setting todo file to: {0}", location);
        if (tm == null) {
            tm = new TimeTrackingManager(location);
        } else {
            tm.updateFileLocation(location);
        }

        return true;
    }

    @RequestMapping(value = { "/getActivitiesByProject" }, method = {
            RequestMethod.POST }) public @ResponseBody Map<Date, List<Activity>> getActivitiesByProject(
            @RequestBody String tag) {
        LOG.debug("Loading activitiesWithHeader for tag: {0}", tag);
        if (am != null) {
            return am.getActivitiesByProject(tag);
        } else {
            return new HashMap<>();
        }
    }

    @RequestMapping(value = { "/getActivitiesWithDateHeader" }, method = {
            RequestMethod.GET }) public @ResponseBody Map<Date, List<Activity>> getActivitiesWithDateHeader() {
        if (am != null) {
            return am.getActivitiesWithDateHeader();
        } else {
            return new HashMap<>();
        }
    }

    @RequestMapping(value = { "/save" }, method = { RequestMethod.POST }) public @ResponseBody boolean saveActivity(
            @RequestBody Activity activity) throws IOException, ParseException {
        LOG.debug("Saving activity");
        if (am != null && activity != null) {
            am.save(activity);
            return true;
        } else {
            return false;
        }

    }

    @RequestMapping(value = { "/delete" }, method = { RequestMethod.POST }) public @ResponseBody boolean deleteActivity(
            @RequestBody Activity activity) throws IOException, ParseException {
        LOG.debug("Deleting Activity!");
        if (am == null) {
            initialize();
        }

        if (activity != null) {
            am.delete(activity);
            return true;
        } else {
            return false;
        }
    }

    @RequestMapping(value = { "/startTimeLog" }, method = { RequestMethod.POST }) public void startTimeLog(
            @RequestBody String activityID) throws IOException {
        LOG.debug("Start timelog");
        ActivityLog timeLogForActivity = getTimeLogForActivity(activityID);
        timeLogForActivity.startLog();
        tm.writeLogs();
    }

    @RequestMapping(value = { "/stopTimeLog" }, method = { RequestMethod.POST }) public void stopTimeLog(
            @RequestBody String activityID) throws IOException {
        LOG.debug("Start timelog");
        ActivityLog timeLogForActivity = getTimeLogForActivity(activityID);
        timeLogForActivity.stopActiveLog();
        tm.writeLogs();
    }

    @RequestMapping(value = { "/getTimeLogForActivity" }, method = {
            RequestMethod.POST }) public @ResponseBody ActivityLog getTimeLogForActivity(
            @RequestBody String activityId) {
        LOG.debug("Loading timelog for activity");
        if (tm != null) {
            return tm.getLogForActivityId(activityId);
        } else {
            return null;
        }
    }

}
