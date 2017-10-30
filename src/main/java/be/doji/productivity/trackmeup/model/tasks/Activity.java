package be.doji.productivity.TrackMeUp.model.tasks;

import be.doji.productivity.TrackMeUp.TrackMeConstants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Doji on 22/10/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true) public class Activity {

    private UUID id;
    private String name;
    private String priority;
    private final LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime completionDate;
    private List<Activity> subTasks = new ArrayList<>();
    private boolean completed = false;
    private List<String> tags = new ArrayList<>();
    private List<String> projects = new ArrayList<>();
    private LocalDateTime deadline;
    private Duration warningTimeFrame = TrackMeConstants.DEFAULT_WARNING_PERIOD;

    public Activity() {
        this("New Activity");
    }

    public Activity(String taskName) {
        this.id = UUID.randomUUID();
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getCompletionDate() {
        return this.completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
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

    public List<String> getProjects() {
        return new ArrayList<>(projects);
    }

    public void addProject(String project) {
        this.projects.add(project);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.isCompleted()) {
            sb.append(TrackMeConstants.INDICATOR_DONE);
            sb.append(" ");
        }

        sb.append("(").append(this.getPriority()).append(")");
        sb.append(" ");
        sb.append(this.getName());
        sb.append(" ");
        for (String project : this.getProjects()) {
            sb.append(TrackMeConstants.INDICATOR_PROJECT).append(project);
            sb.append(" ");
        }

        for (String tag : this.getTags()) {
            sb.append(TrackMeConstants.INDICATOR_TAG).append(tag);
            sb.append(" ");
        }

        if (deadline != null) {
            sb.append(TrackMeConstants.INDICATOR_DEADLINE);
            sb.append(TrackMeConstants.DATA_DATE_FORMAT.format(deadline));
            sb.append(" ");
        }

        if (this.warningTimeFrame != null) {
            sb.append(TrackMeConstants.INDICATOR_WARNING_PERIOD);
            sb.append(warningTimeFrame.toString());
            sb.append(" ");
        }

        return sb.toString().trim();
    }

    public UUID getId() {
        return id;
    }

    public Duration getWarningTimeFrame() {
        return warningTimeFrame;
    }

    public void setWarningTimeFrame(Duration warningTimeFrame) {
        this.warningTimeFrame = warningTimeFrame;
    }
}
