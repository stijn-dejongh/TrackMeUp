package be.doji.productivity.trambuapp.views;

import be.doji.productivity.trambuapp.controllers.ActivityController;
import be.doji.productivity.trambuapp.controls.MainMenuBar;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambuapp.utils.TooltipConstants;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tornadofx.View;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

public class OptionsView extends View {

    private static final Logger LOG = LoggerFactory.getLogger(OptionsView.class);

    private final ActivityController activityController;
    private BorderPane root;

    private String configuredTodoLocation;
    private String configuredTimeLocation;
    private String configuredNoteLocation;

    public OptionsView() {
        super();
        this.setTitle(DisplayConstants.TITLE_APPLICATION + " - " + DisplayConstants.TITLE_OPTIONS);

        this.root = new BorderPane();
        this.activityController = find(ActivityController.class);
        root.setPrefHeight(DisplayConstants.UI_DEFAULT_WINDOW_HEIGHT);
        root.setPrefWidth(DisplayConstants.UI_DEFAULT_WINDOW_WIDTH);

        root.setCenter(createControlGrid());
        root.setBottom(new MainMenuBar(this).getRoot());
    }

    @NotNull private GridPane createControlGrid() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add(DisplayConstants.STYLE_CLASS_DEFAULT);
        grid.setVgap(4);
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.add(new Label("Todo file: "), 0, 0);
        grid.add(createTodoFileSelectButton(), 1, 0);

        grid.add(new Label("Timetracking file: "), 0, 1);
        grid.add(createTimeFileSelectButton(), 1, 1);

        grid.add(new Label("Notes directory: "), 0, 2);
        grid.add(createNotesSelectButton(), 1, 2);

        grid.add(DisplayUtils.createHorizontalSpacer(), 0, 3, 2, 1);

        grid.add(createSavePreferencesButton(), 0, 3);
        return grid;
    }

    @NotNull private Button createTodoFileSelectButton() {
        FileChooser todoFileChooser = new FileChooser();
        todoFileChooser.setTitle("Open TODO list File");

        Button openTodoButton = createOpenFileButton("Select TODO file", todoFileChooser, file -> {
            try {
                String filePath = file.getAbsolutePath();
                this.configuredTodoLocation = filePath;
                this.getActivityController().getActivityManager().updateFileLocation(filePath);
            } catch (IOException | ParseException e) {
                LOG.error("Error opening todo file", e);
            }
        });
        openTodoButton.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_OPTIONS_TODO_FILE_SELECT));
        return openTodoButton;
    }

    @NotNull private Button createTimeFileSelectButton() {
        FileChooser timeFileChooser = new FileChooser();
        timeFileChooser.setTitle("Open time tracking File");

        Button openTimeButton = createOpenFileButton("Select timelog file", timeFileChooser, file -> {
            try {
                String filePath = file.getAbsolutePath();
                this.configuredTimeLocation = filePath;
                this.getActivityController().getTimeTrackingManager().updateFileLocation(filePath);
            } catch (IOException | ParseException e) {
                LOG.error("Error opening time tracking file", e);
            }
        });
        openTimeButton.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_OPTIONS_TIME_FILE_SELECT));
        return openTimeButton;
    }

    @NotNull private Button createNotesSelectButton() {
        DirectoryChooser notesDirectoryChooser = new DirectoryChooser();
        notesDirectoryChooser.setTitle("Open notes directory");

        Button notesDirectoryButton = createOpenDirectoryButton("Select notes directory", notesDirectoryChooser, file -> {
            try {
                String filePath = file.getAbsolutePath();
                this.configuredNoteLocation = filePath;
                this.getActivityController().getNoteManager().updateLocation(filePath);
            } catch (IOException e) {
                LOG.error("Error opening time tracking file", e);
            }
        });
        notesDirectoryButton
                .setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_OPTIONS_TIME_FILE_SELECT));
        return notesDirectoryButton;
    }

    @NotNull private Button createSavePreferencesButton() {
        Button savePreferences = new Button("Save preferences");
        savePreferences.setOnAction(event -> {
            if (StringUtils.isNotBlank(configuredTodoLocation)) {
                this.getActivityController().getConfigManager()
                        .addProperty(DisplayConstants.NAME_PROPERTY_TODO_LOCATION, configuredTodoLocation);
            }
            if (StringUtils.isNotBlank(configuredTimeLocation)) {
                this.getActivityController().getConfigManager()
                        .addProperty(DisplayConstants.NAME_PROPERTY_TIME_LOCATION, configuredTimeLocation);
            }

            if (StringUtils.isNotBlank(configuredNoteLocation)) {
                this.getActivityController().getConfigManager()
                        .addProperty(DisplayConstants.NAME_PROPERTY_NOTES_LOCATION, configuredNoteLocation);
            }
            try {
                this.getActivityController().getConfigManager().writeToFile();
            } catch (IOException e) {
                LOG.error(DisplayConstants.ERROR_MESSAGE_WRITE_PROPERTIES, e);
            }
        });
        savePreferences.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_OPTIONS_REMEMBER));
        return savePreferences;
    }

    private Button createOpenFileButton(String buttonText, FileChooser fileChooser, Consumer<File> fileLambda) {
        Button button = new Button(buttonText);

        button.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                fileLambda.accept(file);
            }
        });

        return button;
    }

    private Button createOpenDirectoryButton(String buttonText, DirectoryChooser dirChooser,
            Consumer<File> fileLambda) {
        Button button = new Button(buttonText);

        button.setOnAction(e -> {
            File file = dirChooser.showDialog(null);
            if (file != null) {
                fileLambda.accept(file);
            }
        });

        return button;
    }

    private ActivityController getActivityController() {
        return activityController;
    }

    @NotNull @Override public Parent getRoot() {
        return root;
    }
}
