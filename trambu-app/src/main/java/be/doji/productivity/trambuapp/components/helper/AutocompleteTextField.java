package be.doji.productivity.trambuapp.components.helper;

import be.doji.productivity.trambuapp.utils.DisplayConstants;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class AutocompleteTextField extends TextField {

    private SortedSet<String> suggestions;
    private ContextMenu suggestionsPopup;
    private static final int MAX_AMOUNT_OF_SUGGESTIONS = 10;

    public AutocompleteTextField() {
        super();
        this.suggestions = new TreeSet<>();
        this.suggestionsPopup = new ContextMenu();

        attachListeners();
    }

    private void attachListeners() {
        addTextEntryListener();
        addDefaultListener();
    }

    private void addTextEntryListener() {
        this.textProperty().addListener((observable, oldVal, newVal) -> {
            String currentText = getText();
            if (StringUtils.isBlank(currentText)) {
                suggestionsPopup.hide();
            } else {
                List<String> matchingSuggestions = suggestions.stream().filter(suggestion -> suggestion.toLowerCase()
                        .contains(StringUtils
                                .substringAfter(currentText.toLowerCase(), DisplayConstants.SEPARATOR_TAGS_PROJECTS)))
                        .collect(Collectors.toList());
                if (!matchingSuggestions.isEmpty()) {
                    updateSuggestionPopup(matchingSuggestions, currentText);
                    if (suggestionsPopup.isShowing()) {
                        suggestionsPopup.show(this, Side.BOTTOM, 0, 0);
                    }
                } else {
                    suggestionsPopup.hide();
                }
            }
        });
    }

    private void updateSuggestionPopup(List<String> matchingSuggestions, String currentText) {
        List<CustomMenuItem> items = new ArrayList<>();
        String currentEnteredText = StringUtils
                .substringAfter(currentText.toLowerCase(), DisplayConstants.SEPARATOR_TAGS_PROJECTS);

        for (int i = 0; i < MAX_AMOUNT_OF_SUGGESTIONS && i < matchingSuggestions.size(); i++) {
            String suggestion = matchingSuggestions.get(i);
            Label suggestionLabel = new Label(suggestion);
            CustomMenuItem menuItem = new CustomMenuItem(suggestionLabel, true);
            menuItem.setOnAction(click -> {
                this.setText(currentText.replace(currentEnteredText, suggestion));
                suggestionsPopup.hide();
            });
            items.add(menuItem);
        }

        suggestionsPopup.getItems().clear();
        suggestionsPopup.getItems().addAll(items);
    }

    private void addDefaultListener() {
        this.textProperty().addListener((observable, oldVal, newVal) -> {
            suggestionsPopup.hide();
        });
    }

}
