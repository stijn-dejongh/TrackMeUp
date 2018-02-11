package be.doji.productivity.trambuapp.components.presenter;

import be.doji.productivity.trambuapp.components.view.OptionsView;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OptionsPresenter extends Presenter {

  private static final Logger LOG = LoggerFactory.getLogger(OptionsPresenter.class);

  private final OptionsView view;
  private final ManagerContainer managers;

  private String configuredTodoLocation;
  private String configuredTimeLocation;
  private String configuredNoteLocation;

  public OptionsPresenter(OptionsView view) {
    this.view = view;
    this.managers = ManagerContainer.Factory.getInstance();
  }


  public void timeFileSelectClicked(File file) {
    try {
      String filePath = file.getAbsolutePath();
      this.configuredTimeLocation = filePath;
      this.managers.getTimeTrackingManager().updateFileLocation(filePath);
    } catch (IOException | ParseException e) {
      LOG.error("Error opening time tracking file", e);
    }
  }


  public void todoFileSelectClicked(File file) {
    try {
      String filePath = file.getAbsolutePath();
      this.configuredTodoLocation = filePath;
      this.managers.getActivityManager().updateFileLocation(filePath);
    } catch (IOException | ParseException e) {
      LOG.error("Error opening todo file", e);
    }
  }


  public void noteDirectorySelectClicked(File directory) {
    try {
      String filePath = directory.getAbsolutePath();
      this.configuredNoteLocation = filePath;
      this.managers.getNoteManager().updateLocation(filePath);
    } catch (IOException e) {
      LOG.error("Error opening time tracking file", e);
    }
  }


  public void savePreferencesClicked() {
    if (StringUtils.isNotBlank(configuredTodoLocation)) {
      this.managers.getConfigManager()
          .addProperty(DisplayConstants.NAME_PROPERTY_TODO_LOCATION, configuredTodoLocation);
    }
    if (StringUtils.isNotBlank(configuredTimeLocation)) {
      this.managers.getConfigManager()
          .addProperty(DisplayConstants.NAME_PROPERTY_TIME_LOCATION, configuredTimeLocation);
    }

    if (StringUtils.isNotBlank(configuredNoteLocation)) {
      this.managers.getConfigManager()
          .addProperty(DisplayConstants.NAME_PROPERTY_NOTES_LOCATION, configuredNoteLocation);
    }
    try {
      this.managers.getConfigManager().writeToFile();
    } catch (IOException e) {
      LOG.error(DisplayConstants.ERROR_MESSAGE_WRITE_PROPERTIES, e);
    }

  }

  @Override
  void refresh() {
    // Do nothing
  }

  @Override
  void populate() {
    // Nothing to do here
  }
}
