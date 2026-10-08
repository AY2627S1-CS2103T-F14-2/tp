package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.person.Email;
import seedu.address.model.person.Level;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Subject;
import seedu.address.testutil.TypicalPersons;

public class StudentStorageTest {
    @TempDir
    public Path directory;

    @Test
    public void saveAndRead_mixedRoster_preservesTypesOrderAndOptionalEmail() throws Exception {
        AddressBook roster = TypicalPersons.getTypicalAddressBook();
        Person student = new Person(new Name("陈 Anne-Marie"), new Phone("+123"), Email.absent(),
                List.of(new Subject("Physics/Chemistry"), new Subject("C++")), new Level("Year 10"));
        roster.addPerson(student);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(directory.resolve("roster.json"));
        storage.saveAddressBook(roster);
        AddressBook loaded = new AddressBook(storage.readAddressBook().orElseThrow());
        assertEquals(roster, loaded);
        assertFalse(loaded.getPersonList().getFirst().isStudent());
        assertTrue(loaded.getPersonList().getLast().isStudent());
        assertFalse(loaded.getPersonList().getLast().getEmail().isPresent());
        assertEquals(student.getSubjects(), loaded.getPersonList().getLast().getSubjects());
    }

    @Test
    public void read_legacyValuesOutsideNewLimits_preservesData() throws Exception {
        String longName = "A".repeat(101);
        String longPhone = "1".repeat(16);
        Person legacy = new JsonAdaptedPerson(longName, longPhone, "a@localhost", "Old address", List.of())
                .toModelType();
        assertEquals(longName, legacy.getName().fullName);
        assertEquals(longPhone, legacy.getPhone().value);
        assertFalse(legacy.isStudent());
    }

    @Test
    public void read_invalidStudentFields_rejectsRatherThanTreatingAsLegacy() {
        for (JsonAdaptedPerson record : List.of(
                student(List.of(), "Year 10", null),
                student(List.of("Math"), null, null),
                student(null, "Year 10", null),
                student(List.of("Math", "math"), "Year 10", null),
                student(List.of("Math"), "Year 10", ""),
                student(List.of("@"), "Year 10", null))) {
            assertThrows(IllegalValueException.class, record::toModelType);
        }
    }

    @Test
    public void read_presentEmail_preservesCapitalization() throws Exception {
        Person record = student(List.of("Math"), "Year 10", "Student@Example.COM").toModelType();
        assertEquals("Student@Example.COM", record.getEmail().value);
    }

    private JsonAdaptedPerson student(List<String> subjects, String level, String email) {
        return new JsonAdaptedPerson("Ryan", "+123", email, null, List.of(), subjects, level);
    }
}
