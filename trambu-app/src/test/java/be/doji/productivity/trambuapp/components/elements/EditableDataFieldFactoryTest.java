package be.doji.productivity.trambuapp.components.elements;

import be.doji.productivity.trambuapp.components.TrambuAppTest;
import javafx.scene.layout.HBox;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class EditableDataFieldFactoryTest extends TrambuAppTest {


    @Test public void testBugListDataAddedMultipleTimes() {
        EditableDataField<HBox, AutocompleteTextField, List<String>> field = EditableDataFieldFactory
                .getEditableStringListFieldWithAutocomplete(s -> {
                }, "fieldName");
        List<String> originalData = Arrays.asList("ItemOne", "ItemTwo");
        Assert.assertEquals(2, originalData.size());
        field.setData(originalData);
        Assert.assertEquals(2, field.getData().size());
        Assert.assertEquals(2, field.getStaticField().getDataContainer().getChildren().size());
        Assert.assertEquals(2, field.getEditableField().getDataContainer().getText().split(",").length);

        field.makeEditable();
        Assert.assertEquals(2, field.getData().size());
        Assert.assertEquals(2, field.getStaticField().getDataContainer().getChildren().size());
        Assert.assertEquals(2, field.getEditableField().getDataContainer().getText().split(",").length);

        List<String> newData = Arrays.asList("ItemThree", "ItemFour");
        field.setData(newData);
        Assert.assertEquals(2, field.getData().size());
        Assert.assertEquals(2, field.getStaticField().getDataContainer().getChildren().size());
        Assert.assertEquals(2, field.getEditableField().getDataContainer().getText().split(",").length);
    }
}
