package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class HistoryCommandParserTest {
    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_undoRedoWithWhitespace_returnsCommand() throws Exception {
        assertInstanceOf(UndoCommand.class, parser.parseCommand("  undo  "));
        assertInstanceOf(RedoCommand.class, parser.parseCommand("\tredo\t"));
    }

    @Test
    public void parseCommand_extraArguments_rejected() {
        for (String args : new String[] {" 1", " all", " n/Ryan"}) {
            assertThrows(ParseException.class, UndoCommand.MESSAGE_USAGE, () -> parser.parseCommand("undo" + args));
            assertThrows(ParseException.class, RedoCommand.MESSAGE_USAGE, () -> parser.parseCommand("redo" + args));
        }
    }
}
