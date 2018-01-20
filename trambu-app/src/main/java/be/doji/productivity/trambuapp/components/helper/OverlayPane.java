package be.doji.productivity.trambuapp.components.helper;

import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OverlayPane extends BorderPane {

    private Node content;
    private List<Button> controls = new ArrayList<>();

    public OverlayPane() {
        this(null);
    }

    public OverlayPane(Node overlayContent) {
        super();
        this.content = overlayContent;
        this.setStyle(DisplayConstants.STYLE_ACTIVTY_OVERLAY);
        this.setTop(createCloseButton());
        this.setCenter(createContent());
        if (!this.controls.isEmpty()) {
            this.setBottom(createOptionalControls());
        }

        this.setVisible(false);
    }

    @NotNull public Node createContent() {
        if (content == null) {
            return new Label("OVERLAY GOES HERE!!!!!");
        } else {
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(this.content);
            return scrollPane;
        }
    }

    @NotNull public Button createCloseButton() {
        Button closeOverlay = new Button();
        closeOverlay.setGraphic(DisplayUtils.createStyledIcon(FontAwesomeIcon.CLOSE));
        closeOverlay.setOnAction(event -> this.setVisible(false));
        return closeOverlay;
    }

    public Node getContent() {
        return content;
    }

    public void setContent(Node content) {
        this.content = content;
    }

    public void refreshContent() {
        this.setCenter(this.createContent());
        if (!this.controls.isEmpty()) {
            this.setBottom(createOptionalControls());
        }
    }

    public void setControlButtons(List<Button> control) {
        this.controls.clear();
        this.controls.addAll(control);
    }

    private Node createOptionalControls() {
        HBox optionalControls = new HBox();
        optionalControls.setSpacing(3.5);
        for (Button button : controls) {
            optionalControls.getChildren().add(button);
        }

        return optionalControls;
    }
}
