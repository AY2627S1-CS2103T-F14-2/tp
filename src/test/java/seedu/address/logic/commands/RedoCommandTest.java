package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class RedoCommandTest {
    @Test
    public void execute_availableHistory_restoresRosterAndClearsFilter() throws Exception {
        Model model = new ModelManager();
        model.addPerson(ALICE);
        model.commitAddressBook();
        model.undoAddressBook();
        model.updateFilteredPersonList(person -> false);
        CommandResult result = new RedoCommand().execute(model);
        assertEquals(RedoCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(List.of(ALICE), model.getAddressBook().getPersonList());
        assertEquals(List.of(ALICE), model.getFilteredPersonList());
    }

    @Test
    public void execute_noHistory_throwsCommandException() {
        Model model = new ModelManager();
        assertThrows(CommandException.class, RedoCommand.MESSAGE_FAILURE, () -> new RedoCommand().execute(model));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RedoCommand().execute(null));
    }
}
