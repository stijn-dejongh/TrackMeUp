package be.doji.productivity.TrackMeUp.managers;

import be.doji.productivity.TrackMeUp.model.tasks.Activity;
import be.doji.productivity.TrackMeUp.parser.ActivityParser;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.*;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by Doji on 22/10/2017.
 */
public class ActivityManager {

    private Map<Activity, Integer> activities = new ConcurrentHashMap<>();
    private Path todoFile;

    public ActivityManager(String fileLocation) throws IOException {
        Path filePath = Paths.get(fileLocation);
        if (filePath.toFile().exists()) {
            this.todoFile = filePath;
        } else {
            this.todoFile = Files.createTempFile("todo", "txt");
        }
    }

    public void readActivitiesFromFile() throws IOException, ParseException {
        activities = new ConcurrentHashMap<>();
        int lineNumber = 0;
        for (String line : Files.readAllLines(this.todoFile)) {
            activities.put(ActivityParser.mapStringToActivity(line), lineNumber);
            lineNumber += 1;
        }
    }

    public void addActivityAndSaveToFile(String activity) throws IOException, ParseException {
        addActivityAndSaveToFile(ActivityParser.mapStringToActivity(activity));
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

    public List<Activity> getActivitiesByTag(String tag) {
        return this.getActivities().stream().filter(activity -> activity.getTags().contains(tag))
                .collect(Collectors.toList());
    }

    public List<Activity> getActivitiesByProject(String project) {

        return this.getActivities().stream().filter(activity -> !activity.getProjects().stream()
                .filter(project1 -> StringUtils.equalsIgnoreCase(project1, project)).collect(Collectors.toList())
                .isEmpty()).collect(Collectors.toList());
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

    private void writeAllToFileAndReload() throws IOException, ParseException {
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
                writeAllToFileAndReload();
                return;
            }
        }

    }

    private void backUpTodoFile() throws IOException {
        Files.copy(this.todoFile, this.todoFile.resolveSibling(this.todoFile.getFileName() + "_BAK"),
                StandardCopyOption.REPLACE_EXISTING);
    }

    public void updateTodoFileLocation(String location) throws IOException, ParseException {
        Path filePath = Paths.get(location);
        if (filePath.toFile().exists()) {
            this.todoFile = filePath;
        }
        this.readActivitiesFromFile();
    }
}
