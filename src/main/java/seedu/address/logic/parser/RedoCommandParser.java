package seedu.address.logic.parser;

import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the redo command, which accepts no arguments.
 */
public class RedoCommandParser implements Parser<RedoCommand> {
    @Override
    public RedoCommand parse(String args) throws ParseException {
        if (!args.strip().isEmpty()) {
            throw new ParseException(RedoCommand.MESSAGE_USAGE);
        }
        return new RedoCommand();
    }
}
