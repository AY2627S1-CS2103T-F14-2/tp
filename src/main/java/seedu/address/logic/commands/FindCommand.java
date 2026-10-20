package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.NameContainsKeywordsPredicate;

/**
 * Finds and lists all students whose name matches the argument keywords.
 * Keyword matching is case insensitive and allows partial names.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";
    public static final String ANY_KEYWORD_OPTION = "--any";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " [" + ANY_KEYWORD_OPTION + "] "
            + "KEYWORD [MORE_KEYWORDS]...\n\n"
            + "By default, find shows students whose names match all specified keywords "
            + "(case-insensitive, partial matches allowed).\n"
            + "Use `--any` to show students whose names match at least one specified keyword.\n\n"
            + "Examples:\n"
            + "- \"find alice bob\" returns students whose names contain both “alice” and “bob”.\n"
            + "- \"find --any alice bob\" returns students whose names contain “alice”, “bob”, or both.";

    public static final String MESSAGE_INVALID_COMMAND_FORMAT =
            "Invalid command format!\n\nUsage: " + MESSAGE_USAGE;

    private final NameContainsKeywordsPredicate predicate;

    /**
     * Creates a command that lists students matching the given name predicate.
     */
    public FindCommand(NameContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
