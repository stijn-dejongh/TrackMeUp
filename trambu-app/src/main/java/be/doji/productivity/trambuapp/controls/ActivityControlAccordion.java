package be.doji.productivity.trambuapp.controls;

import be.doji.productivity.trambuapp.components.presenter.ActivityPagePresenter;
import javafx.scene.control.Accordion;

public class ActivityControlAccordion extends Accordion {

  private final ActivityControls generalControls;

  public ActivityControlAccordion(ActivityPagePresenter parent) {
    super();
    this.generalControls = new ActivityControls(parent);
    this.getPanes().add(generalControls);

    this.setExpandedPane(generalControls);
  }

  public void updateFilterLabel() {
    this.generalControls.updateFilterLabel();
  }
}
