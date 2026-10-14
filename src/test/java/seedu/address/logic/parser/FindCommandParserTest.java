package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.NameContainsKeywordsPredicate;

public class FindCommandParserTest {

    private static final String EXPECTED_INVALID_COMMAND_FORMAT = "Invalid command format!\n\n"
            + "Usage: find [--any] KEYWORD [MORE_KEYWORDS]...\n\n"
            + "By default, find shows students whose names match all specified keywords "
            + "(case-insensitive, partial matches allowed). Use `--any` to show students whose names match "
            + "at least one specified keyword.\n\n"
            + "Examples:\n\n"
            + "- \"find alice bob\" returns students whose names contain both “alice” and “bob”.\n"
            + "- \"find --any alice bob\" returns students whose names contain “alice”, “bob”, or both.";

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "", EXPECTED_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "     ", EXPECTED_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "\t\n\r", EXPECTED_INVALID_COMMAND_FORMAT);
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

}
