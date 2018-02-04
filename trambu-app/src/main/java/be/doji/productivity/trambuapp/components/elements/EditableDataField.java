package be.doji.productivity.trambuapp.components.elements;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import java.util.Objects;

public class EditableDataField<S extends Region, E extends Region, D> {

    private String name;
    private D data;
    private boolean editable;

    private DataContainerDefinition<S, D> staticField;
    private DataContainerDefinition<E, D> editableField;

    /*
     * TODO: maybe convert this to a Builder pattern?
     *
     */

    public EditableDataField(DataContainerDefinition<S, D> staticFieldDefition,
            DataContainerDefinition<E, D> editableFieldDefinition) {
        this.staticField = staticFieldDefition;
        this.editableField = editableFieldDefinition;
    }

    public EditableDataField(DataContainerDefinition<S, D> staticFieldDefition,
            DataContainerDefinition<E, D> editableFieldDefinition, String name) {
        this.staticField = staticFieldDefition;
        this.editableField = editableFieldDefinition;
        this.name = name;
    }

    public EditableDataField(DataContainerDefinition<S, D> staticFieldDefition,
            DataContainerDefinition<E, D> editableFieldDefinition, D data, String name) {
        this(staticFieldDefition, editableFieldDefinition, name);
        this.setData(data);
    }

    public Region get() {
        return isEditable()?this.editableField.getDataContainer():this.staticField.getDataContainer();
    }

    public void makeEditable() {
        this.editable = true;
    }

    public void makeStatic() {
        this.editable = false;
    }

    public boolean isEditable() {
        return this.editable;
    }

    public DataContainerDefinition<S, D> getStaticField() {
        return staticField;
    }

    public void setStaticField(DataContainerDefinition<S, D> staticField) {
        this.staticField = staticField;
    }

    public DataContainerDefinition<E, D> getEditableField() {
        return editableField;
    }

    public void setEditableField(DataContainerDefinition<E, D> editableField) {
        this.editableField = editableField;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
        updateFields();
    }

    public EditableDataField<S, E, D> update() {
        this.data = getEditableField().getData();
        updateFields();
        return this;
    }

    private void updateFields() {
        editableField.setData(data);
        staticField.setData(data);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Label getNameLabel() {
        return new Label(this.name);
    }

    public boolean hasData() {
        return !Objects.isNull(data);
    }
}
