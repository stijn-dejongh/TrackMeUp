package be.doji.productivity.trambucore.exporters;

import be.doji.productivity.trambucore.exporters.util.ExportConstants;
import be.doji.productivity.trambucore.managers.ActivityManager;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.model.tracker.ActivityLog;
import be.doji.productivity.trambucore.utils.TrackerUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Stijn Dejongh
 */
public class TimesheetToCSVExporter implements Exporter<List<ActivityLog>, List<String>> {

    private final ActivityManager activityManager;

    public TimesheetToCSVExporter(ActivityManager activityManager) {
        this.activityManager = activityManager;
    }

    @Override public List<String> convert(List<ActivityLog> input) throws IOException {
        return createFileLines(input);
    }

    List<String> createFileLines(List<ActivityLog> input) {
        List<String> exportedLines = new ArrayList<>();
        exportedLines.add(createHeaderLine());
        for (ActivityLog timeLog : input) {
            exportedLines.add(createItemLine(timeLog));
        }
        return exportedLines;
    }

    private String createItemLine(ActivityLog timeLog) {
        Optional<Activity> savedActivityById = activityManager.getSavedActivityById(timeLog.getActivityId().toString());
        String activityName = savedActivityById.isPresent()?
                savedActivityById.get().getName():
                timeLog.getActivityId().toString();

        StringBuilder csvLine = new StringBuilder();
        csvLine.append(activityName);
        csvLine.append(ExportConstants.CSV_ITEM_SEPERATOR);
        csvLine.append(TrackerUtils.escape(String.valueOf(timeLog.getTimeSpentInHours())));
        csvLine.append(ExportConstants.CSV_ITEM_SEPERATOR);
        csvLine.append(TrackerUtils.escape(String.valueOf(timeLog.getTimeSpentInMinutes())));
        csvLine.append(ExportConstants.CSV_ITEM_SEPERATOR);
        csvLine.append(TrackerUtils.escape(String.valueOf(timeLog.getTimeSpentInSeconds())));
        csvLine.append(ExportConstants.CSV_ITEM_SEPERATOR);
        Optional<Activity> parentActivity = savedActivityById
                .map(activity -> activityManager.getSavedActivityById(activity.getParentActivity()))
                .orElse(Optional.empty());
        String parentActivityString = parentActivity.isPresent()?
                parentActivity.get().getName():
                ExportConstants.CSV_ITEM_EMPTY_VALUE;

        csvLine.append(
                savedActivityById.map(activity -> parentActivityString).orElse(ExportConstants.CSV_ITEM_EMPTY_VALUE));
        return csvLine.toString();
    }

    private String createHeaderLine() {
        StringBuilder headerLine = new StringBuilder();
        headerLine.append("ACTIVITY_NAME");
        headerLine.append(ExportConstants.CSV_ITEM_SEPERATOR);
        headerLine.append("TIMESPENT_HOURS");
        headerLine.append(ExportConstants.CSV_ITEM_SEPERATOR);
        headerLine.append("TIMESPENT_MINUTES");
        headerLine.append(ExportConstants.CSV_ITEM_SEPERATOR);
        headerLine.append("TIMESPENT_SECONDS");
        headerLine.append(ExportConstants.CSV_ITEM_SEPERATOR);
        headerLine.append("PARENT");

        return headerLine.toString();
    }
}
