package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.StudentFields;

/**
 * Adds a student to TutorRoster.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_PARAMETER_GUIDE = "Required: " + COMMAND_WORD + " "
            + PREFIX_NAME + "NAME " + PREFIX_PHONE + "PHONE "
            + PREFIX_SUBJECT + "SUBJECT " + PREFIX_LEVEL + "LEVEL\n"
            + "Optional: " + PREFIX_EMAIL + "EMAIL; " + PREFIX_ADDRESS + "ADDRESS; " + PREFIX_NOTES
            + "NOTES; t/TAG; repeat "
            + PREFIX_SUBJECT
            + "SUBJECT for additional subjects or t/TAG for additional tags.\n"
            + "Parameters may appear in any order.";
    public static final String MESSAGE_USAGE = "Invalid command format.\n" + MESSAGE_PARAMETER_GUIDE;

    public static final String MESSAGE_SUCCESS = "New student added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This student already exists in TutorRoster.";
    public static final String MESSAGE_SAME_NAME_WARNING = "Warning: A student named %1$s is already in the roster. "
            + "Check the contact details to make sure these are different students.";

    private final Person toAdd;
    private final boolean duplicateTagsIgnored;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        this(person, false);
    }

    /** Creates an add command, optionally reporting repeated equivalent tags as a warning. */
    public AddCommand(Person person, boolean duplicateTagsIgnored) {
        requireNonNull(person);
        toAdd = person;
        this.duplicateTagsIgnored = duplicateTagsIgnored;
    }

    public static String missingRequiredParameter(String parameter) {
        return "Missing required parameter: " + parameter + ".\n" + MESSAGE_PARAMETER_GUIDE;
    }

    public static String missingRequiredValue(String parameter) {
        return "Missing value for required parameter: " + parameter + ".\n" + MESSAGE_PARAMETER_GUIDE;
    }

    public Person getPersonToAdd() {
        return toAdd;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasPerson(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        boolean sameNameExists = model.getAddressBook().getPersonList().stream()
                .anyMatch(existing -> StudentFields.nameKey(existing.getName().fullName)
                        .equals(StudentFields.nameKey(toAdd.getName().fullName)));
        model.addPerson(toAdd);
        String feedback = String.format(MESSAGE_SUCCESS, toAdd.getName().fullName);
        if (sameNameExists) {
            feedback += "\n" + String.format(MESSAGE_SAME_NAME_WARNING, toAdd.getName().fullName);
        }
        if (duplicateTagsIgnored) {
            feedback += "\nWarning: Duplicate tags were ignored.";
        }
        return new CommandResult(feedback);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand otherAddCommand)) {
            return false;
        }

        return toAdd.equals(otherAddCommand.toAdd) && duplicateTagsIgnored == otherAddCommand.duplicateTagsIgnored;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
