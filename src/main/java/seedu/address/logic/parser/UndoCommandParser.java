package seedu.address.logic.parser;

import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the undo command, which accepts no arguments.
 */
public class UndoCommandParser implements Parser<UndoCommand> {
    @Override
    public UndoCommand parse(String args) throws ParseException {
        if (!args.strip().isEmpty()) {
            throw new ParseException(UndoCommand.MESSAGE_USAGE);
        }
        return new UndoCommand();
    }
}
