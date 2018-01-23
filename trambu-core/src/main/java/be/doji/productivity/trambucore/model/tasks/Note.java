package be.doji.productivity.trambucore.model.tasks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Note {

    private Path location;
    private List<String> content = new ArrayList<>();
    private UUID activityId;

    public Note(UUID activityId, Path location) {
        this.activityId = activityId;
        this.location = location;
    }

    public Note(String activityId, Path path) {
        this(UUID.fromString(activityId), path);
    }

    public List<String> readContent() throws IOException {
        if (this.location == null || !location.toFile().exists()) {
            throw new IOException("Notes directory not found");
        }

        this.content = Files.readAllLines(location);
        return content;
    }

    public List<String> getContent() throws IOException {
        if (content.isEmpty()) {
            return this.readContent();
        }
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public void save() throws IOException {
        Files.write(location, content);
    }

    public UUID getActivityId() {
        return activityId;
    }

    public Path getLocation() {
        return this.location;
    }
}
