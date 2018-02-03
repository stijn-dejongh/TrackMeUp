package be.doji.productivity.trambuapp.components.view;

import be.doji.productivity.trambuapp.components.presenter.ActivityPagePresenter;
import be.doji.productivity.trambuapp.controls.ActivityControlAccordion;
import be.doji.productivity.trambuapp.controls.MainMenuBar;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambucore.model.tasks.Activity;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tornadofx.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActivityPageView extends View {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityPageView.class);

    private final ActivityPagePresenter presenter;
    private BorderPane root;
    private ActivityControlAccordion controlAccordion;
    private Accordion activityAccordion;
    private List<TitledPane> activityPanes = new ArrayList<>();

    public ActivityPageView() {
        super();
        this.setTitle(DisplayConstants.TITLE_APPLICATION + " - " + DisplayConstants.TITLE_ACTIVITY);
        this.presenter = new ActivityPagePresenter(this);

        root = new BorderPane();
        root.setPrefHeight(DisplayConstants.UI_DEFAULT_WINDOW_HEIGHT);
        root.setPrefWidth(DisplayConstants.UI_DEFAULT_WINDOW_WIDTH);
        root.setBottom(new MainMenuBar(this).getRoot());
        this.populate();
    }

    @NotNull @Override public Parent getRoot() {
        return this.root;
    }

    @Override public void onDock() {
        presenter.onViewLoad();
    }

    @Override public void onDelete() {
        presenter.onViewClose();
    }

    private void populate() {
        SplitPane splitPane = new SplitPane();
        splitPane.setPrefHeight(DisplayConstants.UI_DEFAULT_WINDOW_HEIGHT);
        splitPane.setPrefWidth(DisplayConstants.UI_DEFAULT_WINDOW_WIDTH);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPosition(0, 0.65);

        splitPane.getItems().add(createActivityPane());
        splitPane.getItems().add(createControlsPane());

        this.setContent(splitPane);
    }

    @NotNull private ScrollPane createActivityPane() {
        ScrollPane activityPane = new ScrollPane();
        this.activityAccordion = new Accordion();
        activityPane.setContent(activityAccordion);
        activityPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return activityPane;
    }

    @NotNull private ActivityControlAccordion createControlsPane() {
        this.controlAccordion = new ActivityControlAccordion(this.presenter);
        return controlAccordion;
    }

    @NotNull private ActivityView createActivityPane(Activity activity) {
        return new ActivityView(activity);
    }

    public void refreshAccordion() {
        this.activityAccordion.getPanes().clear();
        this.activityAccordion.getPanes().addAll(this.getPanes());
        getActivePane().ifPresent(activityAccordion::setExpandedPane);
    }

    private Optional<ActivityView> getActivePane() {
        LOG.debug("Looking for active pane");
        for (TitledPane pane : activityAccordion.getPanes()) {
            if (pane.getClass().equals(ActivityView.class)) {
                ActivityView castedPane = (ActivityView) pane;
                if (castedPane.isActive()) {
                    LOG.debug("Found active Pane");
                    return Optional.of(castedPane);
                }
            }
        }
        return Optional.empty();
    }

    public void setContent(Node content) {
        Objects.requireNonNull(content);
        root.setCenter(content);
    }

    public ActivityControlAccordion getControlAccordion() {
        return controlAccordion;
    }

    public void setControlAccordion(ActivityControlAccordion controlAccordion) {
        this.controlAccordion = controlAccordion;
    }

    public Accordion getActivityAccordion() {
        return activityAccordion;
    }

    public void setActivityAccordion(Accordion activityAccordion) {
        this.activityAccordion = activityAccordion;
    }

    public List<TitledPane> getPanes() {
        return activityPanes;
    }

    public List<ActivityView> getActivityPanes() {
        return activityPanes.stream().filter(ActivityView.class::isInstance).map(ActivityView.class::cast)
                .collect(Collectors.toList());
    }

    public void setActivityPanes(List<TitledPane> activityPanes) {
        this.activityPanes = activityPanes;
    }

    public void addPane(TitledPane pane) {
        this.activityPanes.add(pane);
    }
}
