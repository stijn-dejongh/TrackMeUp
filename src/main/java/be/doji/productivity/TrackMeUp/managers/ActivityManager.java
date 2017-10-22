package be.doji.productivity.TrackMeUp.managers;

import be.doji.productivity.TrackMeUp.TrackMeConstants;
import be.doji.productivity.TrackMeUp.model.tasks.Activity;
import be.doji.productivity.TrackMeUp.model.tasks.Project;
import be.doji.productivity.TrackMeUp.utils.TrackerUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Doji on 22/10/2017.
 */
public class ActivityManager {

    private final String COMPLETED_REGEX = "^[Xx]";
    private final String PRIORITY_REGEX = "\\([a-zA-Z]\\)";
    private final String NAME_REGEX = "\\b([a-zA-Z\\s][a-zA-Z0-9\\s]*)\\s\\+";
    private final String TAG_REGEX = "\\@([a-zA-Z0-9]*)\\s";
    private final String PROJECT_REGEX = "\\+([a-zA-Z0-9]*)\\s";
    private final String DUE_DATE_REGEX = "due:[0-9\\-\\:\\.]*\\s";

    private List<Activity> activities = new ArrayList<>();
    private Map<String, Project> projects = new HashMap<>();
    private Path todoFile;

    public ActivityManager(String fileLocation) {
        this.todoFile = Paths.get(fileLocation);
    }

    public void readActivitiesFromFile() throws IOException, ParseException {
        for (String line : Files.readAllLines(this.todoFile)) {
            activities.add(mapStringToActivity(line));
        }
    }

    protected Activity mapStringToActivity(String line) throws ParseException {
        Activity activity = new Activity();
        List<String> matchedCompleted = TrackerUtils.findAllMatches(COMPLETED_REGEX, line);
        if (!matchedCompleted.isEmpty()) {
            activity.setCompleted(true);
        }

        List<String> nameMatches = TrackerUtils.findAllMatches(NAME_REGEX, line);
        if (!nameMatches.isEmpty()) {
            activity.setName(nameMatches.get(0).replace("+", "").trim());
        }

        List<String> priorityMatches = TrackerUtils.findAllMatches(PRIORITY_REGEX, line);
        if (!priorityMatches.isEmpty()) {
            activity.setPriority(priorityMatches.get(0).replace("(", "").replace(")", "").trim());
        }

        List<String> tagMatches = TrackerUtils.findAllMatches(TAG_REGEX, line);
        for (String tag : tagMatches) {
            activity.addTag(tag.replace("@", "").trim());
        }

        List<String> projectMatches = TrackerUtils.findAllMatches(PROJECT_REGEX, line);
        for (String projectMatch : projectMatches) {
            String projectName = projectMatch.replace("+", "").trim();
            activity.addProject(getProjectForName(projectName));
        }

        List<String> dueDateMatches = TrackerUtils.findAllMatches(DUE_DATE_REGEX, line);
        for (String dueDateMatch : dueDateMatches) {
            String dueDateString = dueDateMatch.replace("due:", "").trim();
            activity.setDeadline(TrackMeConstants.DATA_DATE_FORMAT.parse(dueDateString));
        }

        return activity;
    }

    private Project getProjectForName(String projectName) {
        Project project;
        if (this.projects.containsKey(projectName)) {
            project = this.projects.get(projectName);
        } else {
            project = new Project(projectName);
            this.projects.put(projectName, project);
        }
        return project;
    }

    public void addActivityToFile(String activity) throws IOException, ParseException {
        addActivityToFile(mapStringToActivity(activity));
    }

    public void addActivityToFile(Activity activity) throws IOException {
        this.activities.add(activity);
        Files.write(this.todoFile, (activity.toString() + System.lineSeparator()).getBytes(),
                StandardOpenOption.APPEND);

    }

    public List<Activity> getActivities() {
        return new ArrayList<>(this.activities);
    }
}
