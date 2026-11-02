package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.formatStudentListOverview;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.PersonMatchesFieldsPredicate;
import seedu.address.model.person.PersonMatchesFieldsPredicate.MatchMode;
import seedu.address.model.person.PersonMatchesFieldsPredicate.SearchField;
import seedu.address.model.person.PersonMatchesFieldsPredicate.SearchTerm;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        PersonMatchesFieldsPredicate firstPredicate = preparePredicate("first");
        PersonMatchesFieldsPredicate secondPredicate = preparePredicate("second");

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different predicates -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        FindCommand command = new FindCommand(preparePredicate("Alice"));

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_allMode_oneStudentFound() {
        String expectedMessage = formatStudentListOverview(1);
        PersonMatchesFieldsPredicate predicate = preparePredicate("Carl Kurz");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(CARL), model.getFilteredPersonList());
    }

    @Test
    public void execute_anyMode_multipleStudentsFound() {
        String expectedMessage = formatStudentListOverview(3);
        PersonMatchesFieldsPredicate predicate = prepareAnyTermPredicate("Kurz", "Elle", "Kunz");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(CARL, ELLE, FIONA), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        PersonMatchesFieldsPredicate predicate = preparePredicate("keyword");
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Creates a name-search predicate from {@code userInput}.
     */
    private PersonMatchesFieldsPredicate preparePredicate(String userInput) {
        return new PersonMatchesFieldsPredicate(List.of(new SearchTerm(SearchField.NAME, userInput)), MatchMode.ALL);
    }

    private PersonMatchesFieldsPredicate prepareAnyTermPredicate(String... userInputs) {
        List<SearchTerm> searchTerms = java.util.Arrays.stream(userInputs)
                .map(userInput -> new SearchTerm(SearchField.NAME, userInput))
                .toList();
        return new PersonMatchesFieldsPredicate(searchTerms, MatchMode.ANY);
    }
}
