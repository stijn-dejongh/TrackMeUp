package be.doji.productivity.trambuapp.components.view;

import be.doji.productivity.trambuapp.components.presenter.OptionsPresenter;
import be.doji.productivity.trambuapp.controls.MainMenuBar;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambuapp.utils.TooltipConstants;
import java.io.File;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tornadofx.View;

public class OptionsView extends View {

  private static final Logger LOG = LoggerFactory.getLogger(OptionsView.class);

  private BorderPane root;
  private OptionsPresenter presenter;
  private Button todoButton;
  private Button timeButton;
  private Button noteButton;
  private Button saveButton;


  public OptionsView() {
    super();
    LOG.info("Creating OptionsView page");
    this.presenter = new OptionsPresenter(this);

    initControls();
    initPage();
  }

  private void initControls() {
    this.todoButton = initTodoFileSelectButton();
    this.timeButton = initTimeFileButton();
    this.noteButton = initNotesSelectButton();
    this.saveButton = initSavePreferencesButton();
  }

  private void initPage() {
    this.setTitle(DisplayConstants.TITLE_APPLICATION + " - " + DisplayConstants.TITLE_OPTIONS);

    this.root = new BorderPane();
    root.setPrefHeight(DisplayConstants.UI_DEFAULT_WINDOW_HEIGHT);
    root.setPrefWidth(DisplayConstants.UI_DEFAULT_WINDOW_WIDTH);

    root.setCenter(createControlGrid());
    root.setBottom(new MainMenuBar(this).getRoot());
  }

  @NotNull
  GridPane createControlGrid() {
    GridPane grid = new GridPane();
    grid.getStyleClass().add(DisplayConstants.STYLE_CLASS_DEFAULT);
    grid.setVgap(4);
    grid.setPadding(new Insets(5, 5, 5, 5));

    int rowCounter = 0;
    grid.add(new Label("Todo file: "), 0, rowCounter);
    grid.add(this.todoButton, 1, rowCounter++);

    grid.add(new Label("Timetracking file: "), 0, rowCounter);
    grid.add(this.timeButton, 1, rowCounter++);

    grid.add(new Label("Notes directory: "), 0, rowCounter);
    grid.add(this.noteButton, 1, rowCounter++);

    grid.add(DisplayUtils.createHorizontalSpacer(), 0, rowCounter++, 2, 1);

    grid.add(this.saveButton, 0, rowCounter);
    return grid;
  }

  @NotNull
  private Button initTodoFileSelectButton() {
    FileChooser todoFileChooser = new FileChooser();
    todoFileChooser.setTitle("Open TODO list File");

    Button openTodoButton = createOpenFileButton("Select TODO file", todoFileChooser,
        file -> this.presenter.todoFileSelectClicked(file));
    openTodoButton.setTooltip(
        DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_OPTIONS_TODO_FILE_SELECT));
    return openTodoButton;
  }

  @NotNull
  private Button initTimeFileButton() {
    FileChooser timeFileChooser = new FileChooser();
    timeFileChooser.setTitle("Open time tracking File");

    Button openTimeButton = createOpenFileButton("Select timelog file", timeFileChooser,
        file -> this.presenter.timeFileSelectClicked(file));
    openTimeButton.setTooltip(
        DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_OPTIONS_TIME_FILE_SELECT));
    return openTimeButton;
  }

  @NotNull
  private Button initNotesSelectButton() {
    DirectoryChooser notesDirectoryChooser = new DirectoryChooser();
    notesDirectoryChooser.setTitle("Open notes directory");

    Button notesDirectoryButton = createOpenDirectoryButton("Select notes directory",
        notesDirectoryChooser,
        file -> this.presenter.noteDirectorySelectClicked(file));
    notesDirectoryButton
        .setTooltip(
            DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_OPTIONS_TIME_FILE_SELECT));
    return notesDirectoryButton;
  }

  @NotNull
  private Button initSavePreferencesButton() {
    Button savePreferences = new Button("Save preferences");
    savePreferences.setOnAction(event -> this.presenter.savePreferencesClicked());
    savePreferences
        .setTooltip(DisplayUtils.createTooltip(TooltipConstants.TOOLTIP_TEXT_OPTIONS_REMEMBER));
    return savePreferences;
  }

  private Button createOpenFileButton(String buttonText, FileChooser fileChooser,
      Consumer<File> fileLambda) {
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

  public Button getTodoButton() {
    return todoButton;
  }

  public Button getTimeButton() {
    return timeButton;
  }

  public Button getNoteButton() {
    return noteButton;
  }

  public Button getSaveButton() {
    return saveButton;
  }

  @NotNull
  @Override
  public Parent getRoot() {
    return root;
  }

  public OptionsPresenter getPresenter() {
    return this.presenter;
  }
}
