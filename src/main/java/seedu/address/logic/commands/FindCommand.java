package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds and lists students whose name or selected profile fields match the supplied keywords.
 * Keyword matching is case insensitive and allows partial matches.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";
    public static final String ANY_KEYWORD_OPTION = "--any";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " [" + ANY_KEYWORD_OPTION + "] "
            + "KEYWORD [MORE_KEYWORDS]... or [FIELD/]VALUE...\n\n"
            + "Unprefixed keywords search names. Supported fields are n/ name, p/ phone, e/ email, "
            + "a/ address, s/ subject, l/ level, r/ notes, and t/ tag.\n"
            + "By default, find shows students matching all specified criteria; use `--any` to match at least one.\n\n"
            + "Examples:\n"
            + "- \"find alice bob\" returns students whose names contain both “alice” and “bob”.\n"
            + "- \"find s/Mathematics l/JC 1\" returns matching subject and level records.\n"
            + "- \"find --any t/exam-prep t/group-class\" returns students with either tag.";

    public static final String MESSAGE_INVALID_COMMAND_FORMAT =
            "Invalid command format!\n\nUsage: " + MESSAGE_USAGE;

    private final Predicate<Person> predicate;

    /**
     * Creates a command that lists students matching the given name predicate.
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
