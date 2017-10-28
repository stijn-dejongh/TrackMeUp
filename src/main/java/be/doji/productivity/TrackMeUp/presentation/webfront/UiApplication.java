package be.doji.productivity.TrackMeUp.presentation.webfront;

import be.doji.productivity.TrackMeUp.managers.ActivityManager;
import be.doji.productivity.TrackMeUp.model.tasks.Activity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Doji on 23/10/2017.
 */
@SpringBootApplication @RestController
@JsonIgnoreProperties(ignoreUnknown = true)
public class UiApplication {

    private static final String TODO_FILE_LOCATION = "data/todo.txt";
    private ActivityManager am;

    @RequestMapping("/getActivities") public List<Activity> getActivities() throws IOException, ParseException {
        am = new ActivityManager(getPathInProject(TODO_FILE_LOCATION));
        am.readActivitiesFromFile();
        return am.getActivities();
    }

    @RequestMapping(value = { "/save" }, method = { RequestMethod.POST }) public @ResponseBody boolean saveActivity(
            @RequestBody Activity activity) throws IOException {
        System.out.println("Trying to save activity");
        am.save(activity);
        return true;
    }

    @RequestMapping(value = { "/delete" }, method = { RequestMethod.POST }) public @ResponseBody boolean deleteActivity(
            @RequestBody Activity activity) throws IOException, ParseException {
        System.out.println("Trying to delete activity");
        am.delete(activity);
        return true;
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
