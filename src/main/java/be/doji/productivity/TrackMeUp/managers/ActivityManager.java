package be.doji.productivity.TrackMeUp.managers;

import be.doji.productivity.TrackMeUp.TrackMeConstants;
import be.doji.productivity.TrackMeUp.model.tasks.Activity;
import be.doji.productivity.TrackMeUp.model.tasks.Project;
import be.doji.productivity.TrackMeUp.utils.TrackerUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.*;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Doji on 22/10/2017.
 */
public class ActivityManager {

    private final String DATE_REGEX = "[0-9\\-\\:\\.]*";
    private final String COMPLETED_REGEX = "^[Xx]";
    private final String PRIORITY_REGEX = "\\([a-zA-Z]\\)";
    private final String NAME_REGEX = "\\b[a-zA-Z]([\\w\\s\\.\\- && [^\\+]])*(\\s\\+|$|\\s\\@)";
    private final String TAG_REGEX = "\\@([a-zA-Z0-9]*)(\\s|$)";
    private final String PROJECT_REGEX = "\\+([a-zA-Z0-9]*)(\\s|$)";
    private final String DUE_DATE_REGEX = "due:" + DATE_REGEX + "\\s";

    private Map<Activity, Integer> activities = new ConcurrentHashMap<>();
    private Map<String, Project> projects = new HashMap<>();
    private Path todoFile;

    public ActivityManager(String fileLocation) {
        this.todoFile = Paths.get(fileLocation);
    }

    public void readActivitiesFromFile() throws IOException, ParseException {
        int lineNumber = 0;
        for (String line : Files.readAllLines(this.todoFile)) {
            activities.put(mapStringToActivity(line), lineNumber);
            lineNumber += 1;
        }
    }

    protected Activity mapStringToActivity(String line) throws ParseException {
        Activity activity = new Activity();
        List<String> matchedCompleted = TrackerUtils.findAllMatches(COMPLETED_REGEX, line);
        if (!matchedCompleted.isEmpty()) {
            activity.setCompleted(true);
            line = line.replaceFirst(COMPLETED_REGEX + "\\s", "");
        }

        List<String> priorityMatches = TrackerUtils.findAllMatches(PRIORITY_REGEX, line);
        if (!priorityMatches.isEmpty()) {
            activity.setPriority(priorityMatches.get(0).replace("(", "").replace(")", "").trim());
        }

        List<String> nameMatches = TrackerUtils.findAllMatches(NAME_REGEX, line);
        if (!nameMatches.isEmpty()) {
            activity.setName(nameMatches.get(0).replace("+", "").replace("@", "").trim());
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

    public void addActivityAndSaveToFile(String activity) throws IOException, ParseException {
        addActivityAndSaveToFile(mapStringToActivity(activity));
    }

    public void addActivityAndSaveToFile(Activity activity) throws IOException {
        int lineNumber = Files.readAllLines(this.todoFile).size();
        this.activities.put(activity, lineNumber);
        Files.write(this.todoFile, (activity.toString() + System.lineSeparator()).getBytes(),
                StandardOpenOption.APPEND);

    }

    public List<Activity> getActivities() {
        ArrayList<Activity> activities = new ArrayList<>(this.activities.keySet());
        Collections.sort(activities, Comparator.comparing(Activity::getPriority));
        return activities;
    }

    public Activity save(Activity activity) throws IOException {
        Activity matchingActivity = null;
        for (Activity savedActivity : this.activities.keySet()) {
            if (savedActivity.getId().equals(activity.getId())) {
                matchingActivity = savedActivity;
                writeActivityToFile(activity, this.activities.get(savedActivity));
            }
        }
        if (matchingActivity == null && !containsActivityWithName(activity.getName())) {
            this.addActivityAndSaveToFile(activity);
            matchingActivity = activity;
        }

        return matchingActivity;
    }

    private boolean containsActivityWithName(String name) {
        for (Activity savedActivity : this.activities.keySet()) {
            if (StringUtils.equals(savedActivity.getName(), name)) {
                return true;
            }
        }
        return false;
    }

    private void writeActivityToFile(Activity activity, Integer lineNumber) throws IOException {
        System.out.println(">> Updating TODO.txt");
        backUpTodoFile();
        List<String> fileLines = Files.readAllLines(this.todoFile);
        Files.write(this.todoFile, new String().getBytes());
        for (int i = 0; i < fileLines.size(); i++) {
            String lineToWrite;
            if (i != lineNumber) {
                lineToWrite = fileLines.get(i);
            } else {
                lineToWrite = activity.toString();
            }
            Files.write(this.todoFile, (lineToWrite + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
        }
        System.out.println(">> TODO.txt was updated");
    }

    private void writeAllToFile() throws IOException, ParseException {
        System.out.println(">> Updating TODO.txt");
        backUpTodoFile();
        Files.write(this.todoFile, new String().getBytes());
        for (Activity activity : this.getActivities()) {
            Files.write(this.todoFile, (activity.toString() + System.lineSeparator()).getBytes(),
                    StandardOpenOption.APPEND);
        }
        System.out.println(">> TODO.txt was updated");
        this.readActivitiesFromFile();
    }

    public void delete(Activity activity) throws IOException, ParseException {
        Set<Activity> activityCopy = this.activities.keySet();
        for (Activity savedActivity : activityCopy) {
            if (savedActivity.getId().equals(activity.getId())) {
                this.activities.remove(savedActivity);
                writeAllToFile();
                return;
            }
        }
    }

    private void backUpTodoFile() throws IOException {
        Files.copy(this.todoFile, this.todoFile.resolveSibling(this.todoFile.getFileName() + "_BAK"),
                StandardCopyOption.REPLACE_EXISTING);
    }
}
