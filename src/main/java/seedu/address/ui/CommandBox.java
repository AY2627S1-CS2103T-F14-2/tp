package seedu.address.ui;

import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private final CommandBoxLogic commandBoxLogic;
    private boolean isUpdatingCommandField;

    @FXML
    private TextField commandTextField;

    @FXML
    private VBox suggestionContainer;


    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandBoxLogic = new CommandBoxLogic(commandExecutor::execute);
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> {
            setStyleToDefault();
            if (!isUpdatingCommandField) {
                commandBoxLogic.resetHistoryNavigation();
            }
            updateSuggestions();
        });
        commandTextField.focusedProperty().addListener((observable, oldValue, isFocused) -> {
            if (!isFocused) {
                hideSuggestions();
            }
        });
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
    }

    /**
     * Focuses on the command text field and positions the caret at the end of the text.
     */
    public void focus() {
        commandTextField.requestFocus();
        commandTextField.positionCaret(commandTextField.getText().length());
    }

    /**
     * Returns true if the command text field currently has focus.
     */
    public boolean isFocused() {
        return commandTextField.isFocused();
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        hideSuggestions();
        try {
            if (commandBoxLogic.executeCommand(commandText)) {
                commandTextField.setText("");
            }
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Handles keyboard events including autocomplete on Tab and history navigation.
     */
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.TAB) {
            Optional<String> completed = commandBoxLogic.autocomplete(commandTextField.getText());
            if (completed.isPresent()) {
                setCommandFieldText(completed.get());
                updateSuggestions();
            }
            event.consume();
        } else if (event.getCode() == KeyCode.ESCAPE && suggestionContainer != null
                && suggestionContainer.isVisible()) {
            hideSuggestions();
            event.consume();
        } else {
            handleCommandHistoryNavigation(event);
        }
    }

    /**
     * Handles navigating through previously entered commands with the arrow keys.
     */
    private void handleCommandHistoryNavigation(KeyEvent event) {
        if (!commandBoxLogic.shouldNavigateHistory(event.isControlDown(), event.isAltDown(), event.isMetaDown())) {
            return;
        }

        if (event.getCode() == KeyCode.UP) {
            setCommandFieldText(commandBoxLogic.previousCommand(commandTextField.getText()));
            event.consume();
        } else if (event.getCode() == KeyCode.DOWN) {
            setCommandFieldText(commandBoxLogic.nextCommand(commandTextField.getText()));
            event.consume();
        }
    }

    /**
     * Updates and displays matching completion suggestions based on current input.
     */
    private void updateSuggestions() {
        if (suggestionContainer == null) {
            return;
        }

        String currentText = commandTextField.getText();
        List<CommandCompletionLogic.Suggestion> suggestions = commandBoxLogic.getSuggestions(currentText);

        if (suggestions.isEmpty() || !commandTextField.isFocused()) {
            hideSuggestions();
            return;
        }

        suggestionContainer.getChildren().clear();

        Label hintHeader = new Label("Suggestions (Press Tab to autocomplete):");
        hintHeader.getStyleClass().add("suggestion-header");
        suggestionContainer.getChildren().add(hintHeader);

        int count = 0;
        for (CommandCompletionLogic.Suggestion suggestion : suggestions) {
            if (count++ >= 5) {
                break;
            }

            HBox itemRow = new HBox(8);
            itemRow.getStyleClass().add("suggestion-item");

            Label formatLabel = new Label(suggestion.formatText());
            formatLabel.getStyleClass().add("suggestion-format");

            Label descLabel = new Label("— " + suggestion.description());
            descLabel.getStyleClass().add("suggestion-description");

            itemRow.getChildren().addAll(formatLabel, descLabel);
            itemRow.setOnMouseClicked(event -> {
                setCommandFieldText(suggestion.completionText());
                focus();
                updateSuggestions();
            });

            suggestionContainer.getChildren().add(itemRow);
        }

        suggestionContainer.setManaged(true);
        suggestionContainer.setVisible(true);
    }

    /**
     * Hides the suggestion container.
     */
    private void hideSuggestions() {
        if (suggestionContainer != null) {
            suggestionContainer.setVisible(false);
            suggestionContainer.setManaged(false);
            suggestionContainer.getChildren().clear();
        }
    }

    /**
     * Updates the command field without resetting the history navigation state.
     */
    private void setCommandFieldText(String commandText) {
        isUpdatingCommandField = true;
        commandTextField.setText(commandText);
        commandTextField.positionCaret(commandText.length());
        isUpdatingCommandField = false;
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

}
