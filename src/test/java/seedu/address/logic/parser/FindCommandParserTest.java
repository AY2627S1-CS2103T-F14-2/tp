package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesFieldsPredicate;
import seedu.address.model.person.PersonMatchesFieldsPredicate.MatchMode;
import seedu.address.model.person.PersonMatchesFieldsPredicate.SearchField;
import seedu.address.model.person.PersonMatchesFieldsPredicate.SearchTerm;
import seedu.address.model.tag.Tag;

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
        List<SearchTerm> searchTerms = List.of(
                new SearchTerm(SearchField.NAME, "Alex"),
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
    public void parse_tagOnly_returnsFindCommand() {
        List<SearchTerm> searchTerms = List.of(
                new SearchTerm(SearchField.TAG, "exam-prep"));
        FindCommand expectedFindCommand = new FindCommand(
                new PersonMatchesFieldsPredicate(searchTerms, MatchMode.ALL));

        assertParseSuccess(parser, "t/exam-prep", expectedFindCommand);
    }

    @Test
    public void parse_fieldsInDifferentOrder_returnsSameResults() throws ParseException {
        Person alex = new Person("Alex", "123", null, List.of("Math"), "JC 1")
                .withTags(Set.of(new Tag("exam-prep")));
        Person bob = new Person("Bob", "456", null, List.of("Math"), "JC 1")
                .withTags(Set.of(new Tag("exam-prep")));
        Person cam = new Person("Cam", "789", null, List.of("Math"), "JC 2");
        var model = new ModelManager();
        model.addPerson(alex);
        model.addPerson(bob);
        model.addPerson(cam);

        assertSearchResults(model, "n/Alex l/JC 1 t/exam-prep", List.of(alex));
        assertSearchResults(model, "t/exam-prep l/JC 1 n/Alex", List.of(alex));
        assertSearchResults(model, "--any n/Alex l/JC 1 t/exam-prep", List.of(alex, bob));
        assertSearchResults(model, "--any t/exam-prep l/JC 1 n/Alex", List.of(alex, bob));
    }

    /** Checks the displayed students after parsing and executing a search. */
    private void assertSearchResults(ModelManager model, String arguments, List<Person> expectedStudents)
            throws ParseException {
        parser.parse(arguments).execute(model);
        assertEquals(expectedStudents, model.getFilteredPersonList(), arguments);
    }
}
