package seedu.address.model.person;

import java.util.Locale;

/** Validation and comparison rules for students created with the new add command. */
public final class StudentFields {
    public static final String INVALID_NAME = "Invalid name. Names must be 1–100 characters and contain at least one "
            + "letter or number. Only letters, numbers, spaces, apostrophes, hyphens and full stops are allowed.";
    public static final String INVALID_PHONE = "Invalid phone number. Phone numbers must contain 3–15 digits and may "
            + "optionally begin with '+'.";
    public static final String INVALID_EMAIL = "Invalid email. Email must be in the form local-part@domain "
            + "and must not contain spaces.";
    public static final String INVALID_SUBJECT = "Invalid subject. Subjects must be 1–50 characters and contain at "
            + "least one letter or number.";
    public static final String INVALID_LEVEL = "Invalid education level. Levels must be 1–30 characters and contain "
            + "only letters, numbers, spaces and hyphens.";

    private StudentFields() {}

    /** Collapses every run of Unicode whitespace to one ordinary space. */
    public static String normalizeName(String value) {
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

    /** Removes surrounding Unicode whitespace without changing internal whitespace. */
    public static String trim(String value) {
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
        String normalized = normalizeName(name);
        return hasLength(normalized, 1, 100) && hasLetterOrNumber(normalized)
                && normalized.codePoints().allMatch(codePoint -> isLetterOrNumber(codePoint)
                        || codePoint == ' ' || codePoint == '\'' || codePoint == '-'
                        || codePoint == '.');
    }

    /** Returns whether the phone follows the new-student rule. */
    public static boolean isValidPhone(String phone) {
        return phone.matches("\\+?[0-9]{3,15}");
    }

    /** Returns whether the email follows the new-student rule. */
    public static boolean isValidEmail(String email) {
        if (email.isEmpty() || email.codePoints().anyMatch(StudentFields::isWhitespace)) {
            return false;
        }
        int at = email.indexOf('@');
        if (at <= 0 || at != email.lastIndexOf('@') || at == email.length() - 1) {
            return false;
        }
        String domain = email.substring(at + 1);
        String[] labels = domain.split("\\.", -1);
        if (labels.length < 2) {
            return false;
        }
        for (String label : labels) {
            if (label.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /** Returns whether the subject follows the new-student rule. */
    public static boolean isValidSubject(String subject) {
        String trimmed = trim(subject);
        return hasLength(trimmed, 1, 50) && hasLetterOrNumber(trimmed)
                && trimmed.codePoints().allMatch(codePoint -> isLetterOrNumber(codePoint)
                        || codePoint == ' ' || codePoint == '&' || codePoint == '+'
                        || codePoint == '/' || codePoint == '\'' || codePoint == '-'
                        || codePoint == '.');
    }

    /** Returns whether the education level follows the new-student rule. */
    public static boolean isValidLevel(String level) {
        String trimmed = trim(level);
        return hasLength(trimmed, 1, 30) && hasLetterOrNumber(trimmed)
                && trimmed.codePoints().allMatch(codePoint -> isLetterOrNumber(codePoint)
                        || codePoint == ' ' || codePoint == '-');
    }

    public static String nameKey(String name) {
        return normalizeName(name).toLowerCase(Locale.ROOT);
    }

    public static String subjectKey(String subject) {
        return trim(subject).toLowerCase(Locale.ROOT);
    }

    public static String emailKey(String email) {
        return email.toLowerCase(Locale.ROOT);
    }

    public static String phoneKey(String phone) {
        return phone.startsWith("+") ? phone.substring(1) : phone;
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
