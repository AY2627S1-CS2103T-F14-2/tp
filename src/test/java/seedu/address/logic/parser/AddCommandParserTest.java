package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Address;
import seedu.address.model.person.Person;
import seedu.address.model.person.StudentFields;
import seedu.address.model.tag.Tag;

public class AddCommandParserTest {
    private static final String PARAMETER_GUIDE = "Required: add n/NAME p/PHONE s/SUBJECT l/LEVEL\n"
            + "Optional: e/EMAIL; a/ADDRESS; r/NOTES; t/TAG; repeat s/SUBJECT for additional subjects"
            + " or t/TAG for additional tags.\n"
            + "Parameters may appear in any order.";

    private final AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_repeatedTagPrefixesInAnyOrder_success() {
        Person expected = new Person("Amy", "123", null, List.of("Math", "Physics"), "JC 1", "Keep notes")
                .withTags(Set.of(new Tag("exam-prep"), new Tag("group-class")));
        assertParseSuccess(parser, " t/EXAM-Prep n/Amy p/123 s/Math t/group-class s/Physics l/JC 1 r/Keep notes",
                new AddCommand(expected));
    }

    @Test
    public void parse_duplicateTags_deduplicatesAndRequestsWarning() {
        Person expected = new Person("Amy", "123", null, List.of("Math"), "JC 1")
                .withTags(Set.of(new Tag("exam-prep")));
        assertParseSuccess(parser, " n/Amy p/123 s/Math l/JC 1 t/EXAM-PREP t/exam-prep",
                new AddCommand(expected, true));
    }

    @Test
    public void parse_tagLimit_countsDistinctNormalizedTags() {
        String tags = IntStream.range(0, 20).mapToObj(i -> " t/tag" + i).collect(Collectors.joining());
        Set<Tag> expectedTags = IntStream.range(0, 20).mapToObj(i -> new Tag("tag" + i)).collect(Collectors.toSet());
        Person expected = new Person("Amy", "123", null, List.of("Math"), "JC 1").withTags(expectedTags);
        String command = " n/Amy p/123 s/Math l/JC 1" + tags;
        assertParseSuccess(parser, command, new AddCommand(expected));
        assertParseSuccess(parser, command + " t/TAG0", new AddCommand(expected, true));
        assertParseFailure(parser, command + " t/tag20", Tag.MESSAGE_TAG_LIMIT);
    }

    @Test
    public void parse_emptyMultipleOrInvalidTagValues_rejected() {
        String command = " n/Amy p/123 s/Math l/JC 1";
        for (String value : List.of("", "tag1 tag2", "\"exam prep\"", "bad--tag", "bad_tag", "a".repeat(31))) {
            assertParseFailure(parser, command + " t/" + value, Tag.MESSAGE_CONSTRAINTS);
        }
        assertParseFailure(parser, command + " t/t/valid", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, command + " /x exam-prep", "Unknown parameter prefix: /x");
        Person expected = new Person("Amy", "123", null, List.of("Math"), "JC 1")
                .withTags(Set.of(new Tag("a".repeat(30))));
        assertParseSuccess(parser, command + " t/" + "a".repeat(30), new AddCommand(expected));
    }

    @Test
    public void parse_allFieldsInAnyOrder_success() {
        Person expected = new Person("Ryan Tan", "+6591234567", "Ryan@Example.com",
                List.of("Mathematics", "Physics"), "Secondary 4", "", new Address("1 Main Street"));
        assertParseSuccess(parser,
                " l/Secondary 4 s/Mathematics e/Ryan@Example.com a/1 Main Street p/+6591234567 n/Ryan Tan s/Physics",
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
    public void parse_notes_success() {
        Person expected = new Person("Ryan Tan", "+6591234567", null,
                List.of("Mathematics"), "Secondary 4", "Prefers worked examples; parent requests weekly updates");
        assertParseSuccess(parser,
                " n/Ryan Tan p/+6591234567 s/Mathematics l/Secondary 4"
                        + " r/Prefers worked examples; parent requests weekly updates",
                new AddCommand(expected));
    }

    @Test
    public void parse_unicodeWhitespaceInName_normalizes() {
        Person expected = new Person("李 2", "123", null, List.of("数学"), "Grade 8");
        assertParseSuccess(parser, " n/  李\t\u00a0 2   p/123 s/数学 l/Grade 8", new AddCommand(expected));
    }

    @Test
    public void parse_subjectsAndLevelNormalizeWhitespaceAndKeepOrder() {
        Person expected = new Person("A", "123", null, List.of("Math Plus", "Physics"), "JC 1");
        assertParseSuccess(parser, " n/A p/123 s/  Math  \t\u00a0 Plus  s/Physics l/ JC  \u00a0 1 ",
                new AddCommand(expected));
    }

    @Test
    public void parse_subjectAndLevelLengthCheckedAfterNormalization() {
        String subject = "English" + " ".repeat(60) + "Language";
        String level = "Secondary" + " ".repeat(40) + "4";
        Person expected = new Person("A", "123", null, List.of("English Language"), "Secondary 4");
        assertParseSuccess(parser, " n/A p/123 s/" + subject + " l/" + level, new AddCommand(expected));
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
    public void usage_separatesRequiredAndOptionalParameters() {
        assertEquals("Invalid command format.\n" + PARAMETER_GUIDE, AddCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_missingRequiredParameter_namesMissingField() {
        assertParseFailure(parser, "", "Missing required parameter: n/NAME.\n" + PARAMETER_GUIDE);
        assertParseFailure(parser, " p/123 s/Math l/JC 1",
                "Missing required parameter: n/NAME.\n" + PARAMETER_GUIDE);
        assertParseFailure(parser, " n/A s/Math l/JC 1",
                "Missing required parameter: p/PHONE.\n" + PARAMETER_GUIDE);
        assertParseFailure(parser, " n/A p/123 l/JC 1",
                "Missing required parameter: s/SUBJECT.\n" + PARAMETER_GUIDE);
        assertParseFailure(parser, " n/A p/123 s/Math",
                "Missing required parameter: l/LEVEL.\n" + PARAMETER_GUIDE);
    }

    @Test
    public void parse_emptyRequiredValue_namesField() {
        assertParseFailure(parser, " n/ p/123 s/Math l/JC 1",
                "Missing value for required parameter: n/NAME.\n" + PARAMETER_GUIDE);
        assertParseFailure(parser, " n/A p/ s/Math l/JC 1",
                "Missing value for required parameter: p/PHONE.\n" + PARAMETER_GUIDE);
        assertParseFailure(parser, " n/A p/123 s/ l/JC 1",
                "Missing value for required parameter: s/SUBJECT.\n" + PARAMETER_GUIDE);
        assertParseFailure(parser, " n/A p/123 s/Math l/",
                "Missing value for required parameter: l/LEVEL.\n" + PARAMETER_GUIDE);
    }

    @Test
    public void parse_unknownPrefix_failure() {
        assertParseFailure(parser, " n/A p/123 s/Math l/JC 1 x/Test",
                "Unknown parameter prefix: x/");
        assertParseFailure(parser, " n/A p/123 s/Math l/JC 1 /t Old",
                "Unknown parameter prefix: /t");
    }

    @Test
    public void parse_repeatedSingleUsePrefix_failure() {
        assertParseFailure(parser, " n/A n/B p/123 s/Math l/JC 1",
                "Parameter n/ must not be specified more than once.");
        assertParseFailure(parser, " n/A p/123 e/a@b.c e/b@b.c s/Math l/JC 1",
                "Parameter e/ must not be specified more than once.");
        assertParseFailure(parser, " n/A p/123 a/One a/Two s/Math l/JC 1",
                "Parameter a/ must not be specified more than once.");
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
        assertParseFailure(parser, " n/A p/123 s/--- l/JC 1", StudentFields.INVALID_SUBJECT);
        assertParseFailure(parser, " n/A p/123 s/Math_1 l/JC 1", StudentFields.INVALID_SUBJECT);
        assertParseFailure(parser, " n/A p/123 s/" + "A".repeat(51) + " l/JC 1",
                StudentFields.INVALID_SUBJECT);
        assertParseFailure(parser, " n/A p/123 s/Math s/ math  l/JC 1", "Duplicate subject: math");
        assertParseFailure(parser, " n/A p/123 s/English Language s/English  Language l/JC 1",
                "Duplicate subject: English Language");
    }

    @Test
    public void parse_invalidLevel_failure() {
        assertParseFailure(parser, " n/A p/123 s/Math l/-", StudentFields.INVALID_LEVEL);
        assertParseFailure(parser, " n/A p/123 s/Math l/JC_1", StudentFields.INVALID_LEVEL);
        assertParseFailure(parser, " n/A p/123 s/Math l/" + "A".repeat(31),
                StudentFields.INVALID_LEVEL);
    }

    @Test
    public void parse_invalidNotes_failure() {
        assertParseFailure(parser, " n/A p/123 s/Math l/JC 1 r/", StudentFields.INVALID_NOTES);
        assertParseFailure(parser, " n/A p/123 s/Math l/JC 1 r/" + "N".repeat(501),
                StudentFields.INVALID_NOTES);
    }

    @Test
    public void parse_invalidAddress_failure() {
        assertParseFailure(parser, " n/A p/123 a/ s/Math l/JC 1", Address.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/A p/123 a/   s/Math l/JC 1", Address.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_directWithoutLeadingSpace_success() throws Exception {
        AddCommand result = parser.parse("n/A p/123 s/Math l/JC 1");
        assertEquals(new AddCommand(new Person("A", "123", null, List.of("Math"), "JC 1")), result);
    }

    @Test
    public void parse_preamble_failure() {
        assertParseFailure(parser, "garbage n/A p/123 s/Math l/JC 1", AddCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "garbage", AddCommand.MESSAGE_USAGE);
    }
}
