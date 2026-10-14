package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;

public class PersonCardTest {

    @Test
    public void content_newStudentWithEmail_showsAllFields() {
        Person student = new Person("Ryan Tan", "+6591234567", "Ryan@Example.com",
                List.of("Mathematics", "Physics"), "Secondary 4");

        PersonCard.CardContent content = PersonCard.CardContent.from(student);

        assertEquals("Ryan Tan", content.name());
        assertEquals("RT", content.initials());
        assertEquals("Phone: +6591234567", content.phone());
        assertEquals("Email: Ryan@Example.com", content.email());
        assertEquals("Subjects: Mathematics, Physics", content.subjects());
        assertEquals("Level: Secondary 4", content.level());
    }

    @Test
    public void content_newStudentWithoutEmail_omitsEmailAndPreservesUnicodeInitials() {
        Person student = new Person("李 明", "123", null, List.of("数学"), "Grade-8");

        PersonCard.CardContent content = PersonCard.CardContent.from(student);

        assertEquals("李 明", content.name());
        assertEquals("李明", content.initials());
        assertEquals("Phone: 123", content.phone());
        assertNull(content.email());
        assertEquals("Subjects: 数学", content.subjects());
        assertEquals("Level: Grade-8", content.level());
    }
}
