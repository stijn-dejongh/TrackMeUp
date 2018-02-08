package be.doji.productivity.trambuapp.components.elements;

import be.doji.productivity.trambuapp.components.TrambuAppTest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.junit.Assert;
import org.junit.Test;

public class SwitchableFactoryTest extends TrambuAppTest {

  @Test
  public void failOverDataAddedMultipleTimes() {
    Switchable<HBox, AutocompleteTextField, List<String>> field = SwitchableFactory
        .textGroupAutocompleteSwitchable(s -> {
        }, "fieldName");
    List<String> originalData = Arrays.asList("ItemOne", "ItemTwo");
    Assert.assertEquals(2, originalData.size());
    field.setData(originalData);
    Assert.assertEquals(2, field.getData().size());
    Assert.assertEquals(2, field.getStatic().getDisplayItem().getChildren().size());
    Assert
        .assertEquals(2, field.getEditable().getDisplayItem().getText().split(",").length);

    field.makeEditable();
    Assert.assertEquals(2, field.getData().size());
    Assert.assertEquals(2, field.getStatic().getDisplayItem().getChildren().size());
    Assert
        .assertEquals(2, field.getEditable().getDisplayItem().getText().split(",").length);

    List<String> newData = Arrays.asList("ItemThree", "ItemFour");
    field.setData(newData);
    Assert.assertEquals(2, field.getData().size());
    Assert.assertEquals(2, field.getStatic().getDisplayItem().getChildren().size());
    Assert
        .assertEquals(2, field.getEditable().getDisplayItem().getText().split(",").length);
  }

  @Test
  public void failOverNullPointerWhenValuePassing() {
    Switchable<Label, DatePicker, Date> field =
        SwitchableFactory.dateSwitchable(e -> {
        }, "Default name");
    Assert.assertFalse(field.hasData());
    Assert.assertNull(field.getData());

    field.update();

    // If the test gets here, no NullPointer was thrown
    Assert.assertNull(field.getData());
    Assert.assertNull(field.getEditable().getData());
    Assert.assertNull(field.getStatic().getData());
  }

  @Test
  public void textToButtonConversion() {
    Switchable<HBox, AutocompleteTextField, List<String>> field = SwitchableFactory
        .textGroupAutocompleteSwitchable(onclick -> {
        }, "We like buttons");

    field.setData(Arrays.asList("One", "Two", "Three"));
    HBox container = field.getStatic().getDisplayItem();
    ObservableList<Node> buttons = container.getChildren();
    Assert.assertNotNull(buttons);
    Assert.assertEquals(3, buttons.size());
    for (Node button : buttons) {
      Assert.assertTrue(button instanceof Button);
    }
  }

  @Test
  public void emptyTextToButtonConversion() {
    Switchable<HBox, AutocompleteTextField, List<String>> field = SwitchableFactory
        .textGroupAutocompleteSwitchable(onclick -> {
        }, "We like buttons");

    field.setData(Arrays.asList(""));
    HBox container = field.getStatic().getDisplayItem();
    ObservableList<Node> buttons = container.getChildren();
    Assert.assertNotNull(buttons);
    Assert.assertEquals(0, buttons.size());
  }
}
