package be.doji.productivity.trambucore.model.tasks;

import be.doji.productivity.trambucore.TrackMeConstants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
    private Date completionDate;
    private List<Activity> subActivities = new ArrayList<>();
    private boolean completed = false;
    private List<String> tags = new ArrayList<>();
    private List<String> projects = new ArrayList<>();
    private Date deadline;
    private Duration warningTimeFrame = TrackMeConstants.DEFAULT_WARNING_PERIOD;
    private String parentActivity;
    private String location;

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

    public Date getCompletionDate() {
        return this.completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public List<Activity> getSubActivities() {
        return new ArrayList<>(subActivities);
    }

    public void setSubActivities(List<Activity> subActivities) {
        this.subActivities = subActivities;
    }

    public void addSubTask(Activity subTask) {
        this.subActivities.add(subTask);
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isAllSubActivitiesCompleted() {
        boolean allCompeted = true;
        for (Activity sub : this.subActivities) {
            allCompeted = allCompeted && sub.isCompleted();
        }
        return allCompeted;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isSetLocation() {
        return StringUtils.isNotBlank(this.location);
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
            sb.append(TrackMeConstants.getDateFormat().format(deadline));
            sb.append(" ");
        }

        if (this.warningTimeFrame != null) {
            sb.append(TrackMeConstants.INDICATOR_WARNING_PERIOD);
            sb.append(warningTimeFrame.toString());
            sb.append(" ");
        }
        if (isSetLocation()) {
            sb.append(TrackMeConstants.INDICATOR_LOCATION);
            sb.append(this.location);
            sb.append(" ");
        }

        if (StringUtils.isNotBlank(this.parentActivity)) {
            sb.append(TrackMeConstants.INDICATOR_PARENT_ACTIVITY);
            sb.append(parentActivity);
            sb.append(" ");
        }

        sb.append(TrackMeConstants.INDICATOR_UUID);
        sb.append(this.id.toString());
        sb.append(" ");

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

    public void setParentActivity(String parentActivity) {
        this.parentActivity = parentActivity;
    }

    public String getParentActivity() {
        return parentActivity;
    }

    public void removeSubActivity(Activity activityToDelete) {
        this.subActivities.removeIf(subActivity -> subActivity.getId().equals(activityToDelete.getId()));
    }

    public void setId(String uuidString) {
        this.id = UUID.fromString(uuidString);
    }

    public boolean isSetDeadline() {
        return this.deadline != null;
    }

    public boolean isAlertActive() {
        if (isSetDeadline()) {
            return ((this.deadline.getTime() - new Date().getTime()) / 1000) < this.warningTimeFrame.getSeconds();
        } else {
            return false;
        }

    }

    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

}
