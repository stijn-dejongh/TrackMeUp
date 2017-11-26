package be.doji.productivity.trambuapp.controls;

import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambuapp.views.ActivityOverview;
import be.doji.productivity.trambucore.model.tasks.Activity;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

public class ActivityControlAccordion extends Accordion {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityControlAccordion.class);

    private ActivityOverview parent;
    private String configuredTodoLocation;
    private String configuredTimeLocation;
    private Label activeFilter;

    public ActivityControlAccordion(ActivityOverview parent) {
        super();
        this.parent = parent;
        TitledPane fileOptionsControls = createFileOptionsControls();
        this.getPanes().add(fileOptionsControls);
        TitledPane generalControls = createGeneralControls();
        this.getPanes().add(generalControls);
        if (!parent.isSetFileOptions()) {
            this.setExpandedPane(fileOptionsControls);
        } else {
            this.setExpandedPane(generalControls);
        }

        this.getStylesheets().clear();
        this.getStylesheets().add("style/css/trambu-controls.css");
    }

    private TitledPane createFileOptionsControls() {
        TitledPane gridTitlePane = new TitledPane();
        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.add(new Label("Todo file: "), 0, 0);

        FileChooser todoFileChooser = new FileChooser();
        todoFileChooser.setTitle("Open TODO list File");

        Button openTodoButton = createOpenFileButton("Select TODO file", todoFileChooser, file -> {
            try {
                String filePath = file.getAbsolutePath();
                this.configuredTodoLocation = filePath;
                parent.getActivityManager().updateFileLocation(filePath);
                parent.reloadActivities();
            } catch (IOException | ParseException e) {
                LOG.error("Error opening todo file", e);
            }
        });
        grid.add(openTodoButton, 1, 0);

        grid.add(new Label("Timetracking file: "), 0, 1);
        FileChooser timeFileChooser = new FileChooser();
        timeFileChooser.setTitle("Open time tracking File");

        Button openTimeButton = createOpenFileButton("Select timelog file", timeFileChooser, file -> {
            try {
                String filePath = file.getAbsolutePath();
                this.configuredTimeLocation = filePath;
                parent.getTimeTrackingManager().updateFileLocation(filePath);
            } catch (IOException | ParseException e) {
                LOG.error("Error opening time tracking file", e);
            }
        });
        grid.add(openTimeButton, 1, 1);
        grid.add(DisplayUtils.createHorizontalSpacer(), 0, 2, 2, 1);

        Button savePreferences = new Button("Remember choices");
        savePreferences.setOnAction(event -> {
            if (StringUtils.isNotBlank(configuredTodoLocation)) {
                parent.getConfigManager()
                        .addProperty(DisplayConstants.NAME_PROPERTY_TODO_LOCATION, configuredTodoLocation);
            }
            if (StringUtils.isNotBlank(configuredTimeLocation)) {
                parent.getConfigManager()
                        .addProperty(DisplayConstants.NAME_PROPERTY_TIME_LOCATION, configuredTimeLocation);
            }
            try {
                parent.getConfigManager().writeToFile();
            } catch (IOException e) {
                LOG.error(DisplayConstants.ERROR_MESSAGE_WRITE_PROPERTIES, e);
            }
        });
        grid.add(savePreferences, 0, 3);

        gridTitlePane.setText("File Options");
        gridTitlePane.setContent(grid);
        gridTitlePane.setVisible(true);
        return gridTitlePane;
    }

    private Button createOpenFileButton(String buttonText, FileChooser fileChooser, Consumer<File> fileLambda) {
        Button button = new Button(buttonText);

        button.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                fileLambda.accept(file);
                parent.reloadActivities();
            }
        });
        return button;
    }

    private TitledPane createGeneralControls() {
        TitledPane gridTitlePane = new TitledPane();
        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(5);
        grid.setPadding(new Insets(5, 5, 5, 5));

        Label filterLabel = new Label("Active filter: ");
        filterLabel.getStyleClass().clear();
        filterLabel.getStyleClass().add(DisplayConstants.STYLE_LABEL_SPECIAL);
        activeFilter = new Label(parent.getActiveFilter());
        grid.add(filterLabel, 0, 0);
        grid.add(activeFilter, 1, 0);

        Button filterButton = new Button("Filter completed");
        filterButton.setOnAction(e -> {
            parent.setFilterDone(true);
            updateFilterLabel();
            parent.reloadActivities();
        });
        grid.add(filterButton, 0, 1);

        Button resetFilter = new Button("Reset filter");
        resetFilter.setOnAction(e -> {
            parent.resetFilter();
            parent.reloadActivities();
        });
        grid.add(resetFilter, 1, 1);

        grid.add(DisplayUtils.createHorizontalSpacer(), 0, 2, 2, 1);

        Button addActivity = new Button("Add activity");
        FontAwesomeIconView addIcon = new FontAwesomeIconView(FontAwesomeIcon.PLUS_CIRCLE);
        addIcon.setGlyphStyle(DisplayConstants.STYLE_GLYPH_DEFAULT);
        addActivity.setGraphic(addIcon);

        addActivity.setOnAction(event -> {
            try {
                Activity newActivity = new Activity("EDIT ME I AM A NEW ACTIVITY");
                parent.getActivityManager().save(newActivity);
                parent.reloadActivities();
            } catch (IOException | ParseException exception) {
                LOG.error("Error creation new activity", exception);
            }
        });
        grid.add(addActivity, 0, 3);

        grid.add(DisplayUtils.createHorizontalSpacer(), 0, 4, 2, 1);

        Button refresh = new Button("");
        refresh.setOnAction(event -> parent.reloadActivities());
        FontAwesomeIconView glyph = new FontAwesomeIconView(FontAwesomeIcon.REFRESH);
        glyph.setGlyphStyle(DisplayConstants.STYLE_GLYPH_DEFAULT);
        refresh.setGraphic(glyph);

        grid.add(refresh, 0, 5);

        gridTitlePane.setContent(grid);
        gridTitlePane.setText("General controls");
        return gridTitlePane;
    }

    public void updateFilterLabel() {
        activeFilter.setText(parent.getActiveFilter());
    }
}
