package be.doji.productivity.trambuapp.components.elements;

import javafx.scene.control.Control;

public class EditableDataField<S extends Control, E extends Control, D> {

    private D data;
    private boolean editable;

    private DataContainerDefinition<S, D> staticField;
    private DataContainerDefinition<E, D> editableField;

    public EditableDataField(DataContainerDefinition<S, D> staticFieldDefition,
            DataContainerDefinition<E, D> editableFieldDefinition, D data) {
        this.staticField = staticFieldDefition;
        this.editableField = editableFieldDefinition;
        this.setData(data);
    }

    public Control get() {
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

    public void update() {
        this.data = getEditableField().getData();
        updateFields();
    }

    private void updateFields() {
        editableField.setData(data);
        staticField.setData(data);
    }

}
