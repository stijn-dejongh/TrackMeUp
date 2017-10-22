package be.doji.productivity.TrackMeUp.model.tasks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Doji on 22/10/2017.
 */
public class Activity {

    private String name;
    private String priority;
    private final Date creationDate = new Date();
    private Date completionDate;
    private List<Activity> subTasks = new ArrayList<>();
    private boolean completed;
    private List<String> tags = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();

    public Activity() {
        this("New Activity");
    }

    public Activity(String taskName) {
        this.name = taskName;
        this.priority = PriorityConstants.PRIORITY_MEDIUM;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Date getCreationDate() {
        return new Date(creationDate.getTime());
    }

    public Date getCompletionDate() {
        return new Date(completionDate.getTime());
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = new Date(completionDate.getTime());
    }

    public List<Activity> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    public void addSubTask(Activity subTask) {
        this.subTasks.add(subTask);
    }

    public void addSubTask(int index, Activity subTask) {
        if (index >= 0 && index < getSubTasks().size()) {
            this.subTasks.add(index, subTask);
        } else {
            addSubTask(subTask);
        }
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public List<String> getTags() {
        return new ArrayList<>(tags);
    }

    public void addTags(List<String> tags) {
        this.tags.addAll(tags);
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public List<Project> getProjects() {
        return new ArrayList<>(projects);
    }

    public void addProject(Project project) {
        this.projects.add(project);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(this.getPriority()).append(")");
        sb.append(" ");
        sb.append(this.getName());

        //TODO: add other fields

        return  sb.toString();
    }
}
