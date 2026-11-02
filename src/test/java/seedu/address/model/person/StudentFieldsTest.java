package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests validation rules shared by student commands and model construction.
 */
public class StudentFieldsTest {

    @Test
    public void isValidName_straightAndCurlyApostrophes_accepts() {
        for (String name : List.of("O'rayn", "O’rayn", "D'Arcy O’Connor")) {
            assertTrue(StudentFields.isValidName(name), name);
        }
    }

    @Test
    public void isValidName_apostrophes_doNotBypassOtherConstraints() {
        for (String name : List.of("'", "’", "' ’", "O’rayn*", "O'rayn_", "A".repeat(100) + "’")) {
            assertFalse(StudentFields.isValidName(name), name);
        }
    }

    @Test
    public void isValidPhone_acceptsCommonSeparatorsAndNormalizes() {
        for (String phone : List.of("9123 4567", "9123-4567", "+65 9123-4567")) {
            assertTrue(StudentFields.isValidPhone(phone), phone);
        }
        assertEquals("91234567", StudentFields.normalizePhone("9123 4567"));
        assertEquals("+6591234567", StudentFields.normalizePhone("+65 9123-4567"));
        assertEquals("6591234567", StudentFields.phoneKey("+65 9123-4567"));
    }

    @Test
    public void isValidPhone_rejectsUnsupportedCharactersAndInvalidLengths() {
        for (String phone : List.of("91/234567", "9123--4567", "+-123", "12", "1234567890123456")) {
            assertFalse(StudentFields.isValidPhone(phone), phone);
        }
    }

    @Test
    public void isValidEmail_validatesDnsDomainLabelsWithoutRestrictingLocalPart() {
        for (String email : List.of("a@example.com", "a@sub.example.com", "a@my-domain.co.uk",
                "te!st+tag@example.com")) {
            assertTrue(StudentFields.isValidEmail(email), email);
        }
        for (String email : List.of("a@exam!ple.com", "a@-example.com", "a@example-.com", "a@example..com",
                "a@" + "x".repeat(64) + ".com")) {
            assertFalse(StudentFields.isValidEmail(email), email);
        }
    }
}
