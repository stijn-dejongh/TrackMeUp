package be.doji.productivity.trambuapp.components.view;

import be.doji.productivity.trambuapp.components.elements.AutocompleteTextField;
import be.doji.productivity.trambuapp.components.elements.OverlayPane;
import be.doji.productivity.trambuapp.components.presenter.ActivityPresenter;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambuapp.utils.TooltipConstants;
import be.doji.productivity.trambucore.TrackMeConstants;
import be.doji.productivity.trambucore.model.tasks.Activity;
import be.doji.productivity.trambucore.model.tracker.ActivityLog;
import be.doji.productivity.trambucore.model.tracker.TimeLog;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

public class ActivityView extends TitledPane {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityView.class);

    private boolean isActive;

    private Button titleLabel;
    private ActivityPresenter presenter;
    private boolean isEditable = false;
    private TextField nameField;
    private AutocompleteTextField projectsField;
    private AutocompleteTextField tagsField;
    private ActivityLog activityLog;
    private TextField warningPeriodInHours;
    private AutocompleteTextField locationField;
    private OverlayPane overlay;
    private Accordion subActivitiesAccordion;
    private DatePicker deadlineDatePicker;
    private Label warningPeriod;
    private Label priorityField;
    private Label locationLabel;
    private Label deadlineLabel;
    private HBox tagsBox;
    private HBox projectsBox;
    private Button doneButton;

    public ActivityView(Activity activity) {
        super();
        this.presenter = new ActivityPresenter(this, activity);

        /**
         * TODO: add this to presenter
         * this.activityLog = presenter.getLogForActivityId(activity.getId());
         */
        overlay = new OverlayPane();
        this.createHeader();
        this.setContent(createContentContainer());
        this.setVisible(true);
        this.setOnMouseClicked(event -> this.setActive(!this.isActive));
        presenter.populate();
    }

    @NotNull public StackPane createContentContainer() {
        StackPane contentContainer = new StackPane();
        GridPane activityContent = createActivityContent();
        contentContainer.getChildren().add(activityContent);
        contentContainer.getChildren().add(overlay);
        return contentContainer;
    }

    public void createHeader() {
        this.setText(presenter.getActivityName());
        this.setGraphic(createTitleLabel());
    }

    public Button createTitleLabel() {
        this.titleLabel = new Button();
        titleLabel.getStyleClass().clear();
        titleLabel.getStyleClass().add("icon-button");
        titleLabel.setOnAction(event -> presenter.headerButtonClicked());
        return this.titleLabel;
    }

    GridPane createActivityContent() {
        if (isEditable) {
            return createEditableContent();
        } else {
            return createStaticContent();
        }
    }

    GridPane createStaticContent() {
        GridPane content = DisplayUtils.createDefaultGridPane();
        int rowIndex = 0;
        content.add(createActvityControls(), 0, rowIndex++, 2, 1);
        content.add(DisplayUtils.createHorizontalSpacer(), 0, rowIndex++, 2, 1);

        content.add(new Label("Priority: "), 0, rowIndex);
        content.add(createStaticPriority(), 1, rowIndex++);

        content.add(new Label("Deadline: "), 0, rowIndex);
        content.add(createStaticDeadline(), 1, rowIndex++);

        content.add(new Label("Location :"), 0, rowIndex);
        content.add(createStaticLocation(), 1, rowIndex++);

        content.add(new Label("Warning period: "), 0, rowIndex);
        content.add(createStaticWarningPeriod(), 1, rowIndex++);

        content.add(new Label("Tags: "), 0, rowIndex);
        content.add(createStaticTags(), 1, rowIndex++);

        content.add(new Label("Projects: "), 0, rowIndex);
        content.add(createStaticProjects(), 1, rowIndex++);

        content.add(createLogPoints(), 0, rowIndex++, 2, 1);

        content.add(new Label("Notes: "), 0, rowIndex);
        content.add(createNotes(), 1, rowIndex++);

        if (!presenter.hasSubActivities()) {
            Label subActivityTitle = new Label("Subactivities: ");
            subActivityTitle.getStyleClass().clear();
            subActivityTitle.getStyleClass().add("separator-label");
            content.add(subActivityTitle, 0, rowIndex++);
            content.add(createSubActivities(), 0, rowIndex, 2, 1);
        }

        content.setVisible(true);
        return content;

    }

    private Node createStaticPriority() {
        this.priorityField = new Label("Priority");
        return this.priorityField;
    }

    private Node createStaticDeadline() {
        this.deadlineLabel = new Label();

        return deadlineLabel;
    }

    @NotNull private HBox createStaticWarningPeriod() {
        HBox hbox = new HBox();
        this.warningPeriod = new Label();
        hbox.getChildren().add(warningPeriod);
        return hbox;
    }

    private HBox createStaticTags() {
        this.tagsBox = new HBox(5);

        return this.tagsBox;
    }

    private HBox createStaticProjects() {
        this.projectsBox = new HBox();
        return this.projectsBox;
    }

    private Node createStaticLocation() {
        this.locationLabel = new Label("Location");
        return this.locationLabel;
    }

    GridPane createEditableContent() {
        GridPane content = DisplayUtils.createDefaultGridPane();

        int rowIndex = 0;
        content.add(createActvityControls(), 0, rowIndex++, 2, 1);
        content.add(DisplayUtils.createHorizontalSpacer(), 0, rowIndex++, 2, 1);
        content.add(createTimingControls(), 0, rowIndex++, 2, 1);

        content.add(new Label("Change activity name:"), 0, rowIndex);
        content.add(createEditableName(), 1, rowIndex++);

        content.add(new Label("Priority: "), 0, rowIndex);
        content.add(createEditablePriority(), 1, rowIndex++);

        content.add(new Label("Deadline: "), 0, rowIndex);
        content.add(createEditableDeadline(), 1, rowIndex++);

        content.add(new Label("Location :"), 0, rowIndex);
        content.add(createEditableLocation(), 1, rowIndex++);

        content.add(new Label("Warning period: "), 0, rowIndex);
        content.add(createEditableWarningPeriod(), 1, rowIndex++);

        content.add(new Label("Tags: "), 0, rowIndex);
        content.add(createEditableTags(), 1, rowIndex++);

        content.add(new Label("Projects: "), 0, rowIndex);
        content.add(createEditableProjects(), 1, rowIndex++);

        content.add(createLogPoints(), 0, rowIndex++, 2, 1);

        content.add(new Label("Notes: "), 0, rowIndex);
        content.add(createNotes(), 1, rowIndex++);

        if (!presenter.hasSubActivities()) {
            Label subActivityTitle = new Label("Subactivities: ");
            subActivityTitle.getStyleClass().clear();
            subActivityTitle.getStyleClass().add("separator-label");
            content.add(subActivityTitle, 0, rowIndex++);
            content.add(createSubActivities(), 0, rowIndex, 2, 1);
        }

        Label parentTitle = new Label("Select parent: ");
        parentTitle.getStyleClass().clear();
        parentTitle.getStyleClass().add("separator-label");
        content.add(parentTitle, 0, rowIndex++);
        content.add(createParentSelector(), 0, rowIndex++, 2, 1);

        content.setVisible(true);
        return content;
    }

    private Node createEditableName() {
        nameField = new TextField();
        nameField.setText(presenter.getActivityName());
        return nameField;
    }

    Node createEditablePriority() {
        ObservableList<String> options = FXCollections.observableArrayList(TrackMeConstants.getPriorityList());
        final ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.setValue(this.presenter.getActivityPriority());
        comboBox.valueProperty().addListener((ov, t, t1) -> this.presenter.setActivityPriority(t1));
        return comboBox;
    }

    private Node createEditableDeadline() {
        HBox deadlinePicker = new HBox();
        deadlinePicker.getChildren().add(createDatePicker());
        return deadlinePicker;
    }

    private Node createEditableLocation() {
        locationField = new AutocompleteTextField();

        return locationField;
    }

    private DatePicker createDatePicker() {
        this.deadlineDatePicker = new DatePicker();
        deadlineDatePicker.setOnAction(event -> this.presenter.deadlinePicked());
        return deadlineDatePicker;
    }

    @NotNull private HBox createEditableWarningPeriod() {
        HBox hbox = new HBox();
        warningPeriodInHours = new TextField();
        hbox.getChildren().add(warningPeriodInHours);
        hbox.getChildren().add(new Label("hours"));
        return hbox;
    }

    private Node createEditableTags() {
        tagsField = new AutocompleteTextField();
        return tagsField;
    }

    private Node createEditableProjects() {
        projectsField = new AutocompleteTextField();
        return projectsField;
    }

    private GridPane createLogPoints() {
        List<TimeLog> logpoints = activityLog.getLogpoints();

        GridPane logpointGrid = new GridPane();
        logpointGrid.setVgap(4);
        logpointGrid.setPadding(new Insets(5, 5, 5, 5));
        int logRowIndex = 0;
        Label logpointInfo = new Label("There are " + logpoints.size() + " timelogs available.  ");
        logpointGrid.add(logpointInfo, 0, logRowIndex);
        if (!logpoints.isEmpty()) {
            logpointGrid.add(createOpenLogsButton(logpoints), 1, logRowIndex++);
        } else {
            logRowIndex++;
        }

        logpointGrid.add(new Label("Time spent on activity: "), 0, logRowIndex);
        logpointGrid.add(new Label(activityLog.getTimeSpentInHoursString()), 1, logRowIndex);
        return logpointGrid;
    }

    @NotNull private Button createOpenLogsButton(List<TimeLog> logpoints) {
        Button showLogs = new Button("Show timelogs");
        showLogs.setGraphic(DisplayUtils.createStyledIcon(FontAwesomeIcon.INFO_CIRCLE));
        showLogs.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_ACTIVITY_LOGPOINT_EXPAND));
        showLogs.setOnAction(event -> {
            this.overlay.setContent(createLogPointGrid(logpoints));
            this.overlay.refreshContent();
            this.overlay.setVisible(true);
        });
        return showLogs;
    }

    private GridPane createLogPointGrid(List<TimeLog> logpoints) {
        GridPane logpointGrid = new GridPane();
        logpointGrid.setVgap(4);
        logpointGrid.setPadding(new Insets(5, 5, 5, 5));
        int logRowIndex = 0;

        if (!logpoints.isEmpty()) {
            SimpleDateFormat dateFormat = TrackMeConstants.getDateFormat();
            logpointGrid.add(new Label("Logpoints: "), 0, logRowIndex++);
            for (TimeLog log : logpoints) {
                logpointGrid.add(new Label("from " + dateFormat.format(log.getStartTime()) + (log.getEndTime() == null?
                        "":
                        (" to " + dateFormat.format(log.getEndTime())))), 1, logRowIndex++);
            }
        } else {
            logpointGrid.add(new Label("No timelogs available for this activity"), 0, logRowIndex);
        }

        return logpointGrid;
    }

    private Button createNotes() {
        Button noteButton = new Button("View notes");
        noteButton.setGraphic(DisplayUtils.createStyledIcon(FontAwesomeIcon.STICKY_NOTE));
        noteButton.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_ACTIVITY_NOTE_EXPAND));
        noteButton.setOnAction(event -> presenter.openNotes());
        return noteButton;
    }

    private Node createParentSelector() {
        ObservableList<String> options = FXCollections.observableArrayList(presenter.getPossibleParents());
        final ComboBox<String> parent = new ComboBox<>(options);
        parent.valueProperty().addListener((ov, t, newParent) -> {
            presenter.changeParent(newParent);
        });
        return parent;
    }

    private Node createSubActivities() {
        this.subActivitiesAccordion = new Accordion();
        return this.subActivitiesAccordion;
    }

    GridPane createActvityControls() {
        GridPane content = new GridPane();
        content.setVgap(4);
        content.setHgap(4);
        content.setPadding(new Insets(5, 5, 5, 5));
        content.add(createDoneButton(), 0, 0);
        content.add(createEditButton(), 1, 0);
        content.add(createDeleteButton(), 2, 0);
        return content;
    }

    private Button createDoneButton() {
        this.doneButton = new Button();
        FontAwesomeIconView doneIcon = DisplayUtils.createStyledIcon(FontAwesomeIcon.REFRESH);
        this.doneButton.setGraphic(doneIcon);

        this.doneButton.setOnAction(event -> presenter.doneClicked());

        return this.doneButton;
    }

    void makeEditable() {
        this.isEditable = true;
    }

    void makeUneditable() {
        this.isEditable = false;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        LOG.debug("Making pane active: " + this.presenter.getActivityName());
        this.isActive = active;
    }

    public Button getDoneButton() {
        return doneButton;
    }

    public void setDoneButton(Button doneButton) {
        this.doneButton = doneButton;
    }

    public HBox getTagsBox() {
        return tagsBox;
    }

    public void setTagsBox(HBox tagsBox) {
        this.tagsBox = tagsBox;
    }

    public HBox getProjectsBox() {
        return this.projectsBox;
    }

    public void setProjectsBox(HBox projectsBox) {
        this.projectsBox = projectsBox;
    }

    public Label getDeadlineLabel() {
        return deadlineLabel;
    }

    public void setDeadlineLabel(Label deadlineLabel) {
        this.deadlineLabel = deadlineLabel;
    }

    public Button getTitleLabel() {
        return titleLabel;
    }

    public void setTitleLabel(Button titleLabel) {
        this.titleLabel = titleLabel;
    }

    public TextField getNameField() {
        return nameField;
    }

    public void setNameField(TextField nameField) {
        this.nameField = nameField;
    }

    public AutocompleteTextField getProjectsField() {
        return projectsField;
    }

    public void setProjectsField(AutocompleteTextField projectsField) {
        this.projectsField = projectsField;
    }

    public AutocompleteTextField getTagsField() {
        return tagsField;
    }

    public void setTagsField(AutocompleteTextField tagsField) {
        this.tagsField = tagsField;
    }

    public ActivityLog getActivityLog() {
        return activityLog;
    }

    public void setActivityLog(ActivityLog activityLog) {
        this.activityLog = activityLog;
    }

    public TextField getWarningPeriodInHours() {
        return warningPeriodInHours;
    }

    public void setWarningPeriodInHours(TextField warningPeriodInHours) {
        this.warningPeriodInHours = warningPeriodInHours;
    }

    public AutocompleteTextField getLocationField() {
        return locationField;
    }

    public void setLocationField(AutocompleteTextField locationField) {
        this.locationField = locationField;
    }

    public OverlayPane getOverlay() {
        return overlay;
    }

    public void setOverlay(OverlayPane overlay) {
        this.overlay = overlay;
    }

    public DatePicker getDeadlineDatePicker() {
        return deadlineDatePicker;
    }

    public void setDeadlineDatePicker(DatePicker deadlineDatePicker) {
        this.deadlineDatePicker = deadlineDatePicker;
    }

    public Accordion getSubActivitiesAccordion() {
        return subActivitiesAccordion;
    }

    public void setSubActivitiesAccordion(Accordion subActivitiesAccordion) {
        this.subActivitiesAccordion = subActivitiesAccordion;
    }

    public Label getWarningPeriod() {
        return warningPeriod;
    }

    public void setWarningPeriod(Label warningPeriod) {
        this.warningPeriod = warningPeriod;
    }

    public Label getPriorityField() {
        return priorityField;
    }

    public void setPriorityField(Label priorityField) {
        this.priorityField = priorityField;
    }

    public Label getLocationLabel() {
        return locationLabel;
    }

    public void setLocationLabel(Label locationLabel) {
        this.locationLabel = locationLabel;
    }

    private Button createEditButton() {
        Button edit = new Button(getEditButonText());
        FontAwesomeIconView editIcon = DisplayUtils.createStyledIcon(FontAwesomeIcon.EDIT);
        edit.setGraphic(editIcon);
        edit.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_ACTIVITY_EDIT));
        edit.setOnAction(event -> {
            try {
                if (isEditable) {
                    makeUneditable();
                    save();
                    setContent(createActivityContent());

                } else {
                    makeEditable();
                    setContent(createActivityContent());
                }
                updateHeader();
                edit.setText(getEditButonText());
            } catch (IOException | ParseException e) {
                LOG.error(DisplayConstants.ERROR_MESSAGE_ACTIVITY_SAVING + ": " + e.getMessage());
            }
        });
        return edit;
    }

    private String getEditButonText() {
        return this.isEditable?DisplayConstants.BUTTON_TEXT_SAVE:DisplayConstants.BUTTON_TEXT_EDIT;
    }

    private Node createDeleteButton() {
        Button delete = new Button(DisplayConstants.BUTTON_TEXT_DELETE);
        FontAwesomeIconView removeIcon = DisplayUtils.createStyledIcon(FontAwesomeIcon.REMOVE);
        delete.setGraphic(removeIcon);
        delete.setOnAction(event -> {
            try {
                this.toggleCompleted();
                presenter.getActivityController().getActivityManager().delete(this.activity);
                presenter.refresh();
            } catch (IOException | ParseException e) {
                LOG.error(DisplayConstants.ERROR_MESSAGE_ACTIVITY_SAVING + ": " + e.getMessage());
            }
        });
        delete.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_ACTIVITY_DELETE));
        return delete;
    }

    HBox createTimingControls() {
        activityLog = presenter.getActivityController().getTimeTrackingManager()
                .getLogForActivityId(this.activity.getId());
        HBox timingControls = new HBox();

        Button startStopButton = new Button(getTimingButtonText());
        Optional<TimeLog> activeLog = activityLog.getActiveLog();
        startStopButton.setOnAction(event -> {
            if (activeLog.isPresent()) {
                activityLog.stopActiveLog();
            } else {
                activityLog.startLog();
            }
            startStopButton.setText(getTimingButtonText());
            startStopButton.setGraphic(getTimingButtonIcon());
            presenter.getActivityController().getTimeTrackingManager().save(activityLog);
            this.setContent(createActivityContent());
        });

        FontAwesomeIconView iconView = getTimingButtonIcon();
        startStopButton.setGraphic(iconView);
        startStopButton.setTooltip(getTimingButtonTooltipText());

        timingControls.getChildren().add(startStopButton);

        return timingControls;
    }

    private FontAwesomeIconView getTimingButtonIcon() {
        return DisplayUtils.createStyledIcon(
                activityLog.getActiveLog().isPresent()?FontAwesomeIcon.HOURGLASS_END:FontAwesomeIcon.HOURGLASS_START);
    }

    private String getTimingButtonText() {
        Optional<TimeLog> activeLog = getActiveLog();
        if (activeLog.isPresent()) {
            return DisplayConstants.BUTTON_TEXT_TIMER_STOP;
        } else {
            return DisplayConstants.BUTTON_TEXT_TIMER_START;
        }
    }

    private Optional<TimeLog> getActiveLog() {
        activityLog = presenter.getActivityController().getTimeTrackingManager()
                .getLogForActivityId(this.activity.getId());
        return activityLog.getActiveLog();
    }

    public Tooltip getTimingButtonTooltipText() {
        return DisplayUtils.createTooltip(getActiveLog().isPresent()?
                TooltipConstants.TOOLTIP_TEXT_ACTIVITY_TIMING_CONTROL_STOP:
                TooltipConstants.TOOLTIP_TEXT_ACTIVITY_TIMING_CONTROL_START);
    }

    public void refresh() {
        presenter.getActivityController().getActivityManager().getSavedActivityById(this.activity.getId().toString())
                .ifPresent(savedActivity -> this.activity = savedActivity);
        this.setContent(this.createContentContainer());
        this.updateHeader();
    }

}
