package be.doji.productivity.trambuapp.components.helper;

import be.doji.productivity.trambuapp.utils.DisplayConstants;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class AutocompleteTextField extends TextField {

    private static final Logger LOG = LoggerFactory.getLogger(AutocompleteTextField.class);

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
        this.textProperty().addListener((observable, oldVal, newVal) -> suggestionForTextListener());

    }

    private void suggestionForTextListener() {
        String currentText = getText();
        if (StringUtils.isBlank(currentText)) {
            suggestionsPopup.hide();
        } else {
            List<String> matchingSuggestions = suggestions.stream()
                    .filter(suggestion -> suggestion.toLowerCase().contains(getLatestEntry(currentText).toLowerCase()))
                    .collect(Collectors.toList());
            if (!matchingSuggestions.isEmpty()) {
                updateSuggestionPopup(matchingSuggestions, currentText);
                if (!suggestionsPopup.isShowing()) {
                    try {
                        suggestionsPopup.show(AutocompleteTextField.this, Side.BOTTOM, 0, 0);
                    } catch (IllegalArgumentException e) {
                        LOG.error("Error while displaying popup with suggestions :" + e.getMessage());
                    }
                }
            } else {
                suggestionsPopup.hide();
            }
        }
    }

    private void updateSuggestionPopup(List<String> matchingSuggestions, String currentText) {
        List<CustomMenuItem> items = new ArrayList<>();
        String currentEnteredText = getLatestEntry(currentText);

        for (int i = 0; i < MAX_AMOUNT_OF_SUGGESTIONS && i < matchingSuggestions.size(); i++) {
            String suggestion = matchingSuggestions.get(i);
            Label suggestionLabel = new Label(suggestion);
            CustomMenuItem menuItem = new CustomMenuItem(suggestionLabel, true);
            menuItem.setOnAction(click -> {
                this.setText(currentText.replace(currentEnteredText, suggestion));
                suggestionsPopup.hide();
                this.positionCaret(this.getText().length());
            });
            items.add(menuItem);
        }

        suggestionsPopup.getItems().clear();
        suggestionsPopup.getItems().addAll(items);
    }

    private void addDefaultListener() {
        this.focusedProperty().addListener((observable, oldVal, newVal) -> suggestionsPopup.hide());
    }

    @NotNull private String getLatestEntry(String currentText) {
        String afterIndicator = StringUtils.substringAfter(currentText, DisplayConstants.SEPARATOR_TAGS_PROJECTS)
                .trim();
        return StringUtils.isBlank(afterIndicator)?currentText:afterIndicator;
    }

    public SortedSet<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(SortedSet<String> suggestions) {
        this.suggestions = suggestions;
    }
}
