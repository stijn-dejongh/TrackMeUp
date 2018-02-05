package be.doji.productivity.trambuapp.components.elements;

import be.doji.productivity.trambuapp.utils.DisplayConstants;
import be.doji.productivity.trambuapp.utils.DisplayUtils;
import be.doji.productivity.trambucore.TrackMeConstants;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public final class SwitchableFactory {

  private SwitchableFactory() {
  }

  public static Switchable<Label, DatePicker, Date> dateSwitchable(
      EventHandler<ActionEvent> datepickAction, String fieldName) {
    DatePicker picker = new DatePicker();
    picker.setOnAction(datepickAction);
    DataContainerDefinition<DatePicker, Date> editableDefinition = new DataContainerDefinition<>(
        picker,
        (datePicker, date) -> {
          if (Objects.isNull(date)) {
            datePicker.setValue(null);
          } else {
            datePicker.setValue(new java.sql.Date(date.getTime()).toLocalDate());
          }
        }, datePicker -> datePicker.getValue() == null ? null
        : java.sql.Date.valueOf(datePicker.getValue()));
    DataContainerDefinition<Label, Date> staticDefinition = new DataContainerDefinition<>(
        new Label(),
        (label, date) -> {
          if (Objects.isNull(date)) {
            label.setText("");
          } else {
            label.setText(TrackMeConstants.getDateFormat().format(date));
          }
        }, label -> {
      if (StringUtils.isBlank(label.getText())) {
        return null;
      }
      try {
        return TrackMeConstants.getDateFormat().parse(label.getText());
      } catch (ParseException e) {
        return null;
      }
    });

    return new Switchable<>(staticDefinition, editableDefinition, fieldName);

  }

  public static Switchable<Label, TextField, String> textSwitchable(
      String fieldName) {
    DataContainerDefinition<TextField, String> editableDefinition = new DataContainerDefinition<>(
        new TextField(),
        TextField::setText, TextField::getText);
    DataContainerDefinition<Label, String> staticDefinition = new DataContainerDefinition<>(
        new Label(),
        Label::setText, Label::getText);

    return new Switchable<>(staticDefinition, editableDefinition, fieldName);
  }

  public static Switchable<Label, AutocompleteTextField, String> autocompleTextSwitchable(
      String fieldName) {
    DataContainerDefinition<AutocompleteTextField, String> editableDefinition = new DataContainerDefinition<>(
        new AutocompleteTextField(), AutocompleteTextField::setText,
        AutocompleteTextField::getText);
    DataContainerDefinition<Label, String> staticDefinition = new DataContainerDefinition<>(
        new Label(),
        Label::setText, Label::getText);

    return new Switchable<>(staticDefinition, editableDefinition, fieldName);
  }

  public static Switchable<Label, ComboBox<String>, String> textDropdownSwitchable(
      ObservableList<String> options, String fieldName) {
    ComboBox<String> comboBox = new ComboBox<>(options);
    DataContainerDefinition<ComboBox<String>, String> editableDefinition = new DataContainerDefinition<>(
        comboBox,
        ComboBox::setValue, ComboBox::getValue);
    DataContainerDefinition<Label, String> staticDefinition = new DataContainerDefinition<>(
        new Label(),
        Label::setText, Label::getText);

    return new Switchable<>(staticDefinition, editableDefinition, fieldName);
  }

  public static Switchable<HBox, AutocompleteTextField, List<String>> textGroupAutocompleteSwitchable(
      Consumer<String> buttonAction, String fieldName) {
    DataContainerDefinition<HBox, List<String>> staticDefinition = new DataContainerDefinition<>(
        new HBox(),
        (hbox, datalist) -> {
          hbox.getChildren().clear();
          hbox.getChildren().addAll(
              datalist.stream().filter(string -> StringUtils.isNotBlank(string)).map(Button::new)
                  .collect(Collectors.toList()));
          hbox.getChildren().stream().map(Button.class::cast).forEach(
              button -> button.setOnAction(e -> buttonAction.accept(button.getText())));
        }, hbox -> hbox.getChildren().stream().map(button -> ((Button) button).getText())
        .collect(Collectors.toList()));

    AutocompleteTextField autocompleteTextField = new AutocompleteTextField();
    DataContainerDefinition<AutocompleteTextField, List<String>> editableDefinition = new DataContainerDefinition<>(
        autocompleteTextField, (textField, datalist) -> datalist.stream()
        .reduce((s, s2) -> s + DisplayConstants.FIELD_SEPERATOR + " " + s2)
        .ifPresent(textField::setText),
        label -> DisplayUtils
            .splitTextFieldValueOnSeperator(label.getText(), DisplayConstants.FIELD_SEPERATOR));

    return new Switchable<>(staticDefinition, editableDefinition, fieldName);
  }

  @NotNull
  private static Button createItemButton(String item, Consumer<String> buttonAction) {
    Button itemButton = new Button(item);
    itemButton.setOnAction(e -> buttonAction.accept(item));
    return itemButton;
  }
}
