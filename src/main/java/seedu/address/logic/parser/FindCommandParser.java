package seedu.address.logic.parser;

import java.util.Arrays;
import java.util.List;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate.MatchMode;

/**
 * Parses input arguments and creates a new FindCommand object.
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     *
     * @throws ParseException if the arguments contain no search keywords.
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(FindCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        }

        String[] arguments = trimmedArgs.split("\\s+");
        boolean isAnyKeywordSearch = arguments[0].equals(FindCommand.ANY_KEYWORD_OPTION);
        int firstKeywordIndex = isAnyKeywordSearch ? 1 : 0;
        if (arguments.length == firstKeywordIndex) {
            throw new ParseException(FindCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        }

        List<String> nameKeywords = Arrays.stream(arguments)
                .skip(firstKeywordIndex)
                .toList();
        MatchMode matchMode = isAnyKeywordSearch ? MatchMode.ANY : MatchMode.ALL;

        return new FindCommand(new NameContainsKeywordsPredicate(nameKeywords, matchMode));
    }

}
