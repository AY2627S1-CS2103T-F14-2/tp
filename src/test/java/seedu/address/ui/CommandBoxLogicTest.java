package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

public class CommandBoxLogicTest {

    @Test
    public void constructor_nullExecutor_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CommandBoxLogic(null));
    }

    @Test
    public void executeCommand_emptyInput_doesNotExecute() throws Exception {
        AtomicReference<String> executedCommand = new AtomicReference<>();
        CommandBoxLogic logic = new CommandBoxLogic(commandText -> {
            executedCommand.set(commandText);
            return new CommandResult("success");
        });

        assertFalse(logic.executeCommand(""));
        assertNull(executedCommand.get());
    }

    @Test
    public void executeCommand_nullInput_throwsNullPointerException() {
        CommandBoxLogic logic = new CommandBoxLogic(commandText -> new CommandResult("success"));

        assertThrows(NullPointerException.class, () -> logic.executeCommand(null));
    }

    @Test
    public void executeCommand_validInput_executesAndReturnsTrue() throws Exception {
        AtomicReference<String> executedCommand = new AtomicReference<>();
        CommandBoxLogic logic = new CommandBoxLogic(commandText -> {
            executedCommand.set(commandText);
            return new CommandResult("success");
        });

        assertTrue(logic.executeCommand("list"));
        assertEquals("list", executedCommand.get());
    }

    @Test
    public void executeCommand_commandFailure_recordsCommandInHistory() {
        CommandBoxLogic logic = new CommandBoxLogic(commandText -> {
            throw new CommandException("command failed");
        });

        assertThrows(CommandException.class, () -> logic.executeCommand("invalid"));
        assertEquals("invalid", logic.previousCommand("draft"));
    }

    @Test
    public void executeCommand_parseFailure_recordsCommandInHistory() {
        CommandBoxLogic logic = new CommandBoxLogic(commandText -> {
            throw new ParseException("parse failed");
        });

        assertThrows(ParseException.class, () -> logic.executeCommand("invalid"));
        assertEquals("invalid", logic.previousCommand("draft"));
    }

    @Test
    public void commandHistory_navigationPreservesDraft() throws Exception {
        CommandBoxLogic logic = new CommandBoxLogic(commandText -> new CommandResult("success"));
        logic.executeCommand("list");
        logic.executeCommand("find Ryan");

        assertEquals("find Ryan", logic.previousCommand("draft"));
        assertEquals("list", logic.previousCommand("find Ryan"));
        assertEquals("find Ryan", logic.nextCommand("list"));
        assertEquals("draft", logic.nextCommand("find Ryan"));
    }

    @Test
    public void commandHistory_emptyHistory_preservesCurrentText() {
        CommandBoxLogic logic = new CommandBoxLogic(commandText -> new CommandResult("success"));

        assertEquals("draft", logic.previousCommand("draft"));
        assertEquals("draft", logic.nextCommand("draft"));
    }

    @Test
    public void resetHistoryNavigation_discardsPreviousDraft() throws Exception {
        CommandBoxLogic logic = new CommandBoxLogic(commandText -> new CommandResult("success"));
        logic.executeCommand("list");
        logic.previousCommand("old draft");

        logic.resetHistoryNavigation();

        assertEquals("list", logic.previousCommand("new draft"));
        assertEquals("new draft", logic.nextCommand("list"));
    }

    @Test
    public void shouldNavigateHistory_withoutModifiers_returnsTrue() {
        CommandBoxLogic logic = new CommandBoxLogic(commandText -> new CommandResult("success"));

        assertTrue(logic.shouldNavigateHistory(false, false, false));
    }

    @Test
    public void shouldNavigateHistory_withControlAltOrMeta_returnsFalse() {
        CommandBoxLogic logic = new CommandBoxLogic(commandText -> new CommandResult("success"));

        assertFalse(logic.shouldNavigateHistory(true, false, false));
        assertFalse(logic.shouldNavigateHistory(false, true, false));
        assertFalse(logic.shouldNavigateHistory(false, false, true));
    }

    @Test
    public void getSuggestions_delegatesToCompletionLogic() {
        CommandBoxLogic logic = new CommandBoxLogic(commandText -> new CommandResult("success"));

        List<CommandCompletionLogic.Suggestion> suggestions = logic.getSuggestions("sh");
        assertEquals(1, suggestions.size());
        assertEquals("show ", suggestions.get(0).completionText());
    }

    @Test
    public void autocomplete_delegatesToCompletionLogic() {
        CommandBoxLogic logic = new CommandBoxLogic(commandText -> new CommandResult("success"));

        Optional<String> completed = logic.autocomplete("sh");
        assertTrue(completed.isPresent());
        assertEquals("show ", completed.get());
    }
}
