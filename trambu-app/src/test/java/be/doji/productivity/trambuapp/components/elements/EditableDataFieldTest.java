package be.doji.productivity.trambuapp.components.elements;

import be.doji.productivity.trambuapp.components.TrambuAppTest;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.Assert;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditableDataFieldTest extends TrambuAppTest {

    private static final String CHANGED_TEXT = "ChangedData";
    private static final String DEFAULT_TEXT = "Test";
    private static final String DATA_STRING_DEFAULT = "2017-12-21";

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

    @Test public void testSetDataDifferentContentTypes() throws ParseException {
        /**
         *   Due to datepicker internal date representation, all time information is truncated.
         *  To test the validity of working with containers that have different internal representations,
         *  we changed the date format to the SHORT type.
         */
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateFromString = dateFormat.parse(DATA_STRING_DEFAULT);

        DatePicker picker = new DatePicker();
        Label dateLabel = new Label();

        DataContainerDefinition<DatePicker, Date> editableDefinition = new DataContainerDefinition<>(picker,
                (datePicker, date) -> datePicker.setValue(new java.sql.Date(date.getTime()).toLocalDate()),
                datePicker -> java.sql.Date.valueOf(datePicker.getValue()));
        DataContainerDefinition<Label, Date> staticDefinition = new DataContainerDefinition<>(dateLabel,
                (label, date) -> label.setText(dateFormat.format(date)), label -> {
            try {
                return dateFormat.parse(label.getText());
            } catch (ParseException e) {
                return new Date();
            }
        });

        EditableDataField<Label, DatePicker, Date> dataField = new EditableDataField<>(staticDefinition,
                editableDefinition, dateFromString);

        Assert.assertNotNull(dataField);
        Assert.assertEquals(dateFromString, dataField.getData());
        Assert.assertEquals(dateFromString, dataField.getStaticField().getData());
        Assert.assertEquals(dateFromString, dataField.getEditableField().getData());

    }
}
