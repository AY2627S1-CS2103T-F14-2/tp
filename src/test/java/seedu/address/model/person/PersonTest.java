package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name, same phone, other attributes different -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB)
                .withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // same name, same email, other attributes different -> returns true
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withAddress(VALID_ADDRESS_BOB).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // same name, different phone and email -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // name differs in case, same phone -> returns true
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertTrue(BOB.isSamePerson(editedBob));

        // name has surrounding and extra whitespace, same phone -> returns true
        String nameWithTrailingSpaces = "  " + VALID_NAME_BOB + "  ";
        editedBob = new PersonBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertTrue(BOB.isSamePerson(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", subjects=" + ALICE.getSubjects() + ", level=" + ALICE.getLevel()
                + ", address=" + ALICE.getAddress() + ", tags=" + ALICE.getTags() + "}";
        assertEquals(expected, ALICE.toString());
    }

    @Test
    public void newStudentConstructor_invalidFields_rejected() {
        assertInvalidStudent(StudentFields.INVALID_NAME, "---", "123", null, List.of("Math"), "JC 1");
        assertInvalidStudent(StudentFields.INVALID_PHONE, "Amy", "12", null, List.of("Math"), "JC 1");
        assertInvalidStudent(StudentFields.INVALID_EMAIL, "Amy", "123", "amy@", List.of("Math"), "JC 1");
        assertInvalidStudent(StudentFields.INVALID_SUBJECT, "Amy", "123", null, List.of(), "JC 1");
        assertInvalidStudent(StudentFields.INVALID_SUBJECT, "Amy", "123", null,
                Collections.singletonList(null), "JC 1");
        assertInvalidStudent(StudentFields.INVALID_SUBJECT, "Amy", "123", null, List.of("Math_1"), "JC 1");
        assertInvalidStudent("Duplicate subject: math", "Amy", "123", null,
                List.of("Math", " math "), "JC 1");
        assertInvalidStudent("Duplicate subject: English Language", "Amy", "123", null,
                List.of("English Language", "English  Language"), "JC 1");
        assertInvalidStudent(StudentFields.INVALID_LEVEL, "Amy", "123", null, List.of("Math"), "JC_1");
    }

    @Test
    public void newStudentConstructor_unicodeWhitespace_normalized() {
        Person student = new Person("\u00a0 Amy\t  Bee \u00a0", "123", null,
                List.of(" Math  \t\u00a0 Plus \u00a0"), " Secondary  \u00a0 4 \u00a0");

        assertEquals("Amy Bee", student.getName().fullName);
        assertEquals(List.of("Math Plus"), student.getSubjects());
        assertEquals("Secondary 4", student.getLevel());
    }

    @Test
    public void newStudentConstructor_nullNotes_defaultsToEmpty() {
        Person student = new Person("Amy", "123", null, List.of("Math"), "JC 1", null);

        assertEquals("", student.getNotes());
    }

    @Test
    public void fromStoredRecord_nullNotes_defaultsToEmpty() {
        Person student = Person.fromStoredRecord("Amy", "123", null, List.of("Math"), "JC 1",
                (String) null, null, Collections.emptySet());

        assertEquals("", student.getNotes());
    }

    private static void assertInvalidStudent(String expectedMessage, String name, String phone, String email,
            List<String> subjects, String level) {
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new Person(name, phone, email, subjects, level));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
