package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the clear command, which accepts no arguments.
 */
public class ClearCommandParser implements Parser<ClearCommand> {

    /**
     * Parses the supplied arguments and creates a clear command when no arguments are present.
     *
     * @param args arguments supplied after the clear command.
     * @return a clear command.
     * @throws ParseException if arguments are supplied or {@code args} is null.
     */
    @Override
    public ClearCommand parse(String args) throws ParseException {
        if (args == null || !args.strip().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE));
        }
        return new ClearCommand();
    }
}
