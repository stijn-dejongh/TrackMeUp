package be.doji.productivity.trambuapp.controls;

import be.doji.productivity.trambuapp.views.ActivityOverview;
import javafx.scene.control.Accordion;

public class ActivityControlAccordion extends Accordion {

    private final ActivityControls generalControls;

    private ActivityOverview parent;

    public ActivityControlAccordion(ActivityOverview parent) {
        super();
        this.parent = parent;
        this.generalControls = new ActivityControls(parent);
        this.getPanes().add(generalControls);

        this.setExpandedPane(generalControls);
    }

    public void updateFilterLabel() {
        this.generalControls.updateFilterLabel();
    }
}
