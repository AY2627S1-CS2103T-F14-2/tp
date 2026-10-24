package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.person.StudentFields;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        requireNonNull(oneBasedIndex);
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        if (tagSet.size() > Tag.MAX_TAGS_PER_STUDENT) {
            throw new ParseException(Tag.MESSAGE_TAG_LIMIT);
        }
        return tagSet;
    }

    /** Parses a name using the new student validation rules. */
    public static Name parseStudentName(String name) throws ParseException {
        requireNonNull(name);
        String normalizedName = StudentFields.normalizeName(name);
        if (!StudentFields.isValidName(normalizedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return Name.fromStoredValue(normalizedName);
    }

    /** Parses a phone number using the new student validation rules. */
    public static Phone parseStudentPhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = StudentFields.trim(phone);
        if (!StudentFields.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return Phone.fromStoredValue(trimmedPhone);
    }

    /** Parses an email using the new student validation rules. */
    public static Email parseStudentEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = StudentFields.trim(email);
        if (!StudentFields.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return Email.fromStoredValue(trimmedEmail);
    }

    /** Parses and normalises one subject value. */
    public static String parseSubject(String subject) throws ParseException {
        requireNonNull(subject);
        String normalisedSubject = StudentFields.normalizeWhitespace(subject);
        if (!StudentFields.isValidSubject(normalisedSubject)) {
            throw new ParseException(StudentFields.INVALID_SUBJECT);
        }
        return normalisedSubject;
    }

    /** Parses and normalises one education-level value. */
    public static String parseLevel(String level) throws ParseException {
        requireNonNull(level);
        String normalisedLevel = StudentFields.normalizeWhitespace(level);
        if (!StudentFields.isValidLevel(normalisedLevel)) {
            throw new ParseException(StudentFields.INVALID_LEVEL);
        }
        return normalisedLevel;
    }

    /** Parses one non-empty notes value and trims only its surrounding whitespace. */
    public static String parseNotes(String notes) throws ParseException {
        requireNonNull(notes);
        String trimmedNotes = StudentFields.trim(notes);
        if (!StudentFields.isValidNotes(trimmedNotes)) {
            throw new ParseException(StudentFields.INVALID_NOTES);
        }
        return trimmedNotes;
    }
}
