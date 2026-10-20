package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;

public class VersionedAddressBookTest {
    @Test
    public void initialState_cannotUndoOrRedo() {
        VersionedAddressBook roster = new VersionedAddressBook(new AddressBook());
        assertFalse(roster.canUndo());
        assertFalse(roster.canRedo());
        assertThrows(IllegalStateException.class, roster::undo);
        assertThrows(IllegalStateException.class, roster::redo);
    }

    @Test
    public void undoRedo_multipleChanges_restoresRecordsAndOrder() {
        AddressBook initial = new AddressBook();
        initial.addPerson(ALICE);
        initial.addPerson(BENSON);
        VersionedAddressBook roster = new VersionedAddressBook(initial);
        Person edited = new Person("Alice Pauline", "+6591234567", null,
                List.of("English", "Mathematics"), "Secondary 4");
        roster.setPerson(ALICE, edited);
        roster.commit();
        roster.removePerson(BENSON);
        roster.commit();

        roster.undo();
        assertEquals(List.of(edited, BENSON), roster.getPersonList());
        roster.undo();
        assertEquals(initial, roster);
        assertFalse(roster.canUndo());
        assertThrows(IllegalStateException.class, roster::undo);
        roster.redo();
        assertEquals(List.of(edited, BENSON), roster.getPersonList());
        roster.redo();
        assertEquals(List.of(edited), roster.getPersonList());
        assertFalse(roster.canRedo());
        assertThrows(IllegalStateException.class, roster::redo);
    }

    @Test
    public void commit_afterUndo_discardsRedoButNoOpPreservesIt() {
        VersionedAddressBook roster = new VersionedAddressBook(new AddressBook());
        roster.addPerson(ALICE);
        roster.commit();
        roster.undo();
        roster.commit();
        assertTrue(roster.canRedo());

        roster.addPerson(BENSON);
        roster.commit();
        assertFalse(roster.canRedo());
        roster.undo();
        assertTrue(roster.getPersonList().isEmpty());
        assertFalse(roster.canUndo());
        roster.redo();
        assertEquals(List.of(BENSON), roster.getPersonList());
    }

    @Test
    public void copy_independentChanges_preservesOriginalHistory() {
        VersionedAddressBook original = new VersionedAddressBook(new AddressBook());
        original.addPerson(ALICE);
        original.commit();
        VersionedAddressBook copy = new VersionedAddressBook(original);
        copy.undo();
        copy.addPerson(BENSON);
        copy.commit();

        original.undo();
        original.redo();
        assertEquals(List.of(ALICE), original.getPersonList());
        assertEquals(List.of(BENSON), copy.getPersonList());
    }

    @Test
    public void undo_uncommittedChange_detectsMissingCommit() {
        VersionedAddressBook roster = new VersionedAddressBook(new AddressBook());
        roster.addPerson(ALICE);
        roster.commit();
        roster.addPerson(BENSON);
        assertThrows(AssertionError.class, roster::undo);
    }

    @Test
    public void assertions_areEnabledForHistoryTests() {
        assertTrue(VersionedAddressBook.class.desiredAssertionStatus());
    }
}
