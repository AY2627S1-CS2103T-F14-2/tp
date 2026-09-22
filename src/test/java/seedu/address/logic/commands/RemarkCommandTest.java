package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.testutil.PersonBuilder;

public class RemarkCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_addsRemark() {
        assertRemarkSuccess(INDEX_FIRST_PERSON, new Remark(VALID_REMARK_AMY),
                RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS);
    }

    @Test
    public void execute_emptyRemarkUnfilteredList_removesRemark() {
        assertRemarkSuccess(INDEX_FIRST_PERSON, new Remark(""), RemarkCommand.MESSAGE_DELETE_REMARK_SUCCESS);
    }

    @Test
    public void execute_validIndexFilteredList_addsRemark() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertRemarkSuccess(INDEX_FIRST_PERSON, new Remark(VALID_REMARK_BOB),
                RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        RemarkCommand remarkCommand = new RemarkCommand(outOfBoundIndex, new Remark(VALID_REMARK_AMY));

        assertCommandFailure(remarkCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        RemarkCommand remarkCommand = new RemarkCommand(INDEX_SECOND_PERSON, new Remark(VALID_REMARK_AMY));

        assertCommandFailure(remarkCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        RemarkCommand remarkFirstCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(VALID_REMARK_AMY));
        RemarkCommand remarkFirstCommandCopy = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(VALID_REMARK_AMY));
        RemarkCommand remarkSecondCommand = new RemarkCommand(INDEX_SECOND_PERSON, new Remark(VALID_REMARK_AMY));
        RemarkCommand remarkDifferentTextCommand =
                new RemarkCommand(INDEX_FIRST_PERSON, new Remark(VALID_REMARK_BOB));

        assertTrue(remarkFirstCommand.equals(remarkFirstCommand));
        assertTrue(remarkFirstCommand.equals(remarkFirstCommandCopy));
        assertFalse(remarkFirstCommand.equals(remarkSecondCommand));
        assertFalse(remarkFirstCommand.equals(remarkDifferentTextCommand));
        assertFalse(remarkFirstCommand.equals(null));
        assertFalse(remarkFirstCommand.equals(1));
    }

    @Test
    public void toStringMethod() {
        Remark remark = new Remark(VALID_REMARK_AMY);
        RemarkCommand remarkCommand = new RemarkCommand(INDEX_FIRST_PERSON, remark);
        String expected = RemarkCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON
                + ", remark=" + remark + "}";
        assertEquals(expected, remarkCommand.toString());
    }

    private void assertRemarkSuccess(Index index, Remark remark, String messageTemplate) {
        Person personToEdit = model.getFilteredPersonList().get(index.getZeroBased());
        Person editedPerson = new PersonBuilder(personToEdit).withRemark(remark.value).build();
        RemarkCommand remarkCommand = new RemarkCommand(index, remark);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        if (model.getFilteredPersonList().size() != model.getAddressBook().getPersonList().size()) {
            showPersonAtIndex(expectedModel, index);
        }
        expectedModel.setPerson(personToEdit, editedPerson);

        String expectedMessage = String.format(messageTemplate, Messages.format(editedPerson));
        assertCommandSuccess(remarkCommand, model, expectedMessage, expectedModel);
    }
}
