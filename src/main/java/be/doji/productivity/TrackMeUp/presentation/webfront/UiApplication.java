package be.doji.productivity.TrackMeUp.presentation.webfront;

import be.doji.productivity.TrackMeUp.managers.ActivityManager;
import be.doji.productivity.TrackMeUp.model.tasks.Activity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Doji on 23/10/2017.
 */
@SpringBootApplication @RestController public class UiApplication {

    private static final String TODO_FILE_LOCATION = "data/todo.txt";

    @RequestMapping("/getActivities") public List<Activity> getActivities() throws IOException, ParseException {
        ActivityManager am = new ActivityManager(getPathInProject(TODO_FILE_LOCATION));
        am.readActivitiesFromFile();
        return am.getActivities();
    }

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

    public String getPathInProject(String path) throws FileNotFoundException {
        File testFile = ResourceUtils.getFile(getClass().getClassLoader().getResource(path));
        String testPath = testFile.getAbsolutePath();
        return testPath;
    }

}
