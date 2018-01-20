package be.doji.productivity.trambuapp.components.helper;

import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import org.jetbrains.annotations.NotNull;

public class OverlayPane extends BorderPane {

    private Node content;

    public OverlayPane() {
        this(null);
    }

    public OverlayPane(Node overlayContent) {
        super();
        this.content = overlayContent;
        this.setStyle(DisplayConstants.STYLE_ACTIVTY_OVERLAY);
        this.setTop(createCloseButton());
        this.setCenter(createContent());
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
    }
}
