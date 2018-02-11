package be.doji.productivity.trambuapp.components.view;

import be.doji.productivity.trambuapp.components.TrambuAppTest;
import java.io.IOException;
import java.nio.file.Path;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the OptionsView MVP model. Testing both OptionsView and OptionsPresenter classes
 */
public class OptionsViewTest extends TrambuAppTest {

  private static final int AMOUNT_OF_CONTROL_BUTTONS = 4;

  @Test
  public void failOnFaultyInitialization() {
    OptionsView view = new OptionsView();

    Assert.assertNotNull("Expect a view to be created", view);
    Assert.assertNotNull("Expect there to be a root pane", view.getRoot());
    Assert.assertTrue("Assert the root pane to be a BorderedPane",
        view.getRoot() instanceof BorderPane);
    Assert.assertNotNull("Expect the note button to be created", view.getNoteButton());
    Assert.assertNotNull("Expect the time button to be created", view.getTimeButton());
    Assert.assertNotNull("Expect the todo button to be created", view.getTodoButton());
    Assert.assertNotNull("Expect the save button to be created", view.getSaveButton());

  }

  @Test
  public void failOnFaultyContentInitialization() {
    OptionsView view = new OptionsView();
    Assert.assertNotNull("Expect a view to be created", view);
    Assert.assertNotNull("Expect there to be a root pane", view.getRoot());
    Assert.assertTrue("Assert the root pane to be a BorderedPane",
        view.getRoot() instanceof BorderPane);

    BorderPane castedRoot = (BorderPane) view.getRoot();
    Node content = castedRoot.getCenter();

    Assert.assertTrue("Expect the content to be a GridPane", content instanceof GridPane);
    GridPane castedContent = (GridPane) content;
    Assert.assertFalse("Expect the content to have fields", castedContent.getChildren().isEmpty());
    Assert.assertTrue("Expect the content to contain at least the control buttons",
        castedContent.getChildren().size() > AMOUNT_OF_CONTROL_BUTTONS);
  }

  @Test
  public void failIfTodoFileLocationNotUpdated() throws IOException {
    OptionsView view = new OptionsView();
    Assert.assertNotNull("Expect a view to be created", view);
    Path tempFile = createTempFile();
    Assert.assertTrue("Expect the temp file to exist", tempFile.toFile().exists());

    view.getPresenter().todoFileSelectClicked(tempFile.toFile());

    Path todoFileLocation = getMockActController().getActivityManager().getTodoFileLocation();
    Assert.assertNotNull(todoFileLocation);
    Assert.assertEquals("Todo file location should be updated", tempFile, todoFileLocation);
  }

  @Test
  public void failIfTimeFileLocationNotUpdated() throws IOException {
    OptionsView view = new OptionsView();
    Assert.assertNotNull("Expect a view to be created", view);
    Path tempDirectory = createTempDirectory();
    Assert.assertTrue("Expect the temp directory to exist", tempDirectory.toFile().exists());

    view.getPresenter().noteDirectorySelectClicked(tempDirectory.toFile());

    Path noteDirectory = getMockActController().getNoteManager().getNoteDirectory();
    Assert.assertNotNull(noteDirectory);
    Assert.assertEquals("Note directory location should be updated", tempDirectory, noteDirectory);
  }

}
