package be.doji.productivity.trackme.managers;

import be.doji.productivity.trackme.TrackMeConstants;
import be.doji.productivity.trackme.model.tasks.Activity;
import be.doji.productivity.trackme.parser.ActivityParser;
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

    public void addActivity(String activity) throws IOException, ParseException {
        addActivity(ActivityParser.mapStringToActivity(activity));
    }

    public void addActivity(Activity activity) throws IOException {
        int lineNumber = Files.readAllLines(this.todoFile).size();
        this.activities.put(activity, lineNumber);
    }

    public List<Activity> getActivities() {
        ArrayList<Activity> activities = new ArrayList<>(this.activities.keySet());
        Collections.sort(activities, new Comparator<Activity>() {
            @Override public int compare(Activity o1, Activity o2) {
                if (o1.getDeadline() != null && o2.getDeadline() != null) {
                    return o1.getDeadline().compareTo(o2.getDeadline());
                } else {
                    int priorityCompare = o1.getPriority().compareTo(o2.getPriority());
                    return o1.getDeadline() == null?
                            o2.getDeadline() == null?priorityCompare:-1:
                            o2.getDeadline() == null?1:priorityCompare;
                }
            }
        });
        return activities;
    }

    public Map<Date, List<Activity>> getActivitiesByTag(String tag) {
        List<Activity> activitiesByTag = this.getActivities().stream()
                .filter(activity -> activity.getTags().contains(tag)).collect(Collectors.toList());
        return groupByDate(activitiesByTag);
    }

    public Map<Date, List<Activity>> getActivitiesByProject(String project) {

        List<Activity> activitiesByProject = this.getActivities().stream()
                .filter(activity -> !activity.getProjects().stream()
                        .filter(project1 -> StringUtils.equalsIgnoreCase(project1, project))
                        .collect(Collectors.toList()).isEmpty()).collect(Collectors.toList());
        return groupByDate(activitiesByProject);
    }

    public Activity save(Activity activity) throws IOException, ParseException {

        Optional<Activity> savedActivity = getSavedActivityByName(activity.getName());
        if (savedActivity.isPresent()) {
            this.activities.remove(savedActivity.get());

        }
        this.addActivity(activity);
        writeAllToFileAndReload();

        Activity matchingActivity = null;
        for (Activity reloadedAct : this.activities.keySet()) {
            if (reloadedAct.getName().equals(activity.getName())) {
                matchingActivity = reloadedAct;
            }
        }

        return matchingActivity;
    }

    private boolean containsActivityWithName(String name) {
        Optional<Activity> foundActivity = getSavedActivityByName(name);
        return foundActivity.isPresent();
    }

    private Optional<Activity> getSavedActivityByName(String name) {
        for (Activity savedActivity : this.activities.keySet()) {
            if (StringUtils.equals(savedActivity.getName(), name)) {
                return Optional.of(savedActivity);
            }
        }
        return Optional.empty();
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

    public Map<Date, List<Activity>> getActivitiesWithDateHeader() {
        List<Activity> activities = this.getActivities();
        Map<Date, List<Activity>> activitiesWithDateHeader = groupByDate(activities);

        return activitiesWithDateHeader;
    }

    private Map<Date, List<Activity>> groupByDate(List<Activity> activities) {
        Map<Date, List<Activity>> activitiesWithDateHeader = new TreeMap<>();
        for (Activity activity : activities) {
            Date deadline = activity.getDeadline();
            if(deadline == null) {
                deadline = TrackMeConstants.DEFAULT_DATE_HEADER;
            }
            if (activitiesWithDateHeader.containsKey(deadline)) {
                activitiesWithDateHeader.get(deadline).add(activity);
            } else {
                ArrayList<Activity> value = new ArrayList<>();
                value.add(activity);
                activitiesWithDateHeader.put(deadline, value);
            }
        }
        return activitiesWithDateHeader;
    }
}
