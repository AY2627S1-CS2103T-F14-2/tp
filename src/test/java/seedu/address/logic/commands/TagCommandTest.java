package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.TagCommand.Operation;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

public class TagCommandTest {
    @Test
    public void execute_invalidDisplayedIndex_leavesRosterUnchanged() {
        var model = modelWithTags(Set.of());
        var before = model.getFilteredPersonList().getFirst();
        assertEquals(DeleteCommand.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX,
                assertThrows(CommandException.class, () -> new TagCommand(Index.fromOneBased(2),
                        new Tag("exam-prep"), Operation.ADD).execute(model)).getMessage());
        model.updateFilteredPersonList(person -> false);
        assertThrows(CommandException.class, () -> command(new Tag("exam-prep"), Operation.ADD).execute(model));
        assertEquals(List.of(before), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_atLimit_warnsForDuplicateButRejectsNewTag() throws Exception {
        Set<Tag> tags = IntStream.range(0, 19).mapToObj(i -> new Tag("tag" + i)).collect(Collectors.toSet());
        var model = modelWithTags(tags);
        command(new Tag("last-tag"), Operation.ADD).execute(model);
        assertEquals(20, model.getFilteredPersonList().getFirst().getTags().size());
        assertTrue(command(new Tag("LAST-TAG"), Operation.ADD).execute(model)
                .getFeedbackToUser().startsWith("Warning:"));
        CommandException error = assertThrows(CommandException.class, () ->
                command(new Tag("too-many"), Operation.ADD).execute(model));
        assertEquals(Tag.MESSAGE_TAG_LIMIT, error.getMessage());
        assertEquals(20, model.getFilteredPersonList().getFirst().getTags().size());
    }

    @Test
    public void execute_overLegacyLimit_canRemoveAndClear() throws Exception {
        Set<Tag> tags = IntStream.range(0, 21).mapToObj(i -> new Tag("tag" + i)).collect(Collectors.toSet());
        var model = modelWithTags(tags);
        command(new Tag("tag0"), Operation.REMOVE).execute(model);
        assertEquals(20, model.getFilteredPersonList().getFirst().getTags().size());
        command(null, Operation.CLEAR).execute(model);
        assertTrue(model.getFilteredPersonList().getFirst().getTags().isEmpty());
    }

    @Test
    public void execute_longStoredTagCannotBypassAdditionValidation() {
        var model = modelWithTags(Set.of());
        CommandException error = assertThrows(CommandException.class, () ->
                command(Tag.fromStoredName("a".repeat(31)), Operation.ADD).execute(model));
        assertEquals(Tag.MESSAGE_CONSTRAINTS, error.getMessage());
        assertTrue(model.getFilteredPersonList().getFirst().getTags().isEmpty());
    }

    @Test
    public void constructor_invalidOperationArguments_rejected() {
        Tag tag = new Tag("exam-prep");
        assertThrows(IllegalArgumentException.class, () -> command(null, Operation.ADD));
        assertThrows(IllegalArgumentException.class, () -> command(null, Operation.REMOVE));
        assertThrows(IllegalArgumentException.class, () -> command(tag, Operation.CLEAR));
        assertThrows(NullPointerException.class, () -> command(tag, null));
        assertThrows(NullPointerException.class, () -> new TagCommand(null, tag, Operation.ADD));
        assertThrows(NullPointerException.class, () -> command(tag, Operation.ADD).execute(null));
    }

    private static TagCommand command(Tag tag, Operation operation) {
        return new TagCommand(Index.fromOneBased(1), tag, operation);
    }

    private static ModelManager modelWithTags(Set<Tag> tags) {
        var model = new ModelManager();
        model.addPerson(new Person("Amy", "123", null, List.of("Math"), "JC 1").withTags(tags));
        return model;
    }
}
