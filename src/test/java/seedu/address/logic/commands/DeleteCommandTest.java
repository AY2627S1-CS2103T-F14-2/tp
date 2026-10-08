package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = "Student removed: " + personToDelete.getName().fullName;

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, "The student index provided is invalid.");
    }

    @Test
    public void execute_validIndexFilteredList_deletesDisplayedStudentAndShowsFullRoster() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = "Student removed: " + personToDelete.getName().fullName;

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, "The student index provided is invalid.");
    }

    @Test
    public void execute_multipleFilteredStudents_preservesRemainingRosterOrder() {
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of("Meier")));
        assertEquals(2, model.getFilteredPersonList().size());
        assertEquals(DANIEL, model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased()));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(DANIEL);

        assertCommandSuccess(new DeleteCommand(INDEX_SECOND_PERSON), model,
                "Student removed: Daniel Meier", expectedModel);
    }

    @Test
    public void execute_lastDisplayedIndex_success() {
        Index lastIndex = Index.fromOneBased(model.getFilteredPersonList().size());
        Person personToDelete = model.getFilteredPersonList().get(lastIndex.getZeroBased());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(new DeleteCommand(lastIndex), model,
                "Student removed: " + personToDelete.getName().fullName, expectedModel);
    }

    @Test
    public void execute_onlyStudent_leavesEmptyRoster() {
        model = new ModelManager();
        Person student = new PersonBuilder().withName("Ryan Tan").build();
        model.addPerson(student);

        assertCommandSuccess(new DeleteCommand(INDEX_FIRST_PERSON), model,
                "Student removed: Ryan Tan", new ModelManager());
    }

    @Test
    public void execute_emptyRoster_throwsCommandException() {
        model = new ModelManager(new AddressBook(), new UserPrefs());

        assertCommandFailure(new DeleteCommand(INDEX_FIRST_PERSON), model,
                "The student index provided is invalid.");
    }

    @Test
    public void execute_emptyFilteredList_preservesRosterAndFilter() {
        model.updateFilteredPersonList(person -> false);

        assertCommandFailure(new DeleteCommand(INDEX_FIRST_PERSON), model,
                "The student index provided is invalid.");
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteCommand deleteCommand = new DeleteCommand(targetIndex);
        String expected = DeleteCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, deleteCommand.toString());
    }
}
