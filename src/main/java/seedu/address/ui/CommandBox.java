package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";
    private static final double SUGGESTION_POPUP_GAP = 8;

    private final CommandBoxLogic commandBoxLogic;
    private final Popup suggestionPopup;
    private final VBox suggestionContainer;
    private boolean isUpdatingCommandField;
    private long suggestionUpdateVersion;

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandBoxLogic = new CommandBoxLogic(commandExecutor::execute);

        suggestionPopup = new Popup();
        suggestionPopup.setAutoHide(true);
        suggestionPopup.setAutoFix(true);

        suggestionContainer = new VBox();
        suggestionContainer.getStyleClass().add("suggestion-box");
        suggestionContainer.getStylesheets().add(
                requireNonNull(getClass().getResource("/view/DesignSystem.css")).toExternalForm());
        suggestionContainer.setFocusTraversable(false);
        suggestionPopup.getContent().add(suggestionContainer);

        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((_, _, _) -> {
            setStyleToDefault();
            if (!isUpdatingCommandField) {
                commandBoxLogic.resetHistoryNavigation();
            }
            updateSuggestions();
        });
        commandTextField.focusedProperty().addListener((_, _, isFocused) -> {
            if (!isFocused) {
                hideSuggestions();
            } else {
                updateSuggestions();
            }
        });
        commandTextField.setOnMouseClicked(_ -> {
            if (commandTextField.isFocused()) {
                updateSuggestions();
            }
        });
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);

        commandTextField.sceneProperty().addListener((_, _, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((_, _, newWin) -> {
                    if (newWin != null) {
                        newWin.xProperty().addListener((_, _, _) -> hideSuggestions());
                        newWin.yProperty().addListener((_, _, _) -> hideSuggestions());
                        newWin.widthProperty().addListener((_, _, _) -> hideSuggestions());
                        newWin.heightProperty().addListener((_, _, _) -> hideSuggestions());
                    }
                });
            }
        });
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
     * Returns true if the suggestion popup is currently visible.
     */
    public boolean hasSuggestionsVisible() {
        return suggestionPopup.isShowing();
    }

    /**
     * Hides the floating suggestion popup.
     */
    public void hideSuggestions() {
        suggestionUpdateVersion++;
        if (suggestionPopup.isShowing()) {
            suggestionPopup.hide();
        }
        suggestionContainer.getChildren().clear();
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
        } else if (event.getCode() == KeyCode.ESCAPE && suggestionPopup.isShowing()) {
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
     * Updates and displays matching completion suggestions in the floating popup.
     */
    private void updateSuggestions() {
        long currentUpdateVersion = ++suggestionUpdateVersion;
        if (!commandTextField.isFocused()) {
            hideSuggestions();
            return;
        }

        String currentText = commandTextField.getText();
        List<CommandCompletionLogic.Suggestion> suggestions = commandBoxLogic.getSuggestions(currentText);

        if (suggestions.isEmpty()) {
            hideSuggestions();
            return;
        }

        Scene scene = commandTextField.getScene();
        if (scene == null || scene.getWindow() == null || !scene.getWindow().isShowing()) {
            return;
        }

        Bounds inputBarBounds = getRoot().localToScreen(getRoot().getBoundsInLocal());
        if (inputBarBounds == null) {
            return;
        }

        suggestionContainer.getChildren().clear();

        Label hintHeader = new Label("Suggestions (Press Tab to autocomplete):");
        hintHeader.getStyleClass().add("suggestion-header");
        hintHeader.setFocusTraversable(false);
        suggestionContainer.getChildren().add(hintHeader);

        for (CommandCompletionLogic.Suggestion suggestion : suggestions) {
            HBox itemRow = new HBox(8);
            itemRow.getStyleClass().add("suggestion-item");
            itemRow.setFocusTraversable(false);

            Label formatLabel = new Label(suggestion.formatText());
            formatLabel.getStyleClass().add("suggestion-format");
            formatLabel.setFocusTraversable(false);

            Label descLabel = new Label("— " + suggestion.description());
            descLabel.getStyleClass().add("suggestion-description");
            descLabel.setFocusTraversable(false);

            itemRow.getChildren().addAll(formatLabel, descLabel);
            itemRow.setOnMousePressed(event -> {
                setCommandFieldText(suggestion.completionText());
                focus();
                updateSuggestions();
                event.consume();
            });

            suggestionContainer.getChildren().add(itemRow);
        }

        Window window = scene.getWindow();
        double x = inputBarBounds.getMinX();
        suggestionContainer.setMinWidth(inputBarBounds.getWidth());
        suggestionContainer.setMaxWidth(inputBarBounds.getWidth());
        suggestionContainer.applyCss();
        suggestionContainer.layout();
        double popupHeight = suggestionContainer.prefHeight(inputBarBounds.getWidth());
        double y = calculateSuggestionPopupY(window, inputBarBounds, popupHeight);

        if (!suggestionPopup.isShowing()) {
            suggestionPopup.show(window, x, y);
        } else {
            suggestionPopup.setX(x);
            suggestionPopup.setY(y);
        }

        Platform.runLater(() -> repositionSuggestionPopup(
                window, inputBarBounds, currentUpdateVersion));
    }

    /**
     * Repositions the visible popup after JavaFX has laid out its current contents.
     */
    private void repositionSuggestionPopup(Window window, Bounds fieldBounds, long updateVersion) {
        if (updateVersion != suggestionUpdateVersion || window == null || !window.isShowing()
                || !suggestionPopup.isShowing()) {
            return;
        }

        suggestionContainer.applyCss();
        suggestionContainer.layout();
        double currentHeight = suggestionContainer.prefHeight(fieldBounds.getWidth());
        if (!Double.isFinite(currentHeight) || currentHeight <= 0) {
            return;
        }

        suggestionPopup.setY(calculateSuggestionPopupY(window, fieldBounds, currentHeight));
    }

    /**
     * Calculates a popup Y coordinate above the command field and inside the application window where possible.
     */
    private double calculateSuggestionPopupY(Window window, Bounds fieldBounds, double popupHeight) {
        if (window == null || fieldBounds == null || !Double.isFinite(popupHeight) || popupHeight < 0) {
            return fieldBounds == null ? 0 : fieldBounds.getMinY() - SUGGESTION_POPUP_GAP;
        }

        double preferredY = fieldBounds.getMinY() - popupHeight - SUGGESTION_POPUP_GAP;
        double windowTop = window.getY();
        double windowBottom = windowTop + window.getHeight();
        if (!Double.isFinite(windowTop) || !Double.isFinite(windowBottom) || windowBottom <= windowTop) {
            return preferredY;
        }

        double lowestAllowedY = windowBottom - popupHeight;
        if (lowestAllowedY < windowTop) {
            return windowTop;
        }
        return Math.max(windowTop, Math.min(preferredY, lowestAllowedY));
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
