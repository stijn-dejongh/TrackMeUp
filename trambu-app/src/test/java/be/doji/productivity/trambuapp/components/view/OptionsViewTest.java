package be.doji.productivity.trambuapp.components.view;

import be.doji.productivity.trambuapp.components.TrambuAppTest;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.junit.Assert;
import org.junit.Test;

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

  //GridPane
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

  
}
