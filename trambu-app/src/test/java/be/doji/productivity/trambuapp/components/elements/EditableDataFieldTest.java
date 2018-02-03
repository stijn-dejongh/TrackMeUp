package be.doji.productivity.trambuapp.components.elements;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

public class EditableDataFieldTest extends ApplicationTest {

    private static final String CHANGED_TEXT = "ChangedData";
    private static final String DEFAULT_TEXT = "Test";

    @Test public void testCreation() {
        TextField editableField = new TextField();
        Label staticField = new Label();

        DataContainerDefinition<TextField, String> editableDefinition = new DataContainerDefinition<>(editableField,
                TextField::setText, TextField::getText);
        DataContainerDefinition<Label, String> staticDefinition = new DataContainerDefinition<>(staticField,
                Label::setText, Label::getText);

        EditableDataField<Label, TextField, String> dataField = new EditableDataField<>(staticDefinition,
                editableDefinition, DEFAULT_TEXT);
        Assert.assertNotNull(dataField);
        Assert.assertNotNull(dataField.getEditableField());
        Assert.assertNotNull(dataField.getStaticField());
        Assert.assertNotNull(dataField.get());
        Assert.assertEquals(DEFAULT_TEXT, dataField.getData());
    }

    @Test public void testGetStaticField() {
        TextField editableField = new TextField();
        Label staticField = new Label();

        DataContainerDefinition<TextField, String> editableDefinition = new DataContainerDefinition<>(editableField,
                TextField::setText, TextField::getText);
        DataContainerDefinition<Label, String> staticDefinition = new DataContainerDefinition<>(staticField,
                Label::setText, Label::getText);

        EditableDataField<Label, TextField, String> dataField = new EditableDataField<>(staticDefinition,
                editableDefinition, DEFAULT_TEXT);

        dataField.makeStatic();
        Assert.assertEquals(Label.class, dataField.get().getClass());
    }

    @Test public void testGetEditableField() {
        TextField editableField = new TextField();
        Label staticField = new Label();

        DataContainerDefinition<TextField, String> editableDefinition = new DataContainerDefinition<>(editableField,
                TextField::setText, TextField::getText);
        DataContainerDefinition<Label, String> staticDefinition = new DataContainerDefinition<>(staticField,
                Label::setText, Label::getText);

        EditableDataField<Label, TextField, String> dataField = new EditableDataField<>(staticDefinition,
                editableDefinition, DEFAULT_TEXT);

        dataField.makeEditable();
        Assert.assertEquals(TextField.class, dataField.get().getClass());
    }

    @Test public void testSetData() {
        TextField editableField = new TextField();
        Label staticField = new Label();

        DataContainerDefinition<TextField, String> editableDefinition = new DataContainerDefinition<>(editableField,
                TextField::setText, TextField::getText);
        DataContainerDefinition<Label, String> staticDefinition = new DataContainerDefinition<>(staticField,
                Label::setText, Label::getText);

        EditableDataField<Label, TextField, String> dataField = new EditableDataField<>(staticDefinition,
                editableDefinition, DEFAULT_TEXT);

        dataField.setData(CHANGED_TEXT);

        Assert.assertEquals(CHANGED_TEXT, dataField.getData());

        Assert.assertEquals(CHANGED_TEXT, dataField.getStaticField().getData());
        Assert.assertEquals(CHANGED_TEXT, dataField.getStaticField().getDataContainer().getText());

        Assert.assertEquals(CHANGED_TEXT, dataField.getEditableField().getData());
        Assert.assertEquals(CHANGED_TEXT, dataField.getEditableField().getDataContainer().getText());
    }

    @Test public void testUpdate() {
        TextField editableField = new TextField();
        Label staticField = new Label();

        DataContainerDefinition<TextField, String> editableDefinition = new DataContainerDefinition<>(editableField,
                TextField::setText, TextField::getText);
        DataContainerDefinition<Label, String> staticDefinition = new DataContainerDefinition<>(staticField,
                Label::setText, Label::getText);

        EditableDataField<Label, TextField, String> dataField = new EditableDataField<>(staticDefinition,
                editableDefinition, DEFAULT_TEXT);

        Assert.assertEquals(DEFAULT_TEXT, dataField.getData());
        Assert.assertEquals(DEFAULT_TEXT, dataField.getStaticField().getData());
        Assert.assertEquals(DEFAULT_TEXT, dataField.getEditableField().getData());

        editableField.setText(CHANGED_TEXT);
        dataField.update();

        Assert.assertEquals(CHANGED_TEXT, dataField.getData());
        Assert.assertEquals(CHANGED_TEXT, dataField.getStaticField().getData());
        Assert.assertEquals(CHANGED_TEXT, dataField.getEditableField().getData());
    }
}
