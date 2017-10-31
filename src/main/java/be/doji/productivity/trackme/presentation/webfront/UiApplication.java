package be.doji.productivity.trackme.presentation.webfront;

import be.doji.productivity.trackme.managers.ActivityManager;
import be.doji.productivity.trackme.model.tasks.Activity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Doji on 23/10/2017.
 */
@SpringBootApplication @RestController @JsonIgnoreProperties(ignoreUnknown = true) public class UiApplication {

    private static final String TODO_FILE_LOCATION = "data/todo.txt";
    private ActivityManager am;

    @RequestMapping("/initialize") public boolean initialize() throws IOException, ParseException {
        if (am == null) {
            am = new ActivityManager(TODO_FILE_LOCATION);
            am.readActivitiesFromFile();
        }
        return true;
    }

    @RequestMapping("/getActivities") public List<Activity> getActivities() throws IOException, ParseException {
        if (am == null) {
            this.initialize();
        }
        return am.getActivities();
    }

    @RequestMapping(value = { "/getActivitiesByTag" }, method = {
            RequestMethod.POST }) public @ResponseBody List<Activity> getActivitiesByTag(@RequestBody String tag)
            throws IOException {
        System.out.println("Loading activities for tag: " + tag);
        return am.getActivitiesByTag(tag);

    }

    @RequestMapping(value = { "/updateFileLocation" }, method = {
            RequestMethod.POST }) public @ResponseBody boolean updateFileLocation(@RequestBody String location)
            throws IOException, ParseException {
        System.out.println("Setting todo file to: " + location);
        if (am == null) {
            am = new ActivityManager(location);
        }
        am.updateTodoFileLocation(location);
        return true;
    }

    @RequestMapping(value = { "/getActivitiesByProject" }, method = {
            RequestMethod.POST }) public @ResponseBody List<Activity> getActivitiesByProject(@RequestBody String tag)
            throws IOException {
        System.out.println("Loading activities for tag: " + tag);
        if (am != null) {
            return am.getActivitiesByProject(tag);
        } else {
            return new ArrayList<>();
        }
    }

    @RequestMapping(value = { "/save" }, method = { RequestMethod.POST }) public @ResponseBody boolean saveActivity(
            @RequestBody Activity activity) throws IOException {
        if (am != null && activity != null) {
            am.save(activity);
            return true;
        } else {
            return false;
        }

    }

    @RequestMapping(value = { "/delete" }, method = { RequestMethod.POST }) public @ResponseBody boolean deleteActivity(
            @RequestBody Activity activity) throws IOException, ParseException {
        if (am == null) {
            initialize();
        }

        if (activity != null) {
            am.delete(activity);
            return true;
        } else {
            return false;
        }
    }

    public String getPathInProject(String path) throws FileNotFoundException {
        File testFile = ResourceUtils.getFile(getClass().getClassLoader().getResource(path));
        String testPath = testFile.getAbsolutePath();
        return testPath;
    }

}
