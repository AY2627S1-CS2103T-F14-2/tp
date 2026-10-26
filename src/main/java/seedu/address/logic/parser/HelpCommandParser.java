package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses the help command, which accepts no arguments. */
public class HelpCommandParser implements Parser<HelpCommand> {

    /**
     * Parses the supplied arguments and creates a help command when no arguments are present.
     *
     * @param args arguments supplied after the help command.
     * @return a help command.
     * @throws ParseException if arguments are supplied or {@code args} is null.
     */
    @Override
    public HelpCommand parse(String args) throws ParseException {
        if (args == null || !args.strip().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }
        return new HelpCommand();
    }
}
