package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;

/** Contains tests for {@code ShowCommand}. */
public class ShowCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_returnsSelectedStudent() throws CommandException {
        Person expectedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        CommandResult result = new ShowCommand(INDEX_FIRST_PERSON).execute(model);

        assertEquals(expectedPerson, result.getPersonToShow().orElseThrow());
    }

    @Test
    public void execute_validIndexFilteredList_usesDisplayedOrder() throws CommandException {
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Meier")));
        Person expectedPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());

        CommandResult result = new ShowCommand(INDEX_SECOND_PERSON).execute(model);

        assertEquals(expectedPerson, result.getPersonToShow().orElseThrow());
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        CommandException exception = assertThrows(CommandException.class, ()
                -> new ShowCommand(invalidIndex).execute(model));
        assertEquals(ShowCommand.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, exception.getMessage());
    }

    @Test
    public void execute_emptyFilteredList_throwsCommandException() {
        model.updateFilteredPersonList(person -> false);

        CommandException exception = assertThrows(CommandException.class, ()
                -> new ShowCommand(INDEX_FIRST_PERSON).execute(model));
        assertEquals(ShowCommand.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, exception.getMessage());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ShowCommand(INDEX_FIRST_PERSON).execute(null));
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ShowCommand(null));
    }

    @Test
    public void equals() {
        ShowCommand showFirstCommand = new ShowCommand(INDEX_FIRST_PERSON);
        ShowCommand showSecondCommand = new ShowCommand(INDEX_SECOND_PERSON);

        assertTrue(showFirstCommand.equals(showFirstCommand));
        assertTrue(showFirstCommand.equals(new ShowCommand(INDEX_FIRST_PERSON)));
        assertFalse(showFirstCommand.equals(showSecondCommand));
        assertFalse(showFirstCommand.equals(null));
        assertFalse(showFirstCommand.equals("show 1"));
    }

    @Test
    public void toStringMethod() {
        ShowCommand showCommand = new ShowCommand(INDEX_FIRST_PERSON);
        String expected = ShowCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON + "}";

        assertEquals(expected, showCommand.toString());
    }
}
