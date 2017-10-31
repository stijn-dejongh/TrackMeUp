package be.doji.productivity.trackme.model.tasks;

/**
 * Created by Doji on 22/10/2017.
 */
public class Project {

    private String name;

    public Project() {
        this("UnnamedProject");
    }

    public Project(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
