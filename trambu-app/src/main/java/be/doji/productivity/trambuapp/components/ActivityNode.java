package be.doji.productivity.trambuapp.components;

import be.doji.productivity.trackme.model.tasks.Activity;
import be.doji.productivity.trambuapp.presentation.TrambuApplication;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.text.DateFormat;
import java.util.stream.Collectors;

public class ActivityNode extends TitledPane {

    private TrambuApplication application;

    public ActivityNode(Activity activity, TrambuApplication trambuApplication) {
        super();
        this.application = trambuApplication;
        this.setText(activity.getName());
        Button titleLabel = AwesomeDude
                .createIconButton(activity.isCompleted()?AwesomeIcon.CHECK_SIGN:AwesomeIcon.CHECK_EMPTY);
        this.setGraphic(titleLabel);
        this.getStyleClass().clear();
        this.getStyleClass().add(activity.isCompleted()?"done":"todo");
        this.setContent(createActivityContent(activity));
        this.setVisible(true);
    }

    private GridPane createActivityContent(Activity activity) {
        GridPane content = new GridPane();
        content.setVgap(4);
        content.setPadding(new Insets(5, 5, 5, 5));
        if (activity.isSetDeadline()) {
            content.add(new Label("Deadline: "), 0, 0);
            content.add(new Label(DateFormat.getDateInstance(DateFormat.DEFAULT).format(activity.getDeadline())), 1, 0);
        }

        HBox tags = new HBox(5);
        tags.getChildren().addAll(activity.getTags().stream().map(tag -> {
            Button button = new Button(tag);
            button.setOnAction(e -> application.updateActivities(tag));
            return button;
        }).collect(Collectors.toList()));
        content.add(new Label("Tags: "), 0, 1);
        content.add(tags, 1, 1);

        content.setVisible(true);
        return content;
    }
}
