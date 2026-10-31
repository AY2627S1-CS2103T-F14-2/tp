package seedu.address.model.person;

import java.util.Locale;
import java.util.Objects;

/** Validation and comparison rules for students created with the new add command. */
public final class StudentFields {
    public static final String INVALID_NAME = "Invalid name. Names must be 1–100 characters and contain at least one "
            + "letter or number. Only letters, numbers, spaces, apostrophes, hyphens and full stops are allowed.";
    public static final String INVALID_PHONE = "Invalid phone number. Use 3–15 digits, optionally beginning with '+'. "
            + "Spaces and hyphens may separate digit groups.";
    public static final String INVALID_EMAIL = "Invalid email. Use local-part@domain with a domain made of valid "
            + "letters, numbers, and hyphen-separated labels.";
    public static final String INVALID_SUBJECT = "Invalid subject. Subjects must be 1–50 characters and contain at "
            + "least one letter or number.";
    public static final String INVALID_LEVEL = "Invalid education level. Levels must be 1–30 characters and contain "
            + "only letters, numbers, spaces and hyphens.";
    public static final String INVALID_NOTES = "Invalid notes. Notes must be 1–500 characters and must not contain "
            + "control characters.";

    private StudentFields() {}

    /** Removes surrounding Unicode whitespace and collapses every internal run to one ordinary space. */
    public static String normalizeWhitespace(String value) {
        Objects.requireNonNull(value, "value");
        StringBuilder result = new StringBuilder();
        boolean pendingSpace = false;
        for (int offset = 0; offset < value.length();) {
            int codePoint = value.codePointAt(offset);
            offset += Character.charCount(codePoint);
            if (isWhitespace(codePoint)) {
                pendingSpace = result.length() > 0;
            } else {
                if (pendingSpace) {
                    result.append(' ');
                    pendingSpace = false;
                }
                result.appendCodePoint(codePoint);
            }
        }
        return result.toString();
    }

    public static String normalizeName(String value) {
        return normalizeWhitespace(value);
    }

    /** Removes surrounding Unicode whitespace without changing internal whitespace. */
    public static String trim(String value) {
        Objects.requireNonNull(value, "value");
        int start = 0;
        int end = value.length();
        while (start < end && isWhitespace(value.codePointAt(start))) {
            start += Character.charCount(value.codePointAt(start));
        }
        while (start < end && isWhitespace(value.codePointBefore(end))) {
            end -= Character.charCount(value.codePointBefore(end));
        }
        return value.substring(start, end);
    }

    /** Returns whether the normalized name follows the new-student rule. */
    public static boolean isValidName(String name) {
        Objects.requireNonNull(name, "name");
        String normalized = normalizeName(name);
        return hasLength(normalized, 1, 100) && hasLetterOrNumber(normalized)
                && normalized.codePoints().allMatch(codePoint -> isLetterOrNumber(codePoint)
                        || codePoint == ' ' || codePoint == '\'' || codePoint == '\u2019'
                        || codePoint == '-' || codePoint == '.');
    }

    /** Returns whether the phone follows the new-student rule. */
    public static boolean isValidPhone(String phone) {
        Objects.requireNonNull(phone, "phone");
        if (!phone.matches("\\+?[0-9]+(?:[ -][0-9]+)*")) {
            return false;
        }
        long digitCount = phone.chars().filter(Character::isDigit).count();
        return digitCount >= 3 && digitCount <= 15;
    }

    /** Removes supported spaces and hyphens from a valid phone number before storing it. */
    public static String normalizePhone(String phone) {
        Objects.requireNonNull(phone, "phone");
        return phone.replace(" ", "").replace("-", "");
    }

    /** Returns whether the email follows the new-student rule. */
    public static boolean isValidEmail(String email) {
        Objects.requireNonNull(email, "email");
        if (email.isEmpty() || email.codePoints().anyMatch(StudentFields::isWhitespace)) {
            return false;
        }
        int at = email.indexOf('@');
        if (at <= 0 || at != email.lastIndexOf('@') || at == email.length() - 1) {
            return false;
        }
        String domain = email.substring(at + 1);
        String label = "[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?";
        return domain.matches(label + "(?:\\." + label + ")+");
    }

    /** Returns whether the subject follows the new-student rule. */
    public static boolean isValidSubject(String subject) {
        Objects.requireNonNull(subject, "subject");
        String normalized = normalizeWhitespace(subject);
        return hasLength(normalized, 1, 50) && hasLetterOrNumber(normalized)
                && normalized.codePoints().allMatch(codePoint -> isLetterOrNumber(codePoint)
                        || codePoint == ' ' || codePoint == '&' || codePoint == '+'
                        || codePoint == '/' || codePoint == '\'' || codePoint == '-'
                        || codePoint == '.');
    }

    /** Returns whether the education level follows the new-student rule. */
    public static boolean isValidLevel(String level) {
        Objects.requireNonNull(level, "level");
        String normalized = normalizeWhitespace(level);
        return hasLength(normalized, 1, 30) && hasLetterOrNumber(normalized)
                && normalized.codePoints().allMatch(codePoint -> isLetterOrNumber(codePoint)
                        || codePoint == ' ' || codePoint == '-');
    }

    /** Returns whether notes are suitable for storage after trimming surrounding whitespace. */
    public static boolean isValidNotes(String notes) {
        Objects.requireNonNull(notes, "notes");
        String trimmed = trim(notes);
        return hasLength(trimmed, 1, 500)
                && trimmed.codePoints().noneMatch(Character::isISOControl);
    }

    /** Returns the case-insensitive comparison key for a name. */
    public static String nameKey(String name) {
        Objects.requireNonNull(name, "name");
        return normalizeName(name).toLowerCase(Locale.ROOT);
    }

    /** Returns the case-insensitive comparison key for a subject. */
    public static String subjectKey(String subject) {
        Objects.requireNonNull(subject, "subject");
        return normalizeWhitespace(subject).toLowerCase(Locale.ROOT);
    }

    /** Returns the case-insensitive comparison key for an email. */
    public static String emailKey(String email) {
        Objects.requireNonNull(email, "email");
        return email.toLowerCase(Locale.ROOT);
    }

    /** Returns the comparison key for a phone number without its optional plus sign or display separators. */
    public static String phoneKey(String phone) {
        Objects.requireNonNull(phone, "phone");
        String withoutLeadingPlus = phone.startsWith("+") ? phone.substring(1) : phone;
        return withoutLeadingPlus.replace(" ", "").replace("-", "");
    }

    private static boolean hasLength(String value, int minimum, int maximum) {
        int length = value.codePointCount(0, value.length());
        return minimum <= length && length <= maximum;
    }

    private static boolean hasLetterOrNumber(String value) {
        return value.codePoints().anyMatch(StudentFields::isLetterOrNumber);
    }

    private static boolean isLetterOrNumber(int codePoint) {
        int type = Character.getType(codePoint);
        return Character.isLetter(codePoint) || type == Character.DECIMAL_DIGIT_NUMBER
                || type == Character.LETTER_NUMBER || type == Character.OTHER_NUMBER;
    }

    private static boolean isWhitespace(int codePoint) {
        return Character.isWhitespace(codePoint) || Character.isSpaceChar(codePoint);
    }
}
