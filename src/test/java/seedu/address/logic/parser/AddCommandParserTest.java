package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Email;
import seedu.address.model.person.Level;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Subject;

public class AddCommandParserTest {
    private final AddCommandParser parser = new AddCommandParser();
    private final String valid = " n/Ryan Tan p/+6591234567 s/Mathematics l/Secondary 4";

    @Test
    public void parse_orderAndNormalization_success() {
        Person expected = new Person(new Name("陈 Anne-Marie O'Connor."), new Phone("+821012345678"),
                new Email("Student+Tuition@Example.COM"),
                List.of(new Subject("Physics/Chemistry"), new Subject("English Language"), new Subject("C++")),
                new Level("Year 10"));
        assertParseSuccess(parser, "\tl/Year\t10\u2003s/Physics/Chemistry\nn/ 陈  Anne-Marie O'Connor. "
                + "e/Student+Tuition@Example.COM p/+821012345678 s/English\u00a0 Language s/C++",
                new AddCommand(expected));
    }

    @Test
    public void parse_emailOmitted_success() {
        Person expected = new Person(new Name("Ryan Tan"), new Phone("+6591234567"), Email.absent(),
                List.of(new Subject("Mathematics")), new Level("Secondary 4"));
        assertParseSuccess(parser, valid, new AddCommand(expected));
    }

    @Test
    public void parse_unknownAndRepeatedPrefixes_failure() {
        assertParseFailure(parser, valid + " x/Test", "Unknown parameter prefix: x/");
        assertParseFailure(parser, " x/Test n/Ryan n/Ryan", "Unknown parameter prefix: x/");
        for (String prefix : List.of("n/", "p/", "e/", "l/")) {
            String input = valid + " e/ryan@example.com " + prefix + "invalid";
            assertParseFailure(parser, input, "Parameter " + prefix + " must not be specified more than once.");
        }
        assertParseFailure(parser, " n/Ryan n/Ryan", "Parameter n/ must not be specified more than once.");
        assertParseFailure(parser, valid + " a/Old address", "Unknown parameter prefix: a/");
        assertParseFailure(parser, valid + " t/Old tag", "Unknown parameter prefix: t/");
    }

    @Test
    public void parse_requiredAndEmptyFields_failure() {
        for (String input : List.of("", " n/Ryan", " p/81234567 s/Math l/JC 1",
                " n/Ryan s/Math l/JC 1", " n/Ryan p/81234567 l/JC 1", " n/Ryan p/81234567 s/Math",
                " unexpected" + valid)) {
            assertParseFailure(parser, input, AddCommand.MESSAGE_INVALID_FORMAT);
        }
        assertParseFailure(parser, valid + " e/", Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/ p/81234567 s/Math l/JC 1", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/Ryan p/81234567 s/ l/JC 1", Subject.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/Ryan p/81234567 s/Math l/", Level.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidFieldsBeforeDuplicateSubjects_failure() {
        assertParseFailure(parser, " n/@ p/12 s/Math s/math l/JC 1", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/Ryan p/12 s/Math s/math l/JC 1", Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, valid + " e/ryan@example", Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, valid + " s/数学 s/数学 s/@", Subject.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/Ryan p/81234567 s/Math s/math l/@", Level.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, valid + " s/mathematics", "Duplicate subject: Mathematics");
        assertParseFailure(parser, " n/Ryan p/81234567 s/English  Language s/english\tlanguage l/JC 1",
                "Duplicate subject: English Language");
    }

    @Test
    public void validation_boundariesAndUnicode() {
        assertTrue(Name.isValidName("陈李"));
        assertTrue(Name.isValidName("A".repeat(100)));
        assertFalse(Name.isValidName("A".repeat(101)));
        assertFalse(Name.isValidName("'.-"));
        assertEquals("Ryan Tan", new Name(" Ryan\u00a0\tTan ").fullName);
        assertTrue(Phone.isValidPhone("+123"));
        assertTrue(Phone.isValidPhone("1".repeat(15)));
        for (String phone : List.of("12", "1".repeat(16), "++123", "123 456", "123-456", "１２３")) {
            assertFalse(Phone.isValidPhone(phone));
        }
        assertTrue(Subject.isValidSubject("A".repeat(50)));
        assertFalse(Subject.isValidSubject("A".repeat(51)));
        assertFalse(Subject.isValidSubject("&+-/' ."));
        assertTrue(Level.isValidLevel("A".repeat(30)));
        assertFalse(Level.isValidLevel("A".repeat(31)));
        assertFalse(Level.isValidLevel("--"));
        assertFalse(Level.isValidLevel("JC+1"));
    }
}
