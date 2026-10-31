package seedu.address.logic;

import static java.util.Objects.requireNonNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_ERROR_PREFIX = "Error: ";
    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format.\nUsage: %1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The student index provided is invalid.";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    /**
     * Formats an error for display in the application.
     *
     * <p>The formatter deliberately preserves meaningful line breaks, such as those in usage guidance, while
     * ensuring that every displayed error has one consistent prefix and no accidental surrounding whitespace.</p>
     */
    public static String formatError(String message) {
        if (message == null) {
            return MESSAGE_ERROR_PREFIX + "An unexpected error occurred.";
        }
        String trimmedMessage = message.strip().replace("\r\n", "\n").replace('\r', '\n');
        if (trimmedMessage.isEmpty()) {
            return MESSAGE_ERROR_PREFIX + "An unexpected error occurred.";
        }
        return trimmedMessage.startsWith(MESSAGE_ERROR_PREFIX)
                ? trimmedMessage
                : MESSAGE_ERROR_PREFIX + trimmedMessage;
    }

    /** Returns search feedback with a singular or plural student label as appropriate. */
    public static String formatStudentListOverview(int studentCount) {
        String label = studentCount == 1 ? "student" : "students";
        return studentCount + " " + label + " listed!";
    }

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        requireNonNull(duplicatePrefixes);
        for (Prefix prefix : duplicatePrefixes) {
            requireNonNull(prefix);
        }
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        requireNonNull(person);
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Address: ")
                .append(person.getAddress())
                .append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

}
