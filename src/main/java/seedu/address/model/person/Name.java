package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid name. Names must be 1–100 characters and contain at least one letter or number. "
            + "Only letters, numbers, spaces, apostrophes, hyphens and full stops are allowed.";

    /*
     * The first character of the name must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{L}\\p{N} '.-]+";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = StudentText.normalize(name);
    }

    private Name(String value, boolean legacy) {
        fullName = value;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        String normalized = StudentText.normalize(test);
        int length = normalized.codePointCount(0, normalized.length());
        return length >= 1 && length <= 100 && normalized.matches(VALIDATION_REGEX)
                && normalized.matches(".*[\\p{L}\\p{N}].*");
    }

    /** Reads a legacy name without imposing new student length limits or changing its display. */
    public static Name fromLegacy(String value) {
        requireNonNull(value);
        checkArgument(value.matches("[\\p{Alnum}][\\p{Alnum} ]*") || isValidName(value), MESSAGE_CONSTRAINTS);
        return new Name(value, true);
    }



    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
