package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

public class TagCommandParserTest {
    @Test
    public void parse_validForms_executeRequestedOperation() throws Exception {
        var model = new ModelManager();
        model.addPerson(new Person("Amy", "123", null, List.of("Math"), "JC 1"));
        var parser = new AddressBookParser();
        parser.parseCommand("tag\t1\u2003EXAM-Prep").execute(model);
        assertEquals(Set.of(new Tag("exam-prep")), model.getFilteredPersonList().getFirst().getTags());
        parser.parseCommand("untag 1 EXAM-PREP").execute(model);
        assertTrue(model.getFilteredPersonList().getFirst().getTags().isEmpty());
        parser.parseCommand("tag 1 group-class").execute(model);
        parser.parseCommand("untag --all 1").execute(model);
        assertTrue(model.getFilteredPersonList().getFirst().getTags().isEmpty());
    }

    @Test
    public void parse_wrongArityOrIndex_reportsUsage() {
        for (boolean removing : List.of(false, true)) {
            var parser = new TagCommandParser(removing);
            String usage = removing ? TagCommand.MESSAGE_UNTAG_USAGE : TagCommand.MESSAGE_USAGE;
            for (String input : List.of("", "1", "1 exam-prep extra", "0 exam-prep", "-1 exam-prep",
                    "abc exam-prep", "2147483648 exam-prep", "--all", "--all 0", "--all 1 extra")) {
                assertEquals(usage, assertThrows(ParseException.class, () -> parser.parse(input), input).getMessage());
            }
        }
        assertEquals(TagCommand.MESSAGE_USAGE,
                assertThrows(ParseException.class, () -> new TagCommandParser(false).parse("--all 1")).getMessage());
    }

    @Test
    public void parse_invalidTag_reportsConstraints() {
        for (boolean removing : List.of(false, true)) {
            var parser = new TagCommandParser(removing);
            for (String name : List.of("--all", "-tag", "tag-", "tag--name", "tag_name", "标签", "a/b")) {
                assertEquals(Tag.MESSAGE_CONSTRAINTS,
                        assertThrows(ParseException.class, () -> parser.parse("1 " + name)).getMessage());
            }
        }
    }

    @Test
    public void parse_longStoredTag_canRemoveButCannotAdd() throws Exception {
        String name = "A".repeat(31);
        assertEquals(Tag.MESSAGE_CONSTRAINTS,
                assertThrows(ParseException.class, () -> new TagCommandParser(false).parse("1 " + name)).getMessage());
        var model = new ModelManager();
        model.addPerson(new Person("Amy", "123", null, List.of("Math"), "JC 1")
                .withTags(Set.of(Tag.fromStoredName(name))));
        new TagCommandParser(true).parse("1 " + name).execute(model);
        assertTrue(model.getFilteredPersonList().getFirst().getTags().isEmpty());
    }

    @Test
    public void parse_null_rejected() {
        assertThrows(NullPointerException.class, () -> new TagCommandParser(false).parse(null));
    }
}
