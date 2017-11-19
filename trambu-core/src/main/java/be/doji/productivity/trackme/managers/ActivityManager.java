package be.doji.productivity.trackme.managers;

import be.doji.productivity.trackme.TrackMeConstants;
import be.doji.productivity.trackme.model.tasks.Activity;
import be.doji.productivity.trackme.parser.ActivityParser;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.*;
import java.text.ParseException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Created by Doji on 22/10/2017.
 */
public class ActivityManager {

    private List<Activity> activities = new ArrayList<>();
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
        activities = new ArrayList<>();
        for (String line : Files.readAllLines(this.todoFile)) {
            if (StringUtils.isNotBlank(line)) {
                addActivity(line);
            }
        }
    }

    public void addActivity(String activity) throws ParseException {
        addActivity(ActivityParser.mapStringToActivity(activity));
    }

    private void addActivity(Activity activity) {
        String parentActivity = activity.getParentActivity();
        if (StringUtils.isNotBlank(parentActivity)) {
            Optional<Activity> parent = getSavedActivityById(parentActivity);
            if (parent.isPresent()) {
                parent.get().addSubTask(activity);
            } else {
                this.activities.add(activity);
            }
        } else {
            this.activities.add(activity);
        }
    }

    public List<Activity> getActivities() {
        ArrayList<Activity> savedActivities = new ArrayList<>(this.activities);
        savedActivities.sort((o1, o2) -> {
            int priorityCompare = o1.getPriority().compareTo(o2.getPriority());
            if (o1.getDeadline() != null && o2.getDeadline() != null) {
                return o1.getDeadline().compareTo(o2.getDeadline());
            } else if (o1.getDeadline() == null) {
                return o2.getDeadline() == null?priorityCompare:-1;
            } else {
                return o2.getDeadline() == null?1:priorityCompare;
            }
        });

        return savedActivities;
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

        Optional<Activity> savedActivity = getSavedActivityById(activity.getId().toString());

        savedActivity.ifPresent(activity1 -> this.activities.remove(activity1));
        this.addActivity(activity);
        writeAllToFileAndReload();

        Activity matchingActivity = null;
        for (Activity reloadedAct : this.activities) {
            if (reloadedAct.getName().equals(activity.getName())) {
                matchingActivity = reloadedAct;
            }
        }

        return matchingActivity;
    }

    public Optional<Activity> getSavedActivityById(String id) {
        return findActivityInList(id, this.activities, ((activity, s) -> activity.getId().equals(UUID.fromString(id))));
    }

    public Optional<Activity> getSavedActivityByName(String name) {
        return findActivityInList(name, this.activities, ((activity, s) -> StringUtils.equals(activity.getName(), s)));
    }

    private Optional<Activity> findActivityInList(String name, List<Activity> activities,
            BiFunction<Activity, String, Boolean> comparator) {
        for (Activity savedActivity : activities) {
            Optional<Activity> foundSub = findActivityInList(name, savedActivity.getSubActivities(), comparator);
            if (foundSub.isPresent()) {
                return foundSub;
            }

            if (comparator.apply(savedActivity, name)) {
                return Optional.of(savedActivity);
            }
        }
        return Optional.empty();
    }

    private void writeAllToFileAndReload() throws IOException, ParseException {
        System.out.println(">> Updating TODO.txt");
        backUpTodoFile();
        Files.write(this.todoFile, "".getBytes());
        for (Activity activity : this.getActivities()) {
            writeActivityToFile(activity);
        }
        System.out.println(">> TODO.txt was updated");
        this.readActivitiesFromFile();
    }

    private void writeActivityToFile(Activity activity) throws IOException {
        Files.write(this.todoFile, (activity.toString() + System.lineSeparator()).getBytes(),
                StandardOpenOption.APPEND);
        for (Activity subActivity : activity.getSubActivities()) {
            writeActivityToFile(subActivity);
        }
    }

    public void delete(Activity activity) throws IOException, ParseException {
        for (Iterator<Activity> it = this.activities.iterator(); it.hasNext(); ) {
            Activity savedActivity = it.next();
            if (savedActivity.getId().equals(activity.getId())) {
                it.remove();
                writeAllToFileAndReload();
                return;
            } else {
                if (deleteInSubactivities(savedActivity, activity.getId())) {
                    return;
                }
            }
        }

    }

    private boolean deleteInSubactivities(Activity parentActivity, UUID id) throws IOException, ParseException {
        for (Iterator<Activity> it = parentActivity.getSubActivities().iterator(); it.hasNext(); ) {
            Activity savedActivity = it.next();
            if (savedActivity.getId().equals(id)) {
                parentActivity.removeSubActivity(savedActivity);
                writeAllToFileAndReload();
                return true;
            } else {
                if (deleteInSubactivities(savedActivity, id)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void backUpTodoFile() throws IOException {
        Files.copy(this.todoFile, this.todoFile.resolveSibling(this.todoFile.getFileName() + "_BAK"),
                StandardCopyOption.REPLACE_EXISTING);
    }

    public void updateFileLocation(String location) throws IOException, ParseException {
        Path filePath = Paths.get(location);
        if (filePath.toFile().exists()) {
            this.todoFile = filePath;
        }
        this.readActivitiesFromFile();
    }

    public Map<Date, List<Activity>> getActivitiesWithDateHeader() {
        return groupByDate(this.getActivities());
    }

    private Map<Date, List<Activity>> groupByDate(List<Activity> activities) {
        Map<Date, List<Activity>> activitiesWithDateHeader = new TreeMap<>();
        for (Activity activity : activities) {
            Date deadline = activity.getDeadline();
            if (deadline == null) {
                deadline = TrackMeConstants.getDefaultDateHeader();
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

    public void addActivityAsSub(Activity toBeSub, Activity superActivity) {
        if (toBeSub.getId() == superActivity.getId() || getParentIds(superActivity).contains(toBeSub.getId())) {
            return;
        }

        Optional<Activity> savedToBeSub = getSavedActivityById(toBeSub.getId().toString());
        Optional<Activity> savedToBeSuper = getSavedActivityById(superActivity.getId().toString());
        if (savedToBeSuper.isPresent() && savedToBeSub.isPresent()) {
            savedToBeSub.get().setParentActivity(savedToBeSuper.get().getId().toString());
            savedToBeSuper.get().addSubTask(savedToBeSub.get());
            this.activities.remove(savedToBeSub.get());
        }
    }

    private List<UUID> getParentIds(Activity superActivity) {
        List<UUID> parents = new ArrayList<>();
        Activity activityToCheck = superActivity;
        while (StringUtils.isNotBlank(activityToCheck.getParentActivity())) {
            parents.add(activityToCheck.getId());
            Optional<Activity> savedActivityById = getSavedActivityById(activityToCheck.getParentActivity());
            if (savedActivityById.isPresent()) {
                activityToCheck = savedActivityById.get();
            }
        }
        return parents;
    }

    public List<String> getAllActivityNames() {
        return getRecursiveActivityNames(this.activities);
    }

    private List<String> getRecursiveActivityNames(List<Activity> activities) {
        ArrayList<String> names = new ArrayList<>();
        for (Activity activity : activities) {
            names.addAll(getRecursiveActivityNames(activity.getSubActivities()));
            names.add(activity.getName());
        }
        return names;
    }
}
