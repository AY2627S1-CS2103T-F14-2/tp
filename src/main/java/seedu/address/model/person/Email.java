package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * An immutable contact email. Display capitalization is preserved.
 */
public class Email {
    public static final String MESSAGE_CONSTRAINTS =
            "Invalid email. Email must be in the form local-part@domain and must not contain spaces.";
    public static final String VALIDATION_REGEX = "(?U)[^@\\s]+@[^@\\s.]+(?:\\.[^@\\s.]+)+";

    private static final String ALPHANUMERIC = "[^\\W_]+";
    private static final String LOCAL_PART = "^" + ALPHANUMERIC + "([+_.-]" + ALPHANUMERIC + ")*";
    private static final String DOMAIN_PART = ALPHANUMERIC + "(-" + ALPHANUMERIC + ")*";
    private static final String LEGACY_VALIDATION_REGEX =
            LOCAL_PART + "@(" + DOMAIN_PART + "\\.)*(" + DOMAIN_PART + "){2,}$";

    public final String value;

    /** Creates a supplied email, rejecting empty values. */
    public Email(String email) {
        requireNonNull(email);
        checkArgument(isValidEmail(email), MESSAGE_CONSTRAINTS);
        value = email;
    }

    private Email(String value, boolean legacy) {
        this.value = value;
    }

    /** Checks exactly one @, no whitespace, and nonempty dot-separated domain labels. */
    public static boolean isValidEmail(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /** Represents an omitted optional email without accepting an empty e/ parameter. */
    public static Email absent() {
        return new Email("", true);
    }

    /** Retains emails accepted by the legacy contact format. */
    public static Email fromLegacy(String value) {
        requireNonNull(value);
        checkArgument(value.matches(LEGACY_VALIDATION_REGEX) || isValidEmail(value), MESSAGE_CONSTRAINTS);
        return new Email(value, true);
    }

    /** Returns whether this record has an email. */
    public boolean isPresent() {
        return !value.isEmpty();
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Email email && value.equals(email.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
