package be.doji.productivity.trambuapp.components.view;

import be.doji.productivity.trambuapp.components.elements.AutocompleteTextField;
import be.doji.productivity.trambuapp.components.elements.EditableDataField;
import be.doji.productivity.trambuapp.components.elements.EditableDataFieldFactory;
import be.doji.productivity.trambuapp.components.elements.OverlayPane;
import be.doji.productivity.trambuapp.components.presenter.ActivityPagePresenter;
import be.doji.productivity.trambuapp.components.presenter.ActivityPresenter;
import be.doji.productivity.trambuapp.utils.ActivityFieldNames;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambuapp.utils.TooltipConstants;
import be.doji.productivity.trambucore.TrackMeConstants;
import be.doji.productivity.trambucore.model.tasks.Activity;
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

import java.text.SimpleDateFormat;
import java.util.List;

public class ActivityView extends TitledPane {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityView.class);
    private boolean isActive;

    private ActivityPresenter presenter;
    private OverlayPane overlay;
    private Accordion subActivitiesAccordion;

    private Button doneButton;
    private Button editButton;
    private Button deleteButton;
    private Button timingButton;
    private Button titleLabel;

    private EditableDataField<Label, TextField, String> nameField;
    private EditableDataField<Label, ComboBox<String>, String> priorityField;
    private EditableDataField<Label, AutocompleteTextField, String> locationField;
    private EditableDataField<Label, TextField, String> warningPeriodField;
    private EditableDataField<HBox, AutocompleteTextField, List<String>> projectsField;
    private EditableDataField<HBox, AutocompleteTextField, List<String>> tagsField;

    private DatePicker deadlineDatePicker;
    private GridPane activityContent;

    public ActivityView(Activity activity) {
        super();
        this.presenter = new ActivityPresenter(this, activity);
        setUp();
    }

    public ActivityView(Activity activity, ActivityPagePresenter parentPresenter) {
        super();
        this.presenter = new ActivityPresenter(this, activity, parentPresenter);
        setUp();
    }

    public void setUp() {
        overlay = new OverlayPane();
        this.createHeader();
        this.setContent(createContentContainer());
        this.setVisible(true);
        this.setOnMouseClicked(event -> this.setActive(!this.isActive));
        presenter.populate();
    }

    @NotNull private StackPane createContentContainer() {
        StackPane contentContainer = new StackPane();
        this.activityContent = createActivityContent();
        contentContainer.getChildren().add(activityContent);
        contentContainer.getChildren().add(overlay);
        return contentContainer;
    }

    private void createHeader() {
        this.setText(presenter.getActivityName());
        this.setGraphic(createTitleLabel());
    }

    private Button createTitleLabel() {
        this.titleLabel = new Button();
        titleLabel.getStyleClass().clear();
        titleLabel.getStyleClass().add("icon-button");
        titleLabel.setOnAction(event -> presenter.headerButtonClicked());
        return this.titleLabel;
    }

    GridPane createActivityContent() {
        createFields();
        return buildContentGrid();
    }

    void createFields() {
        this.nameField = EditableDataFieldFactory.getEditableStringField(ActivityFieldNames.FIELD_NAME);
        this.priorityField = EditableDataFieldFactory
                .getEditableStringFieldDropdown(FXCollections.observableArrayList(TrackMeConstants.getPriorityList()),
                        ActivityFieldNames.FIELD_PRIORITY);
        this.warningPeriodField = EditableDataFieldFactory
                .getEditableStringField(ActivityFieldNames.FIELD_WARNING_PERIOD);
        this.locationField = EditableDataFieldFactory
                .getEditableStringFieldWithAutocomplete(ActivityFieldNames.FIELD_LOCATION);
        this.projectsField = EditableDataFieldFactory
                .getEditableStringListFieldWithAutocomplete(string -> this.presenter.setTagFilter(string),
                        ActivityFieldNames.FIELD_PROJECTS);
        this.tagsField = EditableDataFieldFactory
                .getEditableStringListFieldWithAutocomplete(string -> this.presenter.setTagFilter(string),
                        ActivityFieldNames.FIELD_TAGS);

    }

    GridPane buildContentGrid() {
        GridPane content = DisplayUtils.createDefaultGridPane();
        int rowIndex = 0;
        content.add(createActvityControls(), 0, rowIndex++, 2, 1);
        content.add(DisplayUtils.createHorizontalSpacer(), 0, rowIndex++, 2, 1);
        content.add(createTimingControls(), 0, rowIndex++, 2, 1);

        if (this.priorityField.hasData()) {
            content.add(this.priorityField.getNameLabel(), 0, rowIndex);
            content.add(this.priorityField.get(), 0, rowIndex);
        }

        if (this.locationField.hasData()) {
            content.add(this.locationField.getNameLabel(), 0, rowIndex);
            content.add(this.locationField.get(), 0, rowIndex++);
        }

        if (this.warningPeriodField.hasData()) {
            content.add(this.warningPeriodField.getNameLabel(), 0, rowIndex);
            content.add(this.warningPeriodField.get(), 1, rowIndex++);
        }

        if (this.tagsField.hasData()) {
            content.add(this.tagsField.getNameLabel(), 0, rowIndex);
            content.add(this.tagsField.get(), 1, rowIndex++);
        }

        if (this.projectsField.hasData()) {
            content.add(this.projectsField.getNameLabel(), 0, rowIndex);
            content.add(this.projectsField.get(), 0, rowIndex);
        }

        content.add(createLogPoints(), 0, rowIndex++, 2, 1);

        content.add(new Label("Notes: "), 0, rowIndex);
        content.add(createNotes(), 1, rowIndex++);

        if (presenter.hasSubActivities()) {
            Label subActivityTitle = new Label("Subactivities: ");
            subActivityTitle.getStyleClass().clear();
            subActivityTitle.getStyleClass().add("separator-label");
            content.add(subActivityTitle, 0, rowIndex++);
            content.add(createSubActivities(), 0, rowIndex, 2, 1);
        }

        content.setVisible(true);
        return content;

    }

    private Node createEditableDeadline() {
        HBox deadlinePicker = new HBox();
        deadlinePicker.getChildren().add(createDatePicker());
        return deadlinePicker;
    }

    private DatePicker createDatePicker() {
        this.deadlineDatePicker = new DatePicker();
        deadlineDatePicker.setOnAction(event -> this.presenter.deadlinePicked());
        return deadlineDatePicker;
    }

    private GridPane createLogPoints() {
        List<TimeLog> logpoints = presenter.getActivityLog().getLogpoints();

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
        logpointGrid.add(new Label(presenter.getActivityLog().getTimeSpentInHoursString()), 1, logRowIndex);
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
        parent.valueProperty().addListener((ov, t, newParent) -> presenter.changeParent(newParent));
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

    HBox createTimingControls() {
        HBox timingControls = new HBox();

        this.timingButton = new Button(presenter.getTimingButtonText());
        timingButton.setOnAction(event -> {
            presenter.timingButtonPressed();
        });

        FontAwesomeIconView iconView = presenter.getTimingButtonIcon();
        timingButton.setGraphic(iconView);
        timingButton.setTooltip(presenter.getTimingButtonTooltipText());

        timingControls.getChildren().add(timingButton);

        return timingControls;
    }

    private Button createDoneButton() {
        this.doneButton = new Button();
        FontAwesomeIconView doneIcon = DisplayUtils.createStyledIcon(FontAwesomeIcon.REFRESH);
        this.doneButton.setGraphic(doneIcon);

        this.doneButton.setOnAction(event -> presenter.doneClicked());

        return this.doneButton;
    }

    private Button createEditButton() {
        this.editButton = new Button(presenter.getEditButonText());
        FontAwesomeIconView editIcon = DisplayUtils.createStyledIcon(FontAwesomeIcon.EDIT);
        editButton.setGraphic(editIcon);
        editButton.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_ACTIVITY_EDIT));
        editButton.setOnAction(event -> presenter.editButtonClicked());
        return editButton;
    }

    private Node createDeleteButton() {
        this.deleteButton = new Button(DisplayConstants.BUTTON_TEXT_DELETE);
        FontAwesomeIconView removeIcon = DisplayUtils.createStyledIcon(FontAwesomeIcon.REMOVE);
        deleteButton.setGraphic(removeIcon);
        deleteButton.setOnAction(event -> {
            presenter.deleteButtonClicked();
        });
        deleteButton.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_ACTIVITY_DELETE));
        return deleteButton;
    }

    boolean isActive() {
        return isActive;
    }

    private void setActive(boolean active) {
        LOG.debug("Making pane active: " + this.presenter.getActivityName());
        this.isActive = active;
    }

    public void refresh() {
        this.presenter.refresh();
        this.activityContent = buildContentGrid();
    }

    public ActivityPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(ActivityPresenter presenter) {
        this.presenter = presenter;
    }

    public OverlayPane getOverlay() {
        return overlay;
    }

    public void setOverlay(OverlayPane overlay) {
        this.overlay = overlay;
    }

    public Accordion getSubActivitiesAccordion() {
        return subActivitiesAccordion;
    }

    public void setSubActivitiesAccordion(Accordion subActivitiesAccordion) {
        this.subActivitiesAccordion = subActivitiesAccordion;
    }

    public Button getDoneButton() {
        return doneButton;
    }

    public void setDoneButton(Button doneButton) {
        this.doneButton = doneButton;
    }

    public Button getEditButton() {
        return editButton;
    }

    public void setEditButton(Button editButton) {
        this.editButton = editButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public void setDeleteButton(Button deleteButton) {
        this.deleteButton = deleteButton;
    }

    public Button getTimingButton() {
        return timingButton;
    }

    public void setTimingButton(Button timingButton) {
        this.timingButton = timingButton;
    }

    public Button getTitleLabel() {
        return titleLabel;
    }

    public void setTitleLabel(Button titleLabel) {
        this.titleLabel = titleLabel;
    }

    public EditableDataField<Label, TextField, String> getNameField() {
        return nameField;
    }

    public void setNameField(EditableDataField<Label, TextField, String> nameField) {
        this.nameField = nameField;
    }

    public EditableDataField<Label, ComboBox<String>, String> getPriorityField() {
        return priorityField;
    }

    public void setPriorityField(EditableDataField<Label, ComboBox<String>, String> priorityField) {
        this.priorityField = priorityField;
    }

    public EditableDataField<Label, AutocompleteTextField, String> getLocationField() {
        return locationField;
    }

    public void setLocationField(EditableDataField<Label, AutocompleteTextField, String> locationField) {
        this.locationField = locationField;
    }

    public EditableDataField<Label, TextField, String> getWarningPeriodField() {
        return warningPeriodField;
    }

    public void setWarningPeriodField(EditableDataField<Label, TextField, String> warningPeriodField) {
        this.warningPeriodField = warningPeriodField;
    }

    public EditableDataField<HBox, AutocompleteTextField, List<String>> getProjectsField() {
        return projectsField;
    }

    public void setProjectsField(EditableDataField<HBox, AutocompleteTextField, List<String>> projectsField) {
        this.projectsField = projectsField;
    }

    public EditableDataField<HBox, AutocompleteTextField, List<String>> getTagsField() {
        return tagsField;
    }

    public void setTagsField(EditableDataField<HBox, AutocompleteTextField, List<String>> tagsField) {
        this.tagsField = tagsField;
    }

    public DatePicker getDeadlineDatePicker() {
        return deadlineDatePicker;
    }

    public void setDeadlineDatePicker(DatePicker deadlineDatePicker) {
        this.deadlineDatePicker = deadlineDatePicker;
    }
}
