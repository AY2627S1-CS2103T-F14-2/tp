package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds and lists all students matching the supplied field-specific search terms.
 * Keyword matching is case insensitive and allows partial matches.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";
    public static final String ANY_KEYWORD_OPTION = "--any";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " [" + ANY_KEYWORD_OPTION + "] "
            + "[n/NAME]... [p/PHONE]... [e/EMAIL]... [a/ADDRESS]... [s/SUBJECT]... [l/LEVEL]... [t/TAG]...\n\n"
            + "Supply at least one field. Fields may appear in any order.\n\n"
            + "By default, find shows students matching every supplied field. Use `--any` to show students "
            + "matching at least one. All field searches are case-insensitive and support partial matching.\n\n"
            + "Examples:\n"
            + "- \"find n/Alex A s/Mathematics\" returns students whose name contains both “Alex A” and study "
            + "Mathematics.\n"
            + "- \"find --any n/Alex A s/Mathematics\" returns students whose name contains “Alex A” or study "
            + "Mathematics."
            + "- \"find --any t/exam-prep t/group-class\" returns students with either tag.";

    public static final String MESSAGE_INVALID_COMMAND_FORMAT =
            "Invalid command format.\nUsage: " + MESSAGE_USAGE;

    private final Predicate<Person> predicate;

    /**
     * Creates a command that lists students matching the given predicate.
     */
    public FindCommand(Predicate<Person> predicate) {
        this.predicate = requireNonNull(predicate);
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
