package be.doji.productivity.trambuapp.controls;

import be.doji.productivity.trambuapp.views.ActivityOverview;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

public class ActivityControlAccordion extends Accordion {
    
    private final ActivityControls generalControls;

    private ActivityOverview parent;

    public ActivityControlAccordion(ActivityOverview parent) {
        super();
        this.parent = parent;
        TitledPane fileOptionsControls = new FileControls(parent);
        this.getPanes().add(fileOptionsControls);
        this.generalControls = new ActivityControls(parent);
        this.getPanes().add(generalControls);
        if (!parent.isSetFileOptions()) {
            this.setExpandedPane(fileOptionsControls);
        } else {
            this.setExpandedPane(generalControls);
        }

        this.getStylesheets().clear();
        this.getStylesheets().add("style/css/trambu-controls.css");
    }

    public void updateFilterLabel() {
        this.generalControls.updateFilterLabel();
    }
}
