package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/** An immutable free-text education level. */
public class Level {
    public static final String MESSAGE_CONSTRAINTS = "Invalid education level. Levels must be 1–30 characters "
            + "and contain only letters, numbers, spaces and hyphens.";
    public final String value;

    /** Creates a normalized education level. */
    public Level(String value) {
        requireNonNull(value);
        String normalized = StudentText.normalize(value);
        checkArgument(isValidLevel(normalized), MESSAGE_CONSTRAINTS);
        this.value = normalized;
    }

    /** Checks the allowed characters and length after normalization. */
    public static boolean isValidLevel(String value) {
        String normalized = StudentText.normalize(value);
        int length = normalized.codePointCount(0, normalized.length());
        return length >= 1 && length <= 30 && normalized.matches("[\\p{L}\\p{N} -]+")
                && normalized.matches(".*[\\p{L}\\p{N}].*");
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Level level && value.equals(level.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
