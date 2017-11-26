package be.doji.productivity.trambuapp.controls;

import be.doji.productivity.trambuapp.views.ActivityOverview;
import be.doji.productivity.trambuapp.views.TimesheetView;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import kotlin.jvm.JvmClassMappingKt;
import org.jetbrains.annotations.NotNull;
import tornadofx.UIComponent;
import tornadofx.View;
import tornadofx.ViewTransition;

public class MainMenuBar extends UIComponent {

    private HBox root;
    private View superView;

    public MainMenuBar(View parentView) {
        super();
        this.superView = parentView;
        root = new HBox();
        root.setSpacing(5.0);
        Button activityPage = new Button("Activity overview");
        activityPage.setOnAction(event -> {
            if (!superView.getClass().equals(ActivityOverview.class)) {
                superView.replaceWith(JvmClassMappingKt.getKotlinClass(ActivityOverview.class),
                        new ViewTransition.Slide(Duration.seconds(0.3), ViewTransition.Direction.LEFT), true, true);
            }

        });
        root.getChildren().add(activityPage);

        Button timesheetPage = new Button("Timesheet");
        timesheetPage.setOnAction(event -> {
            if (!superView.getClass().equals(TimesheetView.class)) {
                superView.replaceWith(JvmClassMappingKt.getKotlinClass(TimesheetView.class),
                        new ViewTransition.Slide(Duration.seconds(0.3), ViewTransition.Direction.RIGHT), true, true);
            }
        });
        root.getChildren().add(timesheetPage);
    }

    @NotNull @Override public Parent getRoot() {
        return root;
    }
}
