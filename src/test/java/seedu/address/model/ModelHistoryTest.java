package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.model.person.Person;

public class ModelHistoryTest {
    @Test
    public void snapshot_restoresFilterRosterAndRedo_withoutReplacingObservableList() {
        Model model = new ModelManager();
        ObservableList<Person> displayedList = model.getFilteredPersonList();
        model.addPerson(ALICE);
        model.commitAddressBook();
        model.addPerson(BENSON);
        model.commitAddressBook();
        model.undoAddressBook();
        model.updateFilteredPersonList(person -> person.equals(BENSON));
        ModelSnapshot checkpoint = model.createSnapshot();

        model.redoAddressBook();
        assertEquals(List.of(ALICE, BENSON), displayedList);
        model.restoreSnapshot(checkpoint);
        assertSame(displayedList, model.getFilteredPersonList());
        assertEquals(List.of(ALICE), model.getAddressBook().getPersonList());
        assertEquals(List.of(), displayedList);
        model.redoAddressBook();
        assertEquals(List.of(ALICE, BENSON), displayedList);
        // A checkpoint is reusable and is unaffected by later model changes.
        model.restoreSnapshot(checkpoint);
        assertEquals(List.of(), displayedList);
        model.undoAddressBook();
        assertEquals(List.of(), model.getAddressBook().getPersonList());
    }
}
