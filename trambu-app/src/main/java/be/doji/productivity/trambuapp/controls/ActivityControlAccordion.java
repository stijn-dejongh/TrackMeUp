package be.doji.productivity.trambuapp.controls;

import be.doji.productivity.trambuapp.components.presenter.ActivityPresenter;
import be.doji.productivity.trambuapp.components.view.ActivityView;
import javafx.scene.control.Accordion;

public class ActivityControlAccordion extends Accordion {

    private final ActivityControls generalControls;

    public ActivityControlAccordion(ActivityPresenter parent) {
        super();
        this.generalControls = new ActivityControls(parent);
        this.getPanes().add(generalControls);

        this.setExpandedPane(generalControls);
    }

    public void updateFilterLabel() {
        this.generalControls.updateFilterLabel();
    }
}
