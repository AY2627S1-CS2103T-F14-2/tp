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

public class VersionedTutorRosterTest {
    @Test
    public void initialState_cannotUndoOrRedo() {
        VersionedTutorRoster roster = new VersionedTutorRoster(new AddressBook());
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
        VersionedTutorRoster roster = new VersionedTutorRoster(initial);
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
        VersionedTutorRoster roster = new VersionedTutorRoster(new AddressBook());
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
        VersionedTutorRoster original = new VersionedTutorRoster(new AddressBook());
        original.addPerson(ALICE);
        original.commit();
        VersionedTutorRoster copy = new VersionedTutorRoster(original);
        copy.undo();
        copy.addPerson(BENSON);
        copy.commit();

        original.undo();
        original.redo();
        assertEquals(List.of(ALICE), original.getPersonList());
        assertEquals(List.of(BENSON), copy.getPersonList());
    }

    @Test
    public void restoreFrom_selfAndDifferentSource_preservesHistory() {
        VersionedTutorRoster roster = new VersionedTutorRoster(new AddressBook());
        roster.restoreFrom(roster);

        VersionedTutorRoster source = new VersionedTutorRoster(new AddressBook());
        source.addPerson(ALICE);
        source.commit();
        roster.restoreFrom(source);

        assertEquals(List.of(ALICE), roster.getPersonList());
        assertTrue(roster.canUndo());
        assertThrows(NullPointerException.class, () -> roster.restoreFrom(null));
    }

    @Test
    public void undo_uncommittedChange_detectsMissingCommit() {
        VersionedTutorRoster roster = new VersionedTutorRoster(new AddressBook());
        roster.addPerson(ALICE);
        roster.commit();
        roster.addPerson(BENSON);
        assertThrows(AssertionError.class, roster::undo);
    }

    @Test
    public void undo_restoreFails_preservesPointerAndAllowsRetry() {
        FailingRestoreRoster roster = new FailingRestoreRoster();
        roster.addPerson(ALICE);
        roster.commit();
        roster.addPerson(BENSON);
        roster.commit();
        roster.shouldFailRestore = true;

        assertThrows(IllegalStateException.class, "Simulated restoration failure", roster::undo);
        assertEquals(List.of(ALICE, BENSON), roster.getPersonList());
        assertTrue(roster.canUndo());
        assertFalse(roster.canRedo());

        roster.shouldFailRestore = false;
        roster.undo();
        assertEquals(List.of(ALICE), roster.getPersonList());
        roster.redo();
        assertEquals(List.of(ALICE, BENSON), roster.getPersonList());
    }

    @Test
    public void redo_restoreFails_preservesPointerAndAllowsRetry() {
        FailingRestoreRoster roster = new FailingRestoreRoster();
        roster.addPerson(ALICE);
        roster.commit();
        roster.addPerson(BENSON);
        roster.commit();
        roster.undo();
        roster.shouldFailRestore = true;

        assertThrows(IllegalStateException.class, "Simulated restoration failure", roster::redo);
        assertEquals(List.of(ALICE), roster.getPersonList());
        assertTrue(roster.canUndo());
        assertTrue(roster.canRedo());

        roster.shouldFailRestore = false;
        roster.redo();
        assertEquals(List.of(ALICE, BENSON), roster.getPersonList());
    }

    @Test
    public void assertions_areEnabledForHistoryTests() {
        assertTrue(VersionedTutorRoster.class.desiredAssertionStatus());
    }
    /**
     * Simulates a restoration failure before roster data is replaced.
     */
    private static class FailingRestoreRoster extends VersionedTutorRoster {
        private boolean shouldFailRestore;

        FailingRestoreRoster() {
            super(new AddressBook());
        }

        @Override
        public void resetData(ReadOnlyAddressBook newData) {
            if (shouldFailRestore) {
                throw new IllegalStateException("Simulated restoration failure");
            }
            super.resetData(newData);
        }
    }

}
