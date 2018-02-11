package be.doji.productivity.trambuapp.components.view;

import be.doji.productivity.trambuapp.components.TrambuAppTest;
import be.doji.productivity.trambuapp.controls.ActivityControlAccordion;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambucore.testutil.ActivityTestData;
import java.text.ParseException;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
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

  @Test
  public void failIfActivityAccordeonIsFaulty() throws ParseException {
    // Add two activities, one has a deadline, one has not
    getActivityManager().addActivity(ActivityTestData.ACTIVITY_DATA_LINE);
    getActivityManager().addActivity(ActivityTestData.DATA_LINE_NO_DEADLINE);
    Assert.assertEquals(2, getActivityManager().getAllActivityNames().size());
    ActivityPageView pageView = new ActivityPageView();
    Assert.assertNotNull(pageView);
    Accordion activityAccordion = pageView.getActivityAccordion();
    Assert.assertNotNull(activityAccordion);

    pageView.getPresenter().refresh();

    ObservableList<TitledPane> panes = activityAccordion.getPanes();
    Assert.assertNotNull(panes);
    Assert.assertFalse("Expect the ActivityPageView to contain panes", panes.isEmpty());
    Assert.assertEquals(4, panes.size());
    List<TitledPane> headingPanes = pageView.getHeadingPanes();
    Assert.assertFalse("Expect there to be heading panes", headingPanes.isEmpty());
    Assert.assertEquals(2, headingPanes.size());
    List<ActivityView> activityPanes = pageView.getActivityPanes();
    Assert.assertFalse("Expect there to be acitvity panes", activityPanes.isEmpty());
    Assert.assertEquals(2, activityPanes.size());
  }

  @Test
  public void failIfControlAccordeonIsFaulty() {
    ActivityPageView pageView = new ActivityPageView();
    Assert.assertNotNull(pageView);

    ActivityControlAccordion controlAccordion = pageView.getControlAccordion();
    Assert.assertNotNull("Expect there to be a control accordeon after creation", controlAccordion);
    ObservableList<TitledPane> controlPanes = controlAccordion.getPanes();
    Assert.assertFalse("Expect the control accordeon to have content", controlPanes.isEmpty());
    Assert.assertEquals("The should currently be one control pane", 1, controlPanes.size());
  }

  @Test
  public void failIfProjectFilterIsFaulty() throws ParseException {
    getActivityManager().addActivity(ActivityTestData.ACTIVITY_DATA_LINE);
    getActivityManager().addActivity(ActivityTestData.NO_PREFIX_DATA_LINE);
    getActivityManager().addActivity(ActivityTestData.COMPLETED_ACTIVITY);
    Assert.assertEquals(3, getActivityManager().getAllActivityNames().size());
    ActivityPageView pageView = new ActivityPageView();
    pageView.getPresenter().refresh();
    Assert.assertNotNull("Expect a PageView to be created", pageView);
    Assert.assertEquals(3, pageView.getActivityPanes().size());

    pageView.getPresenter().setProjectFilter("OverarchingProject");
    pageView.getPresenter().refresh();

    Assert.assertEquals(1, pageView.getActivityPanes().size());
  }

  @Test
  public void failIfProjectFilterResetIsFaulty() throws ParseException {
    getActivityManager().addActivity(ActivityTestData.ACTIVITY_DATA_LINE);
    getActivityManager().addActivity(ActivityTestData.NO_PREFIX_DATA_LINE);
    getActivityManager().addActivity(ActivityTestData.COMPLETED_ACTIVITY);
    Assert.assertEquals(3, getActivityManager().getAllActivityNames().size());
    ActivityPageView pageView = new ActivityPageView();
    pageView.getPresenter().refresh();
    Assert.assertNotNull("Expect a PageView to be created", pageView);
    Assert.assertEquals(3, pageView.getActivityPanes().size());
    pageView.getPresenter().setProjectFilter("OverarchingProject");
    pageView.getPresenter().refresh();
    Assert.assertEquals(1, pageView.getActivityPanes().size());

    pageView.getPresenter().resetFilter();
    pageView.getPresenter().refresh();

    Assert.assertEquals(3, pageView.getActivityPanes().size());
  }

  


}
