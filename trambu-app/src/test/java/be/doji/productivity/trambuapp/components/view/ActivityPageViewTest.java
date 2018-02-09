package be.doji.productivity.trambuapp.components.view;

import be.doji.productivity.trambuapp.components.TrambuAppTest;
import be.doji.productivity.trambuapp.utils.DisplayConstants;
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
    ActivityPageView pageView = new ActivityPageView(getMockActController());

    Assert.assertNotNull(pageView);
    Assert.assertFalse("Expect title to not be empty", StringUtils.isBlank(pageView.getTitle()));
    Assert.assertTrue("Expect title to contain application name",
        StringUtils.contains(pageView.getTitle(), DisplayConstants.TITLE_APPLICATION));
  }

  @Test
  public void failIfNoContentCreated() {
    ActivityPageView pageView = new ActivityPageView(getMockActController());

    Assert.assertNotNull(pageView);
    Assert.assertNotNull(pageView.getControlAccordion());
    Assert.assertNotNull(pageView.getActivityAccordion());
  }



}
