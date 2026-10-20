package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate.MatchMode;

public class FindCommandParserTest {

    private static final String EXPECTED_INVALID_COMMAND_FORMAT = FindCommand.MESSAGE_INVALID_COMMAND_FORMAT;

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

    @Test
    public void parse_anyKeywordOption_returnsAnyKeywordFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(
                new NameContainsKeywordsPredicate(List.of("Alice", "Bob"), MatchMode.ANY));

        assertParseSuccess(parser, "--any Alice Bob", expectedFindCommand);
    }

    @Test
    public void parse_anyKeywordOptionWithoutKeywords_throwsParseException() {
        String expectedMessage = EXPECTED_INVALID_COMMAND_FORMAT;

        assertParseFailure(parser, "--any", expectedMessage);
        assertParseFailure(parser, "--any   ", expectedMessage);
    }

}
