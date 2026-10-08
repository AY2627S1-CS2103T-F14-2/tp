package seedu.address.model.person;

/** Common text normalization for student fields. */
public final class StudentText {
    private StudentText() {
    }

    /** Trims and collapses every Unicode whitespace sequence to one space. */
    public static String normalize(String text) {
        return text.replaceAll("(?U)\\s+", " ").strip();
    }

    /** Removes Unicode whitespace at the boundaries without changing the value internally. */
    public static String trim(String text) {
        return text.replaceAll("(?U)^\\s+|\\s+$", "");
    }
}
