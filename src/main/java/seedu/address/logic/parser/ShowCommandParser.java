package seedu.address.logic.parser;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ShowCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses a displayed student index and creates a {@code ShowCommand}. */
public class ShowCommandParser implements Parser<ShowCommand> {

    /**
     * Parses the given arguments into a command that shows a student from the displayed list.
     *
     * @throws ParseException if the input is not a single positive integer.
     */
    public ShowCommand parse(String args) throws ParseException {
        if (args == null) {
            throw new ParseException(ShowCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        }

        try {
            Index index = ParserUtil.parseIndex(args);
            return new ShowCommand(index);
        } catch (ParseException exception) {
            throw new ParseException(ShowCommand.MESSAGE_INVALID_COMMAND_FORMAT, exception);
        }
    }
}
