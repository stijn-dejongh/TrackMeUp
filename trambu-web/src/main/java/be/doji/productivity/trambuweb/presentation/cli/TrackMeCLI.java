package be.doji.productivity.trambuweb.presentation.cli;

import be.doji.productivity.trackme.managers.ActivityManager;
import be.doji.productivity.trackme.model.tasks.Activity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Doji on 22/10/2017.
 */
public class TrackMeCLI implements CommandLineRunner {

    private static final String TODO_FILE_LOCATION = "data/todo.txt";

    @Override public void run(String... strings) throws Exception {
        System.out.println("Welcome to trackme - CLI");
        ActivityManager am = new ActivityManager(getPathInProject(TODO_FILE_LOCATION));
        am.readActivitiesFromFile();

        Scanner reader = new Scanner(System.in);
        while (true) {
            System.out.print("$: ");
            String input = reader.nextLine();
            String command = input.split(" ")[0];
            switch (command) {
                case "add":
                    am.addActivity(input.replaceFirst("add ", ""));
                    break;
                case "list":
                    List<Activity> activities = am.getActivities();
                    for (Activity activity : activities) {
                        System.out.println(activity.toString());
                    }
                    System.out.println("--------");
                    break;
                case "exit":
                    System.exit(0);
                    break;
                default:
                    System.out.println("Command not recongnised: [" + input + "]");
                    break;
            }
        }

    }

    public String getPathInProject(String path) throws FileNotFoundException {
        File testFile = ResourceUtils.getFile(getClass().getClassLoader().getResource(path));
        String testPath = testFile.getAbsolutePath();
        return testPath;
    }
}
