package be.doji.productivity.trambuapp.components.view;

import be.doji.productivity.trambuapp.components.TrambuAppTest;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the ActivityPageView MVP model. Testing both ActivityPageView and ActvitiyPagePresenter
 * classes
 */
public class ActivityPageViewTest extends TrambuAppTest {

  @Test
  public void failOnBadTitle() {
    ActivityPageView pageView = new ActivityPageView();

    Assert.assertNotNull(pageView);
    Assert.assertFalse("Expect title to not be empty", StringUtils.isBlank(pageView.getTitle()));
    Assert.assertTrue("Expect title to contain application name",
        StringUtils.contains(pageView.getTitle(), DisplayConstants.TITLE_APPLICATION));
  }

  @Test
  public void failIfNoContentCreated() {
    ActivityPageView pageView = new ActivityPageView();

    Assert.assertNotNull(pageView);
    Assert.assertNotNull(pageView.getControlAccordion());
    Assert.assertNotNull(pageView.getActivityAccordion());
  }

  @Test
  public void failIfMenuBarIsFaulty() {
    ActivityPageView pageView = new ActivityPageView();
    Assert.assertNotNull(pageView);
    Parent rootPane = pageView.getRoot();
    Assert.assertNotNull("Expect a root pane to exist after construction", rootPane);
    Assert.assertTrue("Expect root pane to be a BorderPane", rootPane instanceof BorderPane);

    BorderPane castedRoot = (BorderPane) rootPane;
    Node bottomPaneRoot = castedRoot.getBottom();
    Assert.assertNotNull("Expect the rootpane to have a bottom element", bottomPaneRoot);
    Assert.assertTrue("Expect the bottom pane to be a menu", bottomPaneRoot instanceof HBox);
    HBox castedBottomRoot = (HBox) bottomPaneRoot;
    Assert.assertNotNull(castedBottomRoot);
    Assert.assertFalse("Expect menu bar to have content", castedBottomRoot.getChildren().isEmpty());
    Assert.assertEquals(3, castedBottomRoot.getChildren().size());
  }

  


}
