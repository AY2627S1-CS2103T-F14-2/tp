package seedu.address.model.person;

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
}
