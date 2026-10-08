package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/** An immutable, validated subject, preserving display capitalization. */
public class Subject {
    public static final String MESSAGE_CONSTRAINTS = "Invalid subject. Subjects must be 1–50 characters "
            + "and contain at least one letter or number.";
    public final String value;

    /** Creates a normalized subject. */
    public Subject(String value) {
        requireNonNull(value);
        String normalized = StudentText.normalize(value);
        checkArgument(isValidSubject(normalized), MESSAGE_CONSTRAINTS);
        this.value = normalized;
    }

    /** Checks the allowed characters and length after normalization. */
    public static boolean isValidSubject(String value) {
        String normalized = StudentText.normalize(value);
        int length = normalized.codePointCount(0, normalized.length());
        return length >= 1 && length <= 50
                && normalized.matches("[\\p{L}\\p{N} &+/'.-]+")
                && normalized.matches(".*[\\p{L}\\p{N}].*");
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Subject subject && value.equals(subject.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
