package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;

/**
 * Adds or replaces a remark for a person identified using the displayed index.
 */
public class RemarkCommand extends Command {

    public static final String COMMAND_WORD = "remark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a remark to the person identified by the index number used in the displayed person list. "
            + "An existing remark is replaced. An empty remark removes it.\n"
            + "Parameters: INDEX (must be a positive integer) r/REMARK\n"
            + "Example: " + COMMAND_WORD + " 1 r/Likes to swim.";

    public static final String MESSAGE_ADD_REMARK_SUCCESS = "Added remark to Person: %1$s";
    public static final String MESSAGE_DELETE_REMARK_SUCCESS = "Removed remark from Person: %1$s";

    private final Index targetIndex;
    private final Remark remark;

    /**
     * Creates a {@code RemarkCommand} to update the person at {@code targetIndex}.
     */
    public RemarkCommand(Index targetIndex, Remark remark) {
        requireNonNull(targetIndex);
        requireNonNull(remark);
        this.targetIndex = targetIndex;
        this.remark = remark;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(targetIndex.getZeroBased());
        Person editedPerson = new Person(personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                personToEdit.getAddress(), remark, personToEdit.getTags());
        model.setPerson(personToEdit, editedPerson);

        String successMessage = remark.value.isEmpty()
                ? MESSAGE_DELETE_REMARK_SUCCESS
                : MESSAGE_ADD_REMARK_SUCCESS;
        return new CommandResult(String.format(successMessage, Messages.format(editedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RemarkCommand otherRemarkCommand)) {
            return false;
        }

        return targetIndex.equals(otherRemarkCommand.targetIndex)
                && remark.equals(otherRemarkCommand.remark);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("remark", remark)
                .toString();
    }
}
