package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ShowCommand;

/** Contains tests for {@code ShowCommandParser}. */
public class ShowCommandParserTest {

    private final ShowCommandParser parser = new ShowCommandParser();

    @Test
    public void parse_validArgs_returnsShowCommand() {
        assertParseSuccess(parser, "1", new ShowCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, "  1  ", new ShowCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "", ShowCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, null, ShowCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "0", ShowCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "1 2", ShowCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "2147483648", ShowCommand.MESSAGE_INVALID_COMMAND_FORMAT);
    }
}
