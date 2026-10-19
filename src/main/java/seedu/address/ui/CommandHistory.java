package seedu.address.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores commands entered during the current application session and supports
 * navigating through them while preserving the user's unfinished command.
 */
final class CommandHistory {

    private final List<String> commands = new ArrayList<>();
    private int currentIndex;
    private String draft = "";

    /**
     * Adds a command to the history unless it duplicates the most recent command.
     *
     * @param command command to add
     */
    void add(String command) {
        if (commands.isEmpty() || !commands.get(commands.size() - 1).equals(command)) {
            commands.add(command);
        }
        resetNavigation();
    }

    /**
     * Returns the previous command, or the current text if no history exists.
     *
     * @param currentText text currently in the command field
     * @return previous command text
     */
    String previous(String currentText) {
        if (commands.isEmpty()) {
            return currentText;
        }

        if (currentIndex == commands.size()) {
            draft = currentText;
        }
        if (currentIndex > 0) {
            currentIndex--;
        }
        return commands.get(currentIndex);
    }

    /**
     * Returns the next command, or the preserved draft after the newest command.
     *
     * @param currentText text currently in the command field
     * @return next command text
     */
    String next(String currentText) {
        if (commands.isEmpty()) {
            return currentText;
        }
        if (currentIndex < commands.size() - 1) {
            currentIndex++;
            return commands.get(currentIndex);
        }

        currentIndex = commands.size();
        return draft;
    }

    /**
     * Resets navigation after the user edits the command field manually.
     */
    void resetNavigation() {
        currentIndex = commands.size();
        draft = "";
    }
}
