package seedu.address.logic.parser;

import static seedu.address.logic.commands.DeleteCommand.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a displayed student index and creates a {@code DeleteCommand}.
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    /**
     * Parses the given arguments into a command that deletes a student from the displayed list.
     *
     * @throws ParseException if the input is not a single positive integer.
     */
    public DeleteCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new DeleteCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT, pe);
        }
    }

}
