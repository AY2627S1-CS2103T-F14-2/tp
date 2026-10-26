package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses the exit command, which accepts no arguments. */
public class ExitCommandParser implements Parser<ExitCommand> {

    /**
     * Parses the supplied arguments and creates an exit command when no arguments are present.
     *
     * @param args arguments supplied after the exit command.
     * @return an exit command.
     * @throws ParseException if arguments are supplied or {@code args} is null.
     */
    @Override
    public ExitCommand parse(String args) throws ParseException {
        if (args == null || !args.strip().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExitCommand.MESSAGE_USAGE));
        }
        return new ExitCommand();
    }
}
