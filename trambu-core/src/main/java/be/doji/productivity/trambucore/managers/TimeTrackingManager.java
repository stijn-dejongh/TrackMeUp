package be.doji.productivity.trambucore.managers;

import be.doji.productivity.trambucore.TrackMeConstants;
import be.doji.productivity.trambucore.model.tracker.ActivityLog;
import be.doji.productivity.trambucore.parser.TimeLogParser;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TimeTrackingManager {

    private List<ActivityLog> timelogs;
    private Path timelogFile;

    public TimeTrackingManager(String fileLocation) throws IOException {
        this.timelogs = new ArrayList<>();
        Path filePath = Paths.get(fileLocation);
        if (filePath.toFile().exists()) {
            this.timelogFile = filePath;
        } else {
            this.timelogFile = Files.createTempFile("timetracking", "txt");
        }
    }

    public void updateFileLocation(String location) throws IOException, ParseException {
        Path filePath = Paths.get(location);
        if (filePath.toFile().exists()) {
            this.timelogFile = filePath;
        }
        this.readLogs();
    }

    public ActivityLog getLogForActivityId(String activityId) {
        return getLogForActivityId(UUID.fromString(activityId));
    }

    public ActivityLog getLogForActivityId(UUID activityId) {
        for (ActivityLog log : timelogs) {
            if (log.getActivityId().equals(activityId)) {
                return log;
            }
        }
        ActivityLog activityLog = new ActivityLog(activityId);
        this.timelogs.add(activityLog);
        return activityLog;
    }

    public void writeLogs() throws IOException {
        Files.write(this.timelogFile, "".getBytes());
        for (ActivityLog log : this.timelogs) {
            Files.write(this.timelogFile, (log.toString() + System.lineSeparator()).getBytes(),
                    StandardOpenOption.APPEND);
        }
    }

    public void readLogs() throws IOException, ParseException {
        List<String> fileLines = Files.readAllLines(this.timelogFile);
        ActivityLog readLog = null;
        for (String line : fileLines) {
            if (StringUtils.isNotBlank(line)) {
                if (StringUtils.containsIgnoreCase(line, TrackMeConstants.INDICATOR_LOG_START)) {
                    readLog = new ActivityLog(getActivityIdFromLine(line));
                } else if (StringUtils.containsIgnoreCase(line, TrackMeConstants.INDICATOR_LOG_END)) {
                    this.timelogs.add(readLog);
                } else if (readLog != null) {
                    readLog.addLogPoint(TimeLogParser.parseToTimeLog(line));
                }
            }
        }
    }

    private UUID getActivityIdFromLine(String line) {
        String uuidString = line.replace(TrackMeConstants.INDICATOR_LOG_START, "");
        uuidString = uuidString.trim();
        return UUID.fromString(uuidString);
    }
}
