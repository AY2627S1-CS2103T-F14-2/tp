package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.PersonMatchesFieldsPredicate;
import seedu.address.model.person.PersonMatchesFieldsPredicate.MatchMode;
import seedu.address.model.person.PersonMatchesFieldsPredicate.SearchField;
import seedu.address.model.person.PersonMatchesFieldsPredicate.SearchTerm;

public class FindCommandParserTest {

    private static final String EXPECTED_INVALID_COMMAND_FORMAT = FindCommand.MESSAGE_INVALID_COMMAND_FORMAT;

    private final FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "", EXPECTED_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "     ", EXPECTED_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "\t\n\r", EXPECTED_INVALID_COMMAND_FORMAT);
    }

    @Test
    public void parse_allMode_returnsFindCommand() {
        List<SearchTerm> searchTerms = List.of(
                new SearchTerm(SearchField.NAME, "Alice Bob"),
                new SearchTerm(SearchField.SUBJECT, "Mathematics"),
                new SearchTerm(SearchField.SUBJECT, "Physics"));
        FindCommand expectedFindCommand = new FindCommand(
                new PersonMatchesFieldsPredicate(searchTerms, MatchMode.ALL));

        assertParseSuccess(parser, "n/Alice Bob s/Mathematics s/Physics", expectedFindCommand);
        assertParseSuccess(parser, " n/Alice Bob  s/Mathematics  s/Physics ", expectedFindCommand);
    }

    @Test
    public void parse_repeatedFieldValues_returnsFindCommand() {
        List<SearchTerm> searchTerms = List.of(new SearchTerm(SearchField.NAME, "Alex"),
                new SearchTerm(SearchField.NAME, "Alexander"),
                new SearchTerm(SearchField.PHONE, "9123"),
                new SearchTerm(SearchField.PHONE, "4567"),
                new SearchTerm(SearchField.EMAIL, "alex"),
                new SearchTerm(SearchField.EMAIL, "example.com"),
                new SearchTerm(SearchField.SUBJECT, "Math"),
                new SearchTerm(SearchField.SUBJECT, "English"),
                new SearchTerm(SearchField.LEVEL, "Secondary"),
                new SearchTerm(SearchField.LEVEL, "4"),
                new SearchTerm(SearchField.TAG, "exam-prep"),
                new SearchTerm(SearchField.TAG, "group-class"));
        FindCommand expectedFindCommand = new FindCommand(
                new PersonMatchesFieldsPredicate(searchTerms, MatchMode.ALL));

        assertParseSuccess(parser, "n/Alex n/Alexander p/9123 p/4567 e/alex e/example.com "
                + "s/Math s/English l/Secondary l/4 t/exam-prep t/group-class", expectedFindCommand);
    }

    @Test
    public void parse_anyMode_returnsFindCommand() {
        List<SearchTerm> searchTerms = List.of(
                new SearchTerm(SearchField.NAME, "Alex A"),
                new SearchTerm(SearchField.EMAIL, "alice@example.com"));
        FindCommand expectedFindCommand = new FindCommand(
                new PersonMatchesFieldsPredicate(searchTerms, MatchMode.ANY));

        assertParseSuccess(parser, "--any n/Alex A e/alice@example.com", expectedFindCommand);
    }

    @Test
    public void parse_invalidFields_throwsParseException() {
        assertParseFailure(parser, "Alice", EXPECTED_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "--any", EXPECTED_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "--any   ", EXPECTED_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "n/", EXPECTED_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "n/Alex p/ s/Mathematics", EXPECTED_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "x/value", "Unknown parameter prefix: x/");
        assertParseFailure(parser, "--anywhere n/Alex", EXPECTED_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "t/", EXPECTED_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "t/exam-prep t/", EXPECTED_INVALID_COMMAND_FORMAT);
    }

    @Test
    public void parse_targetedFieldsWithTag_returnsFindCommand() {
        List<SearchTerm> searchTerms = List.of(
                new SearchTerm(SearchField.SUBJECT, "Mathematics"),
                new SearchTerm(SearchField.LEVEL, "JC 1"),
                new SearchTerm(SearchField.TAG, "exam-prep"));
        FindCommand expectedFindCommand = new FindCommand(
                new PersonMatchesFieldsPredicate(searchTerms, MatchMode.ALL));

        assertParseSuccess(parser, "s/Mathematics l/JC 1 t/exam-prep", expectedFindCommand);
    }

    @Test
    public void parse_tagOnly_returnsFindCommand() {
        List<SearchTerm> searchTerms = List.of(
                new SearchTerm(SearchField.TAG, "exam-prep"));
        FindCommand expectedFindCommand = new FindCommand(
                new PersonMatchesFieldsPredicate(searchTerms, MatchMode.ALL));

        assertParseSuccess(parser, "t/exam-prep", expectedFindCommand);
    }

    @Test
    public void parse_anyModeWithRepeatedTags_returnsFindCommand() {
        List<SearchTerm> searchTerms = List.of(
                new SearchTerm(SearchField.TAG, "exam-prep"),
                new SearchTerm(SearchField.TAG, "group-class"));
        FindCommand expectedFindCommand = new FindCommand(
                new PersonMatchesFieldsPredicate(searchTerms, MatchMode.ANY));

        assertParseSuccess(parser, "--any t/exam-prep t/group-class", expectedFindCommand);
    }

    @Test
    public void parse_fieldsInDifferentOrder_returnsFindCommand() {
        List<SearchTerm> searchTerms = List.of(
                new SearchTerm(SearchField.TAG, "exam-prep"),
                new SearchTerm(SearchField.LEVEL, "JC 1"),
                new SearchTerm(SearchField.NAME, "Alex"));
        FindCommand expectedFindCommand = new FindCommand(
                new PersonMatchesFieldsPredicate(searchTerms, MatchMode.ALL));

        assertParseSuccess(parser, "t/exam-prep l/JC 1 n/Alex", expectedFindCommand);
    }
}
