package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class EmailTest {
    @Test
    public void constructor_invalidOrNull_throwsException() {
        assertThrows(NullPointerException.class, () -> new Email(null));
        assertThrows(IllegalArgumentException.class, () -> new Email(""));
    }

    @Test
    public void isValidEmail_matchesWrittenRule() {
        for (String value : List.of("ryan@example.com", "parent.tan@example.sg", "student+tuition@example.com",
                "a@b.c", ".local.@exam_ple.c", "陈@学校.中国")) {
            assertTrue(Email.isValidEmail(value), value);
        }
        for (String value : List.of("", "ryan", "ryan@", "@example.com", "a@localhost", "a@@b.c",
                "a@.b.c", "a@b..c", "a@b.c.", "ryan @example.com", "a@b.\u00a0c", "a\t@b.c")) {
            assertFalse(Email.isValidEmail(value), value);
        }
    }

    @Test
    public void equals_andAbsentEmail() {
        Email email = new Email("valid@example.com");
        assertTrue(email.equals(new Email("valid@example.com")));
        assertTrue(email.equals(email));
        assertFalse(email.equals(null));
        assertFalse(email.equals(5.0f));
        assertFalse(email.equals(new Email("other@example.com")));
        assertFalse(Email.absent().isPresent());
        assertTrue(email.isPresent());
        assertTrue(Email.fromLegacy("a@localhost").isPresent());
    }
}
