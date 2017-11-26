package be.doji.productivity.trambuapp.controls;

import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambuapp.views.ActivityOverview;
import be.doji.productivity.trambucore.model.tasks.Activity;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;

public class ActivityControls extends TitledPane {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityControls.class);

    private ActivityOverview view;

    private Label activeFilter;

    public ActivityControls(ActivityOverview view) {
        super();
        this.view = view;
        this.setContent(createControlGrid());
        this.setText("General controls");
    }

    @NotNull private GridPane createControlGrid() {
        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(5);
        grid.setPadding(new Insets(5, 5, 5, 5));

        Label filterLabel = new Label("Active filter: ");
        filterLabel.getStyleClass().clear();
        filterLabel.getStyleClass().add(DisplayConstants.STYLE_LABEL_SPECIAL);
        activeFilter = new Label(this.view.getActiveFilter());
        grid.add(filterLabel, 0, 0);
        grid.add(activeFilter, 1, 0);

        Button filterButton = new Button("Filter completed");
        filterButton.setOnAction(e -> {
            this.view.setFilterDone(true);
            updateFilterLabel();
            this.view.reloadActivities();
        });
        grid.add(filterButton, 0, 1);

        Button resetFilter = new Button("Reset filter");
        resetFilter.setOnAction(e -> {
            this.view.resetFilter();
            this.view.reloadActivities();
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
                this.view.getActivityController().getActivityManager().save(newActivity);
                this.view.reloadActivities();
            } catch (IOException | ParseException exception) {
                LOG.error("Error creation new activity", exception);
            }
        });
        grid.add(addActivity, 0, 3);

        grid.add(DisplayUtils.createHorizontalSpacer(), 0, 4, 2, 1);

        Button refresh = new Button("");
        refresh.setOnAction(event -> this.view.reloadActivities());
        FontAwesomeIconView glyph = new FontAwesomeIconView(FontAwesomeIcon.REFRESH);
        glyph.setGlyphStyle(DisplayConstants.STYLE_GLYPH_DEFAULT);
        refresh.setGraphic(glyph);

        grid.add(refresh, 0, 5);
        return grid;
    }

    public void updateFilterLabel() {
        activeFilter.setText(view.getActiveFilter());
    }
}
