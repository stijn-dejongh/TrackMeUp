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
import java.util.Date;
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
    private EditableDataField<Label, DatePicker, Date> deadlineField;

    private GridPane activityContent;

    public ActivityView(Activity activity) {
        super();
        this.presenter = new ActivityPresenter(this, activity);
        init();
    }

    public ActivityView(Activity activity, ActivityPagePresenter parentPresenter) {
        super();
        this.presenter = new ActivityPresenter(this, activity, parentPresenter);
        init();
    }

    private void init() {
        overlay = new OverlayPane();
        this.initHeader();
        this.initControls();
        this.initFields();
        this.setVisible(true);
        this.setOnMouseClicked(event -> this.setActive(!this.isActive));
        presenter.populate();
        this.setContent(createContentContainer());
    }

    @NotNull private StackPane createContentContainer() {
        StackPane contentContainer = new StackPane();
        this.activityContent = new GridPane();
        contentContainer.getChildren().add(buildContentGrid());
        contentContainer.getChildren().add(overlay);
        return contentContainer;
    }

    private void initHeader() {
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

    private void initControls() {
        initDoneButton();
        initEditButton();
        initDeleteButton();
    }

    private void initFields() {
        this.nameField = EditableDataFieldFactory.getEditableStringField(ActivityFieldNames.FIELD_NAME);
        this.priorityField = EditableDataFieldFactory
                .getEditableStringFieldDropdown(FXCollections.observableArrayList(TrackMeConstants.getPriorityList()),
                        ActivityFieldNames.FIELD_PRIORITY);
        this.warningPeriodField = EditableDataFieldFactory
                .getEditableStringField(ActivityFieldNames.FIELD_WARNING_PERIOD);
        this.locationField = EditableDataFieldFactory
                .getEditableStringFieldWithAutocomplete(ActivityFieldNames.FIELD_LOCATION);
        this.projectsField = EditableDataFieldFactory
                .getEditableStringListFieldWithAutocomplete(string -> this.presenter.setProjectFilter(string),
                        ActivityFieldNames.FIELD_PROJECTS);
        this.tagsField = EditableDataFieldFactory
                .getEditableStringListFieldWithAutocomplete(string -> this.presenter.setTagFilter(string),
                        ActivityFieldNames.FIELD_TAGS);
        this.deadlineField = EditableDataFieldFactory.getEditableDateField(event -> {
        }, ActivityFieldNames.FIELD_DEADLINE);
        this.createSubActivities();

    }

    private GridPane buildContentGrid() {
        GridPane content = DisplayUtils.createDefaultGridPane();
        int rowIndex = 0;
        content.add(buildControlsGrid(), 0, rowIndex++, 2, 1);
        content.add(DisplayUtils.createHorizontalSpacer(), 0, rowIndex++, 2, 1);
        content.add(createTimingControls(), 0, rowIndex++, 2, 1);

        if (this.priorityField.hasData() || this.priorityField.isEditable()) {
            content.add(this.priorityField.getNameLabel(), 0, rowIndex);
            content.add(this.priorityField.get(), 1, rowIndex++);
        }

        if (this.locationField.hasData() || this.locationField.isEditable()) {
            content.add(this.locationField.getNameLabel(), 0, rowIndex);
            content.add(this.locationField.get(), 1, rowIndex++);
        }

        if (this.deadlineField.hasData() || this.deadlineField.isEditable()) {
            content.add(this.deadlineField.getNameLabel(), 0, rowIndex);
            content.add(this.deadlineField.get(), 1, rowIndex++);
        }

        if (this.warningPeriodField.hasData() || this.warningPeriodField.isEditable()) {
            content.add(this.warningPeriodField.getNameLabel(), 0, rowIndex);
            content.add(this.warningPeriodField.get(), 1, rowIndex++);
        }

        if (this.tagsField.hasData() || this.tagsField.isEditable()) {
            content.add(this.tagsField.getNameLabel(), 0, rowIndex);
            content.add(this.tagsField.get(), 1, rowIndex++);
        }

        if (this.projectsField.hasData() || this.projectsField.isEditable()) {
            content.add(this.projectsField.getNameLabel(), 0, rowIndex);
            content.add(this.projectsField.get(), 1, rowIndex++);
        }

        content.add(createLogPoints(), 0, rowIndex++, 2, 1);

        content.add(new Label("Notes: "), 0, rowIndex);
        content.add(createNotes(), 1, rowIndex++);

        if (presenter.hasSubActivities()) {
            Label subActivityTitle = new Label("Subactivities: ");
            subActivityTitle.getStyleClass().clear();
            subActivityTitle.getStyleClass().add("separator-label");
            content.add(subActivityTitle, 0, rowIndex++);
            content.add(this.subActivitiesAccordion, 0, rowIndex++, 2, 1);
        }

        if (this.getPresenter().isEditable()) {
            content.add(new Label("Select parent: "), 0, rowIndex++);
            content.add(createParentSelector(), 0, rowIndex, 2, 1);
        }

        content.setVisible(true);
        return content;

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

    private void createSubActivities() {
        this.subActivitiesAccordion = new Accordion();
    }

    private GridPane buildControlsGrid() {
        GridPane content = new GridPane();
        content.setVgap(4);
        content.setHgap(4);
        content.setPadding(new Insets(5, 5, 5, 5));
        content.add(this.doneButton, 0, 0);
        content.add(this.editButton, 1, 0);
        content.add(this.deleteButton, 2, 0);
        return content;
    }

    private HBox createTimingControls() {
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

    private void initDoneButton() {
        this.doneButton = new Button();
        FontAwesomeIconView doneIcon = DisplayUtils.createStyledIcon(FontAwesomeIcon.REFRESH);
        this.doneButton.setGraphic(doneIcon);

        this.doneButton.setOnAction(event -> presenter.doneClicked());

    }

    private void initEditButton() {
        this.editButton = new Button(presenter.getEditButonText());
        FontAwesomeIconView editIcon = DisplayUtils.createStyledIcon(FontAwesomeIcon.EDIT);
        editButton.setGraphic(editIcon);
        editButton.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_ACTIVITY_EDIT));
        editButton.setOnAction(event -> presenter.editButtonClicked());
    }

    private void initDeleteButton() {
        this.deleteButton = new Button(DisplayConstants.BUTTON_TEXT_DELETE);
        FontAwesomeIconView removeIcon = DisplayUtils.createStyledIcon(FontAwesomeIcon.REMOVE);
        deleteButton.setGraphic(removeIcon);
        deleteButton.setOnAction(event -> {
            presenter.deleteButtonClicked();
        });
        deleteButton.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_ACTIVITY_DELETE));
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
    }

    public void refreshContent() {
        this.activityContent = buildContentGrid();
        this.setContent(this.activityContent);
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

    public EditableDataField<Label, DatePicker, Date> getDeadlineField() {
        return deadlineField;
    }

    public void setDeadlineField(EditableDataField<Label, DatePicker, Date> deadlineField) {
        this.deadlineField = deadlineField;
    }
}
