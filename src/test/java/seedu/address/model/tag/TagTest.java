package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        String invalidTagName = "";
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void isValidTagName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));
    }

    @Test
    public void isValidTagName_grammarAndLengthBoundaries() {
        for (String valid : List.of("a", "1", "EXAM-Prep", "group-class", "a-1-b", "a".repeat(30))) {
            assertTrue(Tag.isValidTagName(valid), valid);
        }
        for (String invalid : List.of("", " ", "exam prep", "exam_prep", "-exam", "exam-",
                "exam--prep", "标签", "é", "a/b", "a".repeat(31))) {
            assertFalse(Tag.isValidTagName(invalid), invalid);
        }
    }

    @Test
    public void constructor_normalizesCaseAndEquality() {
        Tag upper = new Tag("EXAM-Prep");
        Tag lower = new Tag("exam-prep");
        assertEquals("exam-prep", upper.tagName);
        assertEquals(upper, lower);
        assertEquals(upper.hashCode(), lower.hashCode());
        assertEquals(upper, upper);
        assertFalse(upper.equals(null));
        assertFalse(upper.equals("exam-prep"));
        assertFalse(upper.equals(new Tag("group-class")));
    }

    @Test
    public void fromStoredName_preservesLongNamesButStillChecksGrammar() {
        String longName = "A".repeat(31);
        assertFalse(Tag.isValidTagName(longName));
        assertTrue(Tag.isValidStoredTagName(longName));
        assertEquals("a".repeat(31), Tag.fromStoredName(longName).tagName);
        assertThrows(IllegalArgumentException.class, () -> new Tag(longName));
        assertThrows(IllegalArgumentException.class, () -> Tag.fromStoredName("bad--tag"));
        assertThrows(NullPointerException.class, () -> Tag.fromStoredName(null));
    }
}
