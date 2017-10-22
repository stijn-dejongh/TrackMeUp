package be.doji.productivity.TrackMeUp.managers;

import be.doji.productivity.TrackMeUp.model.tasks.Activity;
import be.doji.productivity.TrackMeUp.utils.TrackerUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Doji on 22/10/2017.
 */
public class ActivityManager {

    private final String COMPLETED_REGEX = "^[Xx]";
    private final String PRIORITY_REGEX = "\\([a-zA-Z]\\)";
    private final String NAME_REGEX = "\\b([a-zA-Z\\s]*)\\s\\+";

    private List<Activity> activities = new ArrayList<>();

    public void readActivitiesFromFile(String fileLocation) throws IOException {
        Path filePath = Paths.get(fileLocation);
        for(String line : Files.readAllLines(filePath)) {
            activities.add(mapStringToActivity(line));
        }
    }

    private Activity mapStringToActivity(String line) {
        Activity activity = new Activity();
        List<String> matchedCompleted = TrackerUtils.findAllMatches(COMPLETED_REGEX, line);
        if(!matchedCompleted.isEmpty()) {
            activity.setCompleted(true);
        }

        List<String> nameMatches = TrackerUtils.findAllMatches(NAME_REGEX, line);
        if(!nameMatches.isEmpty()) {
            activity.setName(nameMatches.get(0).replace("+", ""));
        }

        List<String> priorityMatches = TrackerUtils.findAllMatches(PRIORITY_REGEX, line);
        if(!priorityMatches.isEmpty()) {
            activity.setName(priorityMatches.get(0).replace("(", "").replace(")", ""));
        }

        return activity;
    }

    public List<Activity> getActivities() {
        return new ArrayList<>(this.activities);
    }
}
