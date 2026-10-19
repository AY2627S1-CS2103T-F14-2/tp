package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Contains the command-box behaviour that does not depend on JavaFX controls.
 */
final class CommandBoxLogic {

    private final CommandExecutor commandExecutor;
    private final CommandHistory commandHistory = new CommandHistory();

    /**
     * Creates command-box logic with the executor used by the application.
     *
     * @param commandExecutor command executor
     */
    CommandBoxLogic(CommandExecutor commandExecutor) {
        this.commandExecutor = requireNonNull(commandExecutor);
    }

    /**
     * Executes a non-empty command and records it in the session history.
     *
     * @param commandText command text
     * @return true if a command was executed, false for empty input
     * @throws CommandException if command execution fails
     * @throws ParseException if command parsing fails
     */
    boolean executeCommand(String commandText) throws CommandException, ParseException {
        requireNonNull(commandText);
        if (commandText.equals("")) {
            return false;
        }

        commandHistory.add(commandText);
        commandExecutor.execute(commandText);
        return true;
    }

    /**
     * Returns the previous command in the session history.
     */
    String previousCommand(String currentText) {
        return commandHistory.previous(currentText);
    }

    /**
     * Returns the next command in the session history.
     */
    String nextCommand(String currentText) {
        return commandHistory.next(currentText);
    }

    /**
     * Resets command-history navigation after the user edits the input.
     */
    void resetHistoryNavigation() {
        commandHistory.resetNavigation();
    }

    /**
     * Returns whether an arrow key should navigate command history.
     */
    boolean shouldNavigateHistory(boolean controlDown, boolean altDown, boolean metaDown) {
        return !controlDown && !altDown && !metaDown;
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }
}
