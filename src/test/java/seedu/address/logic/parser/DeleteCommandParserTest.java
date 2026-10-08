package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteCommand;

/**
 * Tests parsing and specification feedback for displayed student indices.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new DeleteCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_surroundingWhitespace_returnsDeleteCommand() {
        assertParseSuccess(parser, "  1  ", new DeleteCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String[] invalidArguments = {"", " ", "0", "-1", "1.5", "abc", "1 n/Ryan", "1 2", "+1", "2147483648"};
        for (String arguments : invalidArguments) {
            assertParseFailure(parser, arguments, "Invalid command format. Usage: delete INDEX");
        }
    }
}
