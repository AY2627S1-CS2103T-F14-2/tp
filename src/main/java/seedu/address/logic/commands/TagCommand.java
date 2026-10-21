package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/** Adds or removes tags on one student in the currently displayed list. */
public class TagCommand extends Command {
    public static final String COMMAND_WORD = "tag";
    public static final String UNTAG_COMMAND_WORD = "untag";
    public static final String MESSAGE_USAGE = "Invalid command format. Usage: tag INDEX TAG";
    public static final String MESSAGE_UNTAG_USAGE =
            "Invalid command format. Usage: untag INDEX TAG or untag --all INDEX";
    private static final Logger logger = LogsCenter.getLogger(TagCommand.class);

    /** The supported changes to a student's tags. */
    public enum Operation { ADD, REMOVE, CLEAR }

    private final Index index;
    private final Tag tag;
    private final Operation operation;

    /** Creates a tag change. Only CLEAR accepts a null tag. */
    public TagCommand(Index index, Tag tag, Operation operation) {
        this.index = requireNonNull(index);
        this.operation = requireNonNull(operation);
        if ((operation == Operation.CLEAR) != (tag == null)) {
            throw new IllegalArgumentException("Only clearing all tags takes no tag.");
        }
        this.tag = tag;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayed = model.getFilteredPersonList();
        if (index.getZeroBased() >= displayed.size()) {
            throw new CommandException(DeleteCommand.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        }
        Person student = displayed.get(index.getZeroBased());
        Set<Tag> tags = new HashSet<>(student.getTags());
        String name = student.getName().fullName;
        String feedback;
        switch (operation) {
            case ADD:
                if (tags.contains(tag)) {
                    return warning("Student " + name + " already has tag: " + tag.tagName);
                }
                if (!Tag.isValidTagName(tag.tagName)) {
                    throw new CommandException(Tag.MESSAGE_CONSTRAINTS);
                }
                if (tags.size() >= Tag.MAX_TAGS_PER_STUDENT) {
                    throw new CommandException(Tag.MESSAGE_TAG_LIMIT);
                }
                tags.add(tag);
                feedback = "Tag added to " + name + ": " + tag.tagName;
                break;
            case REMOVE:
                if (!tags.remove(tag)) {
                    return warning("Student " + name + " does not have tag: " + tag.tagName);
                }
                feedback = "Tag removed from " + name + ": " + tag.tagName;
                break;
            case CLEAR:
                if (tags.isEmpty()) {
                    return warning("Student " + name + " already has no tags.");
                }
                tags.clear();
                feedback = "All tags removed from " + name + ".";
                break;
            default:
                throw new AssertionError("Unhandled tag operation: " + operation);
        }
        assert !tags.equals(student.getTags()) : "Unchanged tag sets should return warning feedback";
        Person updated = student.withTags(tags);
        assert student.isSamePerson(updated) : "Tag changes must preserve student identity";
        model.setPerson(student, updated);
        logger.fine("Prepared tag operation " + operation + " at displayed index " + index.getOneBased());
        return new CommandResult(feedback);
    }

    private CommandResult warning(String message) {
        logger.fine("Tag operation made no changes: " + operation);
        return new CommandResult("Warning: " + message);
    }
}
