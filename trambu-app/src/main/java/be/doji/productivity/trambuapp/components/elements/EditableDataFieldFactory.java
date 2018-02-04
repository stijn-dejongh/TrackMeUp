package be.doji.productivity.trambuapp.components.elements;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class EditableDataFieldFactory {

    private EditableDataFieldFactory() {
    }

    public static EditableDataField<Label, TextField, String> getEditableStringField(String fieldName) {
        DataContainerDefinition<TextField, String> editableDefinition = new DataContainerDefinition<>(new TextField(),
                TextField::setText, TextField::getText);
        DataContainerDefinition<Label, String> staticDefinition = new DataContainerDefinition<>(new Label(),
                Label::setText, Label::getText);

        return new EditableDataField<>(staticDefinition, editableDefinition, fieldName);
    }

    public static EditableDataField<Label, AutocompleteTextField, String> getEditableStringFieldWithAutocomplete(
            String fieldName) {
        DataContainerDefinition<AutocompleteTextField, String> editableDefinition = new DataContainerDefinition<>(
                new AutocompleteTextField(), AutocompleteTextField::setText, AutocompleteTextField::getText);
        DataContainerDefinition<Label, String> staticDefinition = new DataContainerDefinition<>(new Label(),
                Label::setText, Label::getText);

        return new EditableDataField<>(staticDefinition, editableDefinition, fieldName);
    }

    public static EditableDataField<Label, ComboBox<String>, String> getEditableStringFieldDropdown(
            ObservableList<String> options, String fieldName) {
        ComboBox<String> comboBox = new ComboBox<>(options);
        DataContainerDefinition<ComboBox<String>, String> editableDefinition = new DataContainerDefinition<>(comboBox,
                ComboBox::setValue, ComboBox::getValue);
        DataContainerDefinition<Label, String> staticDefinition = new DataContainerDefinition<>(new Label(),
                Label::setText, Label::getText);

        return new EditableDataField<>(staticDefinition, editableDefinition, fieldName);
    }

    public static EditableDataField<HBox, AutocompleteTextField, List<String>> getEditableStringListFieldWithAutocomplete(
            Consumer<String> buttonAction, String fieldName) {
        HBox dataHbox = new HBox();

        DataContainerDefinition<HBox, List<String>> staticDefinition = new DataContainerDefinition<>(dataHbox,
                (hbox, datalist) -> datalist.stream().map(item -> {
                    Button button = new Button(item);
                    EventHandler<ActionEvent> action = e -> buttonAction.accept(item);
                    button.setOnAction(action);
                    return button;
                }).collect(Collectors.toList()).stream().forEach(button -> dataHbox.getChildren().add(button)),
                hbox -> hbox.getChildren().stream().map(button -> {
                    Button casted = (Button) button;
                    return casted.getText();
                }).collect(Collectors.toList()));

        

        return null;
    }
}
