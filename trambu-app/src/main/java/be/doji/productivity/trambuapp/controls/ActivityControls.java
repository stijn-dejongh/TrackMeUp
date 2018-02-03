package be.doji.productivity.trambuapp.controls;

import be.doji.productivity.trambuapp.components.presenter.ActivityPagePresenter;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambuapp.utils.TooltipConstants;
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

public class ActivityControls extends TitledPane {

    private static final Logger LOG = LoggerFactory.getLogger(ActivityControls.class);

    private ActivityPagePresenter presenter;

    private Label activeFilter;

    public ActivityControls(ActivityPagePresenter view) {
        super();
        this.presenter = view;
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
        activeFilter = new Label(this.presenter.getActiveFilter());
        grid.add(filterLabel, 0, 0);
        grid.add(activeFilter, 1, 0);

        grid.add(createFilterCompletedButton(), 0, 1);
        grid.add(createResetFilterButton(), 1, 1);
        grid.add(DisplayUtils.createHorizontalSpacer(), 0, 2, 2, 1);
        grid.add(createAddActivityButton(), 0, 3);
        grid.add(DisplayUtils.createHorizontalSpacer(), 0, 4, 2, 1);

        grid.add(createRefreshButton(), 0, 5);
        return grid;
    }

    @NotNull private Button createRefreshButton() {
        Button refresh = new Button("");
        refresh.setOnAction(event -> this.presenter.refresh());
        FontAwesomeIconView glyph = DisplayUtils.createStyledIcon(FontAwesomeIcon.REFRESH);
        refresh.setGraphic(glyph);
        refresh.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_CONTROL_REFRESH));
        return refresh;
    }

    @NotNull private Button createAddActivityButton() {
        Button addActivity = new Button("Add activity");
        FontAwesomeIconView addIcon = DisplayUtils.createStyledIcon(FontAwesomeIcon.PLUS_CIRCLE);
        addActivity.setGraphic(addIcon);

        addActivity.setOnAction(event -> {
            Activity newActivity = new Activity("EDIT ME I AM A NEW ACTIVITY");
            this.presenter.saveActivity(newActivity);
            this.presenter.refresh();

        });

        addActivity.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_CONTROL_CREATE));
        return addActivity;
    }

    @NotNull private Button createResetFilterButton() {
        Button resetFilter = new Button("Reset filter");
        resetFilter.setOnAction(e -> {
            this.presenter.resetFilter();
            this.presenter.refresh();
        });
        resetFilter.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_CONTROL_FILTER_RESET));
        return resetFilter;
    }

    @NotNull private Button createFilterCompletedButton() {
        Button filterButton = new Button("Filter completed");
        filterButton.setOnAction(e -> {
            this.presenter.setFilterDone(true);
            updateFilterLabel();
            this.presenter.refresh();
        });
        filterButton.setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_CONTROL_FILTER_DONE));
        return filterButton;
    }

    public void updateFilterLabel() {
        activeFilter.setText(presenter.getActiveFilter());
    }
}
