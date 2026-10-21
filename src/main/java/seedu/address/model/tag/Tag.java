package seedu.address.model.tag;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a Tag in the address book.
 * Names are immutable and lowercase. Stored names may exceed the length limit for new tags.
 */
public class Tag {

    public static final int MAX_NAME_LENGTH = 30;
    public static final int MAX_TAGS_PER_STUDENT = 20;
    public static final String MESSAGE_CONSTRAINTS = "Tag names must be 1–30 characters: English letters or digits, "
            + "with optional single hyphens between non-empty segments.";
    public static final String MESSAGE_TAG_LIMIT = "A student may have at most 20 tags.";
    public static final String VALIDATION_REGEX = "[a-zA-Z0-9]+(-[a-zA-Z0-9]+)*";

    public final String tagName;

    /**
     * Constructs a {@code Tag}.
     *
     * @param tagName A valid tag name.
     */
    public Tag(String tagName) {
        this(tagName, false);
    }

    private Tag(String tagName, boolean stored) {
        requireNonNull(tagName);
        checkArgument(stored ? isValidStoredTagName(tagName) : isValidTagName(tagName), MESSAGE_CONSTRAINTS);
        this.tagName = tagName.toLowerCase(Locale.ROOT);
    }

    /** Loads an existing tag, retaining names that predate the length limit. */
    public static Tag fromStoredName(String tagName) {
        return new Tag(tagName, true);
    }

    /** Checks the tag grammar without imposing new-add length limits on existing data. */
    public static boolean isValidStoredTagName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if a given string is a valid tag name.
     */
    public static boolean isValidTagName(String test) {
        return test.length() <= MAX_NAME_LENGTH && isValidStoredTagName(test);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Tag otherTag)) {
            return false;
        }

        return tagName.equals(otherTag.tagName);
    }

    @Override
    public int hashCode() {
        return tagName.hashCode();
    }

    /**
     * Formats state as text for viewing.
     */
    public String toString() {
        return '[' + tagName + ']';
    }

}
