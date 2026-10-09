package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;
import seedu.address.model.person.StudentFields;

public class AddCommandParserTest {
    private final AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allFieldsInAnyOrder_success() {
        Person expected = new Person("Ryan Tan", "+6591234567", "Ryan@Example.com",
                List.of("Mathematics", "Physics"), "Secondary 4");
        assertParseSuccess(parser,
                " l/Secondary 4 s/Mathematics e/Ryan@Example.com p/+6591234567 n/Ryan Tan s/Physics",
                new AddCommand(expected));
    }

    @Test
    public void parse_optionalEmailMissing_success() {
        Person expected = new Person("Anne-Marie Lee", "81234567", null,
                List.of("C++", "Physics/Chemistry"), "JC 1");
        assertParseSuccess(parser,
                " n/Anne-Marie Lee p/81234567 s/C++ s/Physics/Chemistry l/JC 1",
                new AddCommand(expected));
    }

    @Test
    public void parse_unicodeWhitespaceInName_normalizes() {
        Person expected = new Person("李 2", "123", null, List.of("数学"), "Grade 8");
        assertParseSuccess(parser, " n/  李\t\u00a0 2   p/123 s/数学 l/Grade 8", new AddCommand(expected));
    }

    @Test
    public void parse_subjectsTrimOnlyAndKeepOrder() {
        Person expected = new Person("A", "123", null, List.of("Math  Plus", "Physics"), "JC 1");
        assertParseSuccess(parser, " n/A p/123 s/  Math  Plus  s/Physics l/ JC 1 ",
                new AddCommand(expected));
    }

    @Test
    public void parse_subjectWithSlashInsideValue_success() {
        Person expected = new Person("A", "123", null,
                List.of("Math Physics/Chemistry"), "JC 1");
        assertParseSuccess(parser, " n/A p/123 s/Math Physics/Chemistry l/JC 1",
                new AddCommand(expected));
    }

    @Test
    public void parse_validMaximumLengthsAndEmailShape_success() {
        String name = "A".repeat(100);
        String phone = "+" + "1".repeat(15);
        String subject = "S".repeat(50);
        String level = "L".repeat(30);
        Person expected = new Person(name, phone, "a..b@x.y", List.of(subject), level);
        assertParseSuccess(parser, " n/" + name + " p/" + phone + " e/a..b@x.y s/" + subject
                + " l/" + level, new AddCommand(expected));
    }

    @Test
    public void parse_allowedPunctuationAndHyphenatedLevel_success() {
        Person expected = new Person("O'Neil Jr.", "123", null,
                List.of("English & Lit.", "Tutor's Notes", "Math-A"), "Grade-8");
        assertParseSuccess(parser,
                " n/O'Neil Jr. p/123 s/English & Lit. s/Tutor's Notes s/Math-A l/Grade-8",
                new AddCommand(expected));
    }

    @Test
    public void parse_unicodeNumberCharacters_success() {
        Person expected = new Person("Ⅻ", "123", null, List.of("Math ²"), "Level Ⅻ");
        assertParseSuccess(parser, " n/Ⅻ p/123 s/Math ² l/Level Ⅻ", new AddCommand(expected));
    }

    @Test
    public void parse_missingRequiredOrNoParameters_usage() {
        assertParseFailure(parser, "", AddCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " n/A p/123 l/JC 1", AddCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " n/A p/123 s/Math", AddCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_unknownPrefix_failure() {
        assertParseFailure(parser, " n/A p/123 s/Math l/JC 1 x/Test",
                "Unknown parameter prefix: x/");
        assertParseFailure(parser, " n/A p/123 s/Math l/JC 1 a/Old",
                "Unknown parameter prefix: a/");
        assertParseFailure(parser, " n/A p/123 s/Math l/JC 1 t/Old",
                "Unknown parameter prefix: t/");
    }

    @Test
    public void parse_repeatedSingleUsePrefix_failure() {
        assertParseFailure(parser, " n/A n/B p/123 s/Math l/JC 1",
                "Parameter n/ must not be specified more than once.");
        assertParseFailure(parser, " n/A p/123 e/a@b.c e/b@b.c s/Math l/JC 1",
                "Parameter e/ must not be specified more than once.");
    }

    @Test
    public void parse_invalidName_failure() {
        assertParseFailure(parser, " n/--- p/123 s/Math l/JC 1", StudentFields.INVALID_NAME);
        assertParseFailure(parser, " n/A*B p/123 s/Math l/JC 1", StudentFields.INVALID_NAME);
        assertParseFailure(parser, " n/" + "A".repeat(101) + " p/123 s/Math l/JC 1",
                StudentFields.INVALID_NAME);
    }

    @Test
    public void parse_invalidPhone_failure() {
        assertParseFailure(parser, " n/A p/12 s/Math l/JC 1", StudentFields.INVALID_PHONE);
        assertParseFailure(parser, " n/A p/123-456 s/Math l/JC 1", StudentFields.INVALID_PHONE);
        assertParseFailure(parser, " n/A p/" + "1".repeat(16) + " s/Math l/JC 1",
                StudentFields.INVALID_PHONE);
    }

    @Test
    public void parse_invalidEmail_failure() {
        assertParseFailure(parser, " n/A p/123 e/ s/Math l/JC 1", StudentFields.INVALID_EMAIL);
        assertParseFailure(parser, " n/A p/123 e/a@b s/Math l/JC 1", StudentFields.INVALID_EMAIL);
        assertParseFailure(parser, " n/A p/123 e/a@b..c s/Math l/JC 1", StudentFields.INVALID_EMAIL);
        assertParseFailure(parser, " n/A p/123 e/a @b.c s/Math l/JC 1", StudentFields.INVALID_EMAIL);
        assertParseFailure(parser, " n/A p/123 e/abc s/Math l/JC 1", StudentFields.INVALID_EMAIL);
        assertParseFailure(parser, " n/A p/123 e/@b.c s/Math l/JC 1", StudentFields.INVALID_EMAIL);
        assertParseFailure(parser, " n/A p/123 e/a@ s/Math l/JC 1", StudentFields.INVALID_EMAIL);
        assertParseFailure(parser, " n/A p/123 e/a@@b.c s/Math l/JC 1", StudentFields.INVALID_EMAIL);
    }

    @Test
    public void parse_invalidOrRepeatedSubject_failure() {
        assertParseFailure(parser, " n/A p/123 s/ l/JC 1", StudentFields.INVALID_SUBJECT);
        assertParseFailure(parser, " n/A p/123 s/--- l/JC 1", StudentFields.INVALID_SUBJECT);
        assertParseFailure(parser, " n/A p/123 s/Math_1 l/JC 1", StudentFields.INVALID_SUBJECT);
        assertParseFailure(parser, " n/A p/123 s/" + "A".repeat(51) + " l/JC 1",
                StudentFields.INVALID_SUBJECT);
        assertParseFailure(parser, " n/A p/123 s/Math s/ math  l/JC 1", "Duplicate subject: math");
    }

    @Test
    public void parse_invalidLevel_failure() {
        assertParseFailure(parser, " n/A p/123 s/Math l/", StudentFields.INVALID_LEVEL);
        assertParseFailure(parser, " n/A p/123 s/Math l/-", StudentFields.INVALID_LEVEL);
        assertParseFailure(parser, " n/A p/123 s/Math l/JC_1", StudentFields.INVALID_LEVEL);
        assertParseFailure(parser, " n/A p/123 s/Math l/" + "A".repeat(31),
                StudentFields.INVALID_LEVEL);
    }

    @Test
    public void parse_directWithoutLeadingSpace_success() throws Exception {
        AddCommand result = parser.parse("n/A p/123 s/Math l/JC 1");
        assertEquals(new AddCommand(new Person("A", "123", null, List.of("Math"), "JC 1")), result);
    }

    @Test
    public void parse_preamble_failure() {
        assertThrows(ParseException.class, () -> parser.parse("garbage n/A p/123 s/Math l/JC 1"));
    }
}
